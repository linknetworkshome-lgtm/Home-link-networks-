package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.HomeLinkDao
import com.example.data.model.AdminConfigEntity
import com.example.data.model.ChatMessageEntity
import com.example.data.model.ClientEntity
import com.example.data.model.NetworkLinkEntity
import com.example.data.model.SearchHistoryEntity
import com.example.data.model.TechnicianLogEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        NetworkLinkEntity::class,
        SearchHistoryEntity::class,
        TechnicianLogEntity::class,
        AdminConfigEntity::class,
        ClientEntity::class,
        ChatMessageEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class HomeLinkDatabase : RoomDatabase() {

    abstract fun homeLinkDao(): HomeLinkDao

    companion object {
        @Volatile
        private var INSTANCE: HomeLinkDatabase? = null

        fun getDatabase(context: Context): HomeLinkDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HomeLinkDatabase::class.java,
                    "homelink_network_db"
                )
                    .addCallback(DatabaseCallback(context))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val context: Context
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database.homeLinkDao())
                    }
                }
            }

            private suspend fun populateInitialData(dao: HomeLinkDao) {
                // Initial Default Network Directory Links
                val defaultLinks = listOf(
                    NetworkLinkEntity(
                        title = "Primary Gateway Portal",
                        url = "http://192.168.1.1",
                        category = "Gateway Portal",
                        description = "Standard HomeLink router setup and gateway control panel",
                        isSystemDefault = true
                    ),
                    NetworkLinkEntity(
                        title = "Secondary Router Admin",
                        url = "http://192.168.0.1",
                        category = "Gateway Portal",
                        description = "Alternative subnet gateway interface",
                        isSystemDefault = true
                    ),
                    NetworkLinkEntity(
                        title = "Fiber / Modem ONT Management",
                        url = "http://192.168.100.1",
                        category = "Gateway Portal",
                        description = "Fiber optical network termination diagnostics",
                        isSystemDefault = true
                    ),
                    NetworkLinkEntity(
                        title = "HomeLink Central Cloud Portal",
                        url = "https://homelink.network/admin/status",
                        category = "Internal Support",
                        description = "Main corporate status and provisioning dashboard",
                        isSystemDefault = true
                    ),
                    NetworkLinkEntity(
                        title = "Network Speed & Ping Tester",
                        url = "https://speed.homelink.network",
                        category = "Diagnostic Tool",
                        description = "Bandwidth latency and packet loss verification",
                        isSystemDefault = true
                    ),
                    NetworkLinkEntity(
                        title = "Public DNS Checker (Cloudflare)",
                        url = "https://1.1.1.1",
                        category = "Diagnostic Tool",
                        description = "Verify external WAN DNS connectivity",
                        isSystemDefault = true
                    ),
                    NetworkLinkEntity(
                        title = "Google Public DNS",
                        url = "https://8.8.8.8",
                        category = "Diagnostic Tool",
                        description = "Secondary public IP check",
                        isSystemDefault = true
                    )
                )

                for (link in defaultLinks) {
                    dao.insertNetworkLink(link)
                }

                // Initial sample technician work log for demonstration
                dao.insertTechnicianLog(
                    TechnicianLogEntity(
                        technicianId = "TECH-001",
                        clientName = "Horizon Corporate Office",
                        siteAddressOrUrl = "http://192.168.1.10",
                        equipmentModel = "HomeLink WiFi 6 Pro Router",
                        signalStrengthDbm = -58,
                        status = "RESOLVED",
                        notes = "Configured VLAN 10 and verified 1Gbps uplink. Signal optimal."
                    )
                )

                // Initial sample registered clients identified by ID Number
                val defaultClients = listOf(
                    ClientEntity(
                        clientIdNumber = "1001",
                        clientName = "Apex Financial Systems",
                        contactPhone = "+1 (555) 234-5678",
                        email = "tech@apexfinancial.com",
                        siteAddress = "http://192.168.1.10",
                        subscriptionPlan = "Enterprise Fiber 1Gbps",
                        status = "ONLINE",
                        notes = "Primary corporate headquarters gateway. VLAN 10 configured."
                    ),
                    ClientEntity(
                        clientIdNumber = "1002",
                        clientName = "Metro Health Medical Center",
                        contactPhone = "+1 (555) 876-5432",
                        email = "admin@metrohealth.org",
                        siteAddress = "http://192.168.100.15",
                        subscriptionPlan = "Pro Business 500Mbps",
                        status = "ONLINE",
                        notes = "Emergency ward telemetry router with failover cellular link."
                    ),
                    ClientEntity(
                        clientIdNumber = "1003",
                        clientName = "Skyline Logistics Hub",
                        contactPhone = "+1 (555) 432-1098",
                        email = "ops@skylinelogistics.net",
                        siteAddress = "http://192.168.0.22",
                        subscriptionPlan = "HomeLink Gigabit Pro",
                        status = "PENDING",
                        notes = "Warehouse terminal installation pending signal calibration."
                    ),
                    ClientEntity(
                        clientIdNumber = "1004",
                        clientName = "Nexus Media Studios",
                        contactPhone = "+1 (555) 901-2345",
                        email = "support@nexusstudios.io",
                        siteAddress = "http://192.168.1.45",
                        subscriptionPlan = "Enterprise Ultra 2Gbps",
                        status = "ONLINE",
                        notes = "High bandwidth fiber line for live 4K video rendering streams."
                    )
                )

                for (client in defaultClients) {
                    dao.insertRegisteredClient(client)
                }

                // Initial Field Tech & Admin Direct Chat Channel
                val initialChat = listOf(
                    ChatMessageEntity(
                        senderName = "System Administrator",
                        senderRole = "ADMIN",
                        messageText = "Field Technicians: Use this direct chat channel to communicate with Admin for client site provisioning and internet connection authorizations.",
                        requestType = "SYSTEM_ANNOUNCEMENT"
                    )
                )

                for (msg in initialChat) {
                    dao.insertChatMessage(msg)
                }

                // Initial admin config setting (default admin password homelink002)
                dao.setConfigValue(
                    AdminConfigEntity(
                        configKey = "ADMIN_PASSWORD",
                        configValue = "homelink002"
                    )
                )
            }
        }
    }
}
