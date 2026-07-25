package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import com.example.data.model.UserRole
import com.example.ui.viewmodel.AppNavSection

@Composable
fun HomeLinkBottomNav(
    currentSection: AppNavSection,
    userRole: UserRole,
    onSectionSelected: (AppNavSection) -> Unit
) {
    NavigationBar(
        containerColor = Color(0xFF090014),
        contentColor = Color(0xFFE040FB)
    ) {
        // Tab 1: URL & Link Search Intelligence
        NavigationBarItem(
            selected = currentSection == AppNavSection.DASHBOARD_SEARCH,
            onClick = { onSectionSelected(AppNavSection.DASHBOARD_SEARCH) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "URL Link Search"
                )
            },
            label = {
                Text(
                    text = "URL Search",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.testTag("nav_tab_search")
        )

        // Tab 2: Client Registration Site / Portal by ID Number
        NavigationBarItem(
            selected = currentSection == AppNavSection.CLIENT_REGISTRATION,
            onClick = { onSectionSelected(AppNavSection.CLIENT_REGISTRATION) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Badge,
                    contentDescription = "Register Clients by ID"
                )
            },
            label = {
                Text(
                    text = "Clients",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.testTag("nav_tab_clients")
        )

        // Tab 3: Customer Care & NOC Live Support Chat
        NavigationBarItem(
            selected = currentSection == AppNavSection.SUPPORT_CHAT,
            onClick = { onSectionSelected(AppNavSection.SUPPORT_CHAT) },
            icon = {
                Icon(
                    imageVector = Icons.Default.SupportAgent,
                    contentDescription = "Customer Care Live Chat"
                )
            },
            label = {
                Text(
                    text = "Live Chat",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.testTag("nav_tab_support_chat")
        )

        // Tab 3: Company Directory Links
        NavigationBarItem(
            selected = currentSection == AppNavSection.LINK_DIRECTORY,
            onClick = { onSectionSelected(AppNavSection.LINK_DIRECTORY) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = "Directory Links"
                )
            },
            label = {
                Text(
                    text = "Links",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.testTag("nav_tab_directory")
        )

        // Tab 3: Technician Work Logs
        NavigationBarItem(
            selected = currentSection == AppNavSection.TECHNICIAN_LOGS,
            onClick = { onSectionSelected(AppNavSection.TECHNICIAN_LOGS) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Assignment,
                    contentDescription = "Technician Field Logs"
                )
            },
            label = {
                Text(
                    text = "Field Logs",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.testTag("nav_tab_logs")
        )

        // Tab 4: Admin Management Panel (Available for Admin)
        NavigationBarItem(
            selected = currentSection == AppNavSection.ADMIN_PANEL,
            onClick = { onSectionSelected(AppNavSection.ADMIN_PANEL) },
            icon = {
                Icon(
                    imageVector = Icons.Default.AdminPanelSettings,
                    contentDescription = "Admin Operations"
                )
            },
            label = {
                Text(
                    text = if (userRole == UserRole.ADMIN) "Admin" else "Admin Lock",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.testTag("nav_tab_admin")
        )
    }
}
