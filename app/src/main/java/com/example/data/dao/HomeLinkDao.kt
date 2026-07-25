package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AdminConfigEntity
import com.example.data.model.ChatMessageEntity
import com.example.data.model.ClientEntity
import com.example.data.model.NetworkLinkEntity
import com.example.data.model.SearchHistoryEntity
import com.example.data.model.TechnicianLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HomeLinkDao {

    // Network Links Directory
    @Query("SELECT * FROM network_links ORDER BY isSystemDefault DESC, title ASC")
    fun getAllNetworkLinks(): Flow<List<NetworkLinkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNetworkLink(link: NetworkLinkEntity): Long

    @Delete
    suspend fun deleteNetworkLink(link: NetworkLinkEntity)

    @Query("SELECT COUNT(*) FROM network_links")
    suspend fun getNetworkLinkCount(): Int

    // Search History
    @Query("SELECT * FROM search_history ORDER BY timestamp DESC LIMIT 50")
    fun getSearchHistory(): Flow<List<SearchHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchHistory(history: SearchHistoryEntity)

    @Query("DELETE FROM search_history")
    suspend fun clearSearchHistory()

    // Technician Work Logs
    @Query("SELECT * FROM technician_logs ORDER BY timestamp DESC")
    fun getAllTechnicianLogs(): Flow<List<TechnicianLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTechnicianLog(log: TechnicianLogEntity): Long

    @Update
    suspend fun updateTechnicianLog(log: TechnicianLogEntity)

    @Delete
    suspend fun deleteTechnicianLog(log: TechnicianLogEntity)

    // Admin Configurations
    @Query("SELECT configValue FROM admin_configs WHERE configKey = :key LIMIT 1")
    suspend fun getConfigValue(key: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setConfigValue(config: AdminConfigEntity)

    // Registered Clients Directory (By ID Number)
    @Query("SELECT * FROM registered_clients ORDER BY clientIdNumber ASC")
    fun getAllRegisteredClients(): Flow<List<ClientEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRegisteredClient(client: ClientEntity): Long

    @Update
    suspend fun updateRegisteredClient(client: ClientEntity)

    @Delete
    suspend fun deleteRegisteredClient(client: ClientEntity)

    @Query("SELECT * FROM registered_clients WHERE clientIdNumber = :idNumber LIMIT 1")
    suspend fun getClientByIdNumber(idNumber: String): ClientEntity?

    // Customer Support & Field Tech Chat Channel
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllChatMessages(): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMessageEntity): Long

    @Query("DELETE FROM chat_messages")
    suspend fun clearChatHistory()
}

