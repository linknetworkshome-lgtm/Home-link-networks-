package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole {
    TECHNICIAN,
    ADMIN,
    NONE
}

@Entity(tableName = "network_links")
data class NetworkLinkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val url: String,
    val category: String, // e.g., "Gateway Portal", "Diagnostic Tool", "Internal Support", "Custom"
    val description: String = "",
    val isSystemDefault: Boolean = false,
    val createdByRole: String = "ADMIN",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "search_history")
data class SearchHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val url: String,
    val statusCode: Int = 0,
    val latencyMs: Long = 0,
    val statusMessage: String = "",
    val ipAddress: String = "",
    val searchedByRole: String = "TECHNICIAN",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "technician_logs")
data class TechnicianLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val technicianId: String = "TECH-001",
    val clientName: String,
    val siteAddressOrUrl: String,
    val equipmentModel: String,
    val signalStrengthDbm: Int = -65,
    val status: String, // "RESOLVED", "IN_PROGRESS", "ESCALATED"
    val notes: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "admin_configs")
data class AdminConfigEntity(
    @PrimaryKey val configKey: String,
    val configValue: String
)

@Entity(tableName = "registered_clients")
data class ClientEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val clientIdNumber: String, // Unique Client Numeric ID e.g. "1001"
    val clientName: String,
    val contactPhone: String = "",
    val email: String = "",
    val siteAddress: String = "", // IP address or physical site location
    val subscriptionPlan: String = "Fiber Standard",
    val status: String = "ONLINE", // "ONLINE", "PENDING", "OFFLINE", "SUSPENDED"
    val notes: String = "",
    val registeredAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val senderName: String,
    val senderRole: String, // "TECHNICIAN", "CUSTOMER_CARE", "NOC_SPECIALIST", "SYSTEM"
    val messageText: String,
    val relatedClientIdNumber: String = "",
    val relatedClientName: String = "",
    val requestType: String = "GENERAL_INQUIRY", // "PROVISION_REQUEST", "SIGNAL_CHECK", "ACCOUNT_ACTIVATION", "GENERAL_INQUIRY"
    val timestamp: Long = System.currentTimeMillis()
)

