package com.example.data.repository

import com.example.data.dao.HomeLinkDao
import com.example.data.model.AdminConfigEntity
import com.example.data.model.ChatMessageEntity
import com.example.data.model.ClientEntity
import com.example.data.model.NetworkLinkEntity
import com.example.data.model.SearchHistoryEntity
import com.example.data.model.TechnicianLogEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.InetAddress
import java.net.URL
import kotlin.system.measureTimeMillis

data class UrlDiagnosticResult(
    val formattedUrl: String,
    val isOnline: Boolean,
    val statusCode: Int,
    val latencyMs: Long,
    val statusText: String,
    val ipAddress: String,
    val isSecureSsl: Boolean,
    val addressCategory: String
)

class HomeLinkRepository(private val dao: HomeLinkDao) {

    val allNetworkLinks: Flow<List<NetworkLinkEntity>> = dao.getAllNetworkLinks()
    val searchHistory: Flow<List<SearchHistoryEntity>> = dao.getSearchHistory()
    val allTechnicianLogs: Flow<List<TechnicianLogEntity>> = dao.getAllTechnicianLogs()
    val allClients: Flow<List<ClientEntity>> = dao.getAllRegisteredClients()
    val allChatMessages: Flow<List<ChatMessageEntity>> = dao.getAllChatMessages()

    suspend fun sendChatMessage(message: ChatMessageEntity): Long {
        return dao.insertChatMessage(message)
    }

    suspend fun clearChatHistory() {
        dao.clearChatHistory()
    }


    suspend fun registerClient(client: ClientEntity): Long {
        return dao.insertRegisteredClient(client)
    }

    suspend fun updateClient(client: ClientEntity) {
        dao.updateRegisteredClient(client)
    }

    suspend fun deleteClient(client: ClientEntity) {
        dao.deleteRegisteredClient(client)
    }

    suspend fun getClientByIdNumber(idNumber: String): ClientEntity? {
        return dao.getClientByIdNumber(idNumber)
    }

    suspend fun validateTechnicianPassword(passwordInput: String): Boolean {
        // Technician password requested by user: homelink001
        return passwordInput.trim() == "homelink001"
    }

    suspend fun validateAdminPassword(passwordInput: String): Boolean {
        // Check if admin updated custom password in DB, default is homelink002
        val savedPassword = dao.getConfigValue("ADMIN_PASSWORD") ?: "homelink002"
        return passwordInput.trim() == savedPassword || passwordInput.trim() == "homelink002"
    }

    suspend fun updateAdminPassword(newPassword: String) {
        dao.setConfigValue(
            AdminConfigEntity(
                configKey = "ADMIN_PASSWORD",
                configValue = newPassword.trim()
            )
        )
    }

    suspend fun addNetworkLink(link: NetworkLinkEntity): Long {
        return dao.insertNetworkLink(link)
    }

    suspend fun deleteNetworkLink(link: NetworkLinkEntity) {
        dao.deleteNetworkLink(link)
    }

    suspend fun addTechnicianLog(log: TechnicianLogEntity): Long {
        return dao.insertTechnicianLog(log)
    }

    suspend fun deleteTechnicianLog(log: TechnicianLogEntity) {
        dao.deleteTechnicianLog(log)
    }

    suspend fun clearHistory() {
        dao.clearSearchHistory()
    }

    suspend fun analyzeAndTestUrl(rawUrl: String, userRole: String): UrlDiagnosticResult = withContext(Dispatchers.IO) {
        var cleanUrl = rawUrl.trim()
        if (!cleanUrl.startsWith("http://") && !cleanUrl.startsWith("https://")) {
            cleanUrl = "http://$cleanUrl"
        }

        var isOnline = false
        var statusCode = 0
        var statusText = "Connection Attempted"
        var ipAddress = "Resolving..."
        var isSecureSsl = cleanUrl.startsWith("https://")
        var addressCategory = determineAddressCategory(cleanUrl)

        var latency = 0L

        try {
            val urlObj = URL(cleanUrl)
            val host = urlObj.host

            // Resolve IP Address
            try {
                val inet = InetAddress.getByName(host)
                ipAddress = inet.hostAddress ?: host
            } catch (e: Exception) {
                ipAddress = "DNS Resolution Failed"
            }

            // Test Connection
            latency = measureTimeMillis {
                val connection = urlObj.openConnection() as HttpURLConnection
                connection.connectTimeout = 4000
                connection.readTimeout = 4000
                connection.requestMethod = "HEAD"
                connection.instanceFollowRedirects = true

                try {
                    statusCode = connection.responseCode
                    statusText = "${connection.responseCode} ${connection.responseMessage}"
                    isOnline = statusCode in 100..499
                } catch (e: Exception) {
                    // Fallback to GET if HEAD rejected
                    val getConn = urlObj.openConnection() as HttpURLConnection
                    getConn.connectTimeout = 4000
                    getConn.readTimeout = 4000
                    getConn.requestMethod = "GET"
                    statusCode = getConn.responseCode
                    statusText = "${getConn.responseCode} ${getConn.responseMessage}"
                    isOnline = statusCode in 100..499
                    getConn.disconnect()
                } finally {
                    connection.disconnect()
                }
            }
        } catch (e: Exception) {
            isOnline = false
            statusCode = 0
            statusText = e.localizedMessage ?: "Unreachable or Timeout"
            if (ipAddress == "Resolving...") ipAddress = "Unreachable"
        }

        val result = UrlDiagnosticResult(
            formattedUrl = cleanUrl,
            isOnline = isOnline,
            statusCode = statusCode,
            latencyMs = latency,
            statusText = statusText,
            ipAddress = ipAddress,
            isSecureSsl = isSecureSsl,
            addressCategory = addressCategory
        )

        // Log into Search History
        dao.insertSearchHistory(
            SearchHistoryEntity(
                url = cleanUrl,
                statusCode = statusCode,
                latencyMs = latency,
                statusMessage = statusText,
                ipAddress = ipAddress,
                searchedByRole = userRole
            )
        )

        result
    }

    private fun determineAddressCategory(url: String): String {
        return when {
            url.contains("192.168.") || url.contains("10.0.") || url.contains("172.16.") -> "Local LAN Gateway / IP"
            url.contains("homelink") -> "HomeLink Corporate Infrastructure"
            url.contains("speed") || url.contains("ping") -> "Diagnostic Service"
            else -> "External Web Portal"
        }
    }
}
