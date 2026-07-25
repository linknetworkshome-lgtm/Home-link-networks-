package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.database.HomeLinkDatabase
import com.example.data.model.ChatMessageEntity
import com.example.data.model.ClientEntity
import com.example.data.model.NetworkLinkEntity
import com.example.data.model.SearchHistoryEntity
import com.example.data.model.TechnicianLogEntity
import com.example.data.model.UserRole
import com.example.data.repository.HomeLinkRepository
import com.example.data.repository.UrlDiagnosticResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

enum class AppNavSection {
    LOGIN,
    DASHBOARD_SEARCH,
    CLIENT_REGISTRATION,
    SUPPORT_CHAT,
    LINK_DIRECTORY,
    TECHNICIAN_LOGS,
    ADMIN_PANEL,
    WEB_VIEWER
}

class HomeLinkViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository: HomeLinkRepository

    // State Flows
    private val _userRole = MutableStateFlow(UserRole.NONE)
    val userRole: StateFlow<UserRole> = _userRole.asStateFlow()

    private val _currentSection = MutableStateFlow(AppNavSection.LOGIN)
    val currentSection: StateFlow<AppNavSection> = _currentSection.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    private val _urlSearchInput = MutableStateFlow("")
    val urlSearchInput: StateFlow<String> = _urlSearchInput.asStateFlow()

    private val _isAnalyzingUrl = MutableStateFlow(false)
    val isAnalyzingUrl: StateFlow<Boolean> = _isAnalyzingUrl.asStateFlow()

    private val _diagnosticResult = MutableStateFlow<UrlDiagnosticResult?>(null)
    val diagnosticResult: StateFlow<UrlDiagnosticResult?> = _diagnosticResult.asStateFlow()

    private val _selectedWebUrl = MutableStateFlow<String?>(null)
    val selectedWebUrl: StateFlow<String?> = _selectedWebUrl.asStateFlow()

    private val _networkLinks = MutableStateFlow<List<NetworkLinkEntity>>(emptyList())
    val networkLinks: StateFlow<List<NetworkLinkEntity>> = _networkLinks.asStateFlow()

    private val _searchHistory = MutableStateFlow<List<SearchHistoryEntity>>(emptyList())
    val searchHistory: StateFlow<List<SearchHistoryEntity>> = _searchHistory.asStateFlow()

    private val _technicianLogs = MutableStateFlow<List<TechnicianLogEntity>>(emptyList())
    val technicianLogs: StateFlow<List<TechnicianLogEntity>> = _technicianLogs.asStateFlow()

    private val _registeredClients = MutableStateFlow<List<ClientEntity>>(emptyList())
    val registeredClients: StateFlow<List<ClientEntity>> = _registeredClients.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessageEntity>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessageEntity>> = _chatMessages.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    init {
        val database = HomeLinkDatabase.getDatabase(application)
        repository = HomeLinkRepository(database.homeLinkDao())

        viewModelScope.launch {
            repository.allNetworkLinks.collectLatest { _networkLinks.value = it }
        }
        viewModelScope.launch {
            repository.searchHistory.collectLatest { _searchHistory.value = it }
        }
        viewModelScope.launch {
            repository.allTechnicianLogs.collectLatest { _technicianLogs.value = it }
        }
        viewModelScope.launch {
            repository.allClients.collectLatest { _registeredClients.value = it }
        }
        viewModelScope.launch {
            repository.allChatMessages.collectLatest { _chatMessages.value = it }
        }
    }

    fun loginWithPassword(password: String) {
        val cleanPass = password.trim()
        viewModelScope.launch {
            when {
                repository.validateTechnicianPassword(cleanPass) -> {
                    _userRole.value = UserRole.TECHNICIAN
                    _loginError.value = null
                    _currentSection.value = AppNavSection.DASHBOARD_SEARCH
                }
                repository.validateAdminPassword(cleanPass) -> {
                    _userRole.value = UserRole.ADMIN
                    _loginError.value = null
                    _currentSection.value = AppNavSection.DASHBOARD_SEARCH
                }
                else -> {
                    _loginError.value = "Invalid Access Password. Please verify your credentials."
                }
            }
        }
    }

    fun logout() {
        _userRole.value = UserRole.NONE
        _loginError.value = null
        _currentSection.value = AppNavSection.LOGIN
        _diagnosticResult.value = null
        _urlSearchInput.value = ""
    }

    fun setNavSection(section: AppNavSection) {
        // Enforce role-based access control
        if (_userRole.value == UserRole.TECHNICIAN && section == AppNavSection.ADMIN_PANEL) {
            _toastMessage.value = "Access Restricted: Administrator Privileges Required."
            return
        }
        _currentSection.value = section
    }

    fun setUrlSearchInput(input: String) {
        _urlSearchInput.value = input
    }

    fun performUrlDiagnostics(url: String) {
        if (url.isBlank()) return
        _isAnalyzingUrl.value = true
        viewModelScope.launch {
            val roleName = if (_userRole.value == UserRole.ADMIN) "ADMIN" else "TECHNICIAN"
            val result = repository.analyzeAndTestUrl(url, roleName)
            _diagnosticResult.value = result
            _isAnalyzingUrl.value = false
        }
    }

    fun openInInAppBrowser(url: String) {
        var clean = url.trim()
        if (!clean.startsWith("http://") && !clean.startsWith("https://")) {
            clean = "http://$clean"
        }
        _selectedWebUrl.value = clean
        _currentSection.value = AppNavSection.WEB_VIEWER
    }

    fun addCustomNetworkLink(title: String, url: String, category: String, description: String) {
        viewModelScope.launch {
            repository.addNetworkLink(
                NetworkLinkEntity(
                    title = title,
                    url = url,
                    category = category,
                    description = description,
                    createdByRole = if (_userRole.value == UserRole.ADMIN) "ADMIN" else "TECHNICIAN"
                )
            )
            _toastMessage.value = "Link added successfully to directory."
        }
    }

    fun registerClient(
        idNumber: String,
        name: String,
        phone: String,
        email: String,
        siteAddress: String,
        plan: String,
        status: String,
        notes: String
    ) {
        if (idNumber.isBlank() || name.isBlank()) {
            _toastMessage.value = "Client ID Number and Name are required."
            return
        }
        viewModelScope.launch {
            repository.registerClient(
                ClientEntity(
                    clientIdNumber = idNumber.trim(),
                    clientName = name.trim(),
                    contactPhone = phone.trim(),
                    email = email.trim(),
                    siteAddress = siteAddress.trim(),
                    subscriptionPlan = plan,
                    status = status,
                    notes = notes.trim()
                )
            )
            _toastMessage.value = "Client #${idNumber.trim()} registered successfully!"
        }
    }

    fun updateClient(client: ClientEntity) {
        viewModelScope.launch {
            repository.updateClient(client)
            _toastMessage.value = "Client #${client.clientIdNumber} updated."
        }
    }

    fun deleteClient(client: ClientEntity) {
        viewModelScope.launch {
            repository.deleteClient(client)
            _toastMessage.value = "Client #${client.clientIdNumber} removed."
        }
    }

    fun deleteNetworkLink(link: NetworkLinkEntity) {
        viewModelScope.launch {
            repository.deleteNetworkLink(link)
            _toastMessage.value = "Link removed."
        }
    }

    fun submitTechnicianLog(
        clientName: String,
        siteUrl: String,
        equipmentModel: String,
        signalDbm: Int,
        status: String,
        notes: String
    ) {
        viewModelScope.launch {
            val techId = if (_userRole.value == UserRole.ADMIN) "ADMIN-EXEC" else "TECH-001"
            repository.addTechnicianLog(
                TechnicianLogEntity(
                    technicianId = techId,
                    clientName = clientName,
                    siteAddressOrUrl = siteUrl,
                    equipmentModel = equipmentModel,
                    signalStrengthDbm = signalDbm,
                    status = status,
                    notes = notes
                )
            )
            _toastMessage.value = "Service log submitted successfully."
        }
    }

    fun deleteTechnicianLog(log: TechnicianLogEntity) {
        if (_userRole.value != UserRole.ADMIN) {
            _toastMessage.value = "Only Administrator can delete service logs."
            return
        }
        viewModelScope.launch {
            repository.deleteTechnicianLog(log)
            _toastMessage.value = "Log entry deleted."
        }
    }

    fun updateAdminPassword(newPassword: String) {
        if (newPassword.length < 4) {
            _toastMessage.value = "Password must be at least 4 characters long."
            return
        }
        viewModelScope.launch {
            repository.updateAdminPassword(newPassword)
            _toastMessage.value = "Admin password updated successfully!"
        }
    }

    fun clearSearchHistory() {
        viewModelScope.launch {
            repository.clearHistory()
            _toastMessage.value = "Search history cleared."
        }
    }

    fun sendChatMessage(
        text: String,
        relatedClientId: String = "",
        relatedClientName: String = "",
        requestType: String = "GENERAL_INQUIRY"
    ) {
        val cleanText = text.trim()
        if (cleanText.isEmpty()) return

        val currentRoleName = when (_userRole.value) {
            UserRole.ADMIN -> "System Administrator"
            else -> "Field Technician"
        }

        val currentRoleType = when (_userRole.value) {
            UserRole.ADMIN -> "ADMIN"
            else -> "TECHNICIAN"
        }

        viewModelScope.launch {
            repository.sendChatMessage(
                ChatMessageEntity(
                    senderName = currentRoleName,
                    senderRole = currentRoleType,
                    messageText = cleanText,
                    relatedClientIdNumber = relatedClientId,
                    relatedClientName = relatedClientName,
                    requestType = requestType,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    fun clearChatHistory() {
        viewModelScope.launch {
            repository.clearChatHistory()
            _toastMessage.value = "Chat history cleared."
        }
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeLinkViewModel::class.java)) {
                return HomeLinkViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
