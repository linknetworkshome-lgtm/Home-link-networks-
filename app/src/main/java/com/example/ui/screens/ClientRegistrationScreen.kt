package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ClientEntity
import com.example.ui.components.HomeLinkBackground
import com.example.ui.theme.HomeLinkEmeraldOnline
import com.example.ui.theme.HomeLinkErrorRed
import com.example.ui.theme.HomeLinkPurplePrimary
import com.example.ui.theme.HomeLinkWarningAmber

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ClientRegistrationScreen(
    clients: List<ClientEntity>,
    onRegisterClient: (
        idNumber: String,
        name: String,
        phone: String,
        email: String,
        siteAddress: String,
        plan: String,
        status: String,
        notes: String
    ) -> Unit,
    onUpdateClient: (ClientEntity) -> Unit,
    onDeleteClient: (ClientEntity) -> Unit,
    onTestClientDiagnostics: (siteAddress: String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatusFilter by remember { mutableStateOf("ALL") }
    var showRegisterModal by remember { mutableStateOf(false) }
    var editingClient by remember { mutableStateOf<ClientEntity?>(null) }
    var deletingClient by remember { mutableStateOf<ClientEntity?>(null) }

    // Auto-calculate next recommended ID number
    val nextIdNumber = remember(clients) {
        val maxId = clients.mapNotNull { it.clientIdNumber.toLongOrNull() }.maxOrNull() ?: 1000L
        (maxId + 1).toString()
    }

    // Filtered clients list
    val filteredClients = remember(clients, searchQuery, selectedStatusFilter) {
        clients.filter { client ->
            val matchesQuery = searchQuery.isBlank() ||
                    client.clientIdNumber.contains(searchQuery.trim(), ignoreCase = true) ||
                    client.clientName.contains(searchQuery.trim(), ignoreCase = true) ||
                    client.siteAddress.contains(searchQuery.trim(), ignoreCase = true)

            val matchesStatus = when (selectedStatusFilter) {
                "ALL" -> true
                else -> client.status.equals(selectedStatusFilter, ignoreCase = true)
            }

            matchesQuery && matchesStatus
        }
    }

    val totalCount = clients.size
    val onlineCount = clients.count { it.status.equals("ONLINE", ignoreCase = true) }
    val pendingCount = clients.count { it.status.equals("PENDING", ignoreCase = true) }

    HomeLinkBackground {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Title Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(HomeLinkPurplePrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Badge,
                                    contentDescription = "Client Registration Portal",
                                    tint = Color.White,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Client Registration Portal",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Register, assign ID numbers & manage client site accounts",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Metric summary counters
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ClientMetricChip(
                                title = "Total Registered",
                                value = "$totalCount Accounts",
                                color = HomeLinkPurplePrimary,
                                modifier = Modifier.weight(1f)
                            )
                            ClientMetricChip(
                                title = "Active Online",
                                value = "$onlineCount Clients",
                                color = HomeLinkEmeraldOnline,
                                modifier = Modifier.weight(1f)
                            )
                            ClientMetricChip(
                                title = "Next ID Number",
                                value = "#$nextIdNumber",
                                color = Color(0xFF00E5FF),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { showRegisterModal = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("btn_register_new_client"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.White
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Register Client Button"
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Register New Client Account",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }

            // Search Bar & Filter Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Query Registered Client by ID Number or Name",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search ID number (e.g., 1001), Client Name, or IP...") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search icon",
                                    tint = HomeLinkPurplePrimary
                                )
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Clear search query"
                                        )
                                    }
                                }
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_client_id_search"),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = HomeLinkPurplePrimary,
                                unfocusedBorderColor = Color(0xFFD1C4E9),
                                focusedContainerColor = Color(0xFFF8F5FF),
                                unfocusedContainerColor = Color(0xFFF8F5FF)
                            )
                        )

                        // Status Filter Chips
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = selectedStatusFilter == "ALL",
                                onClick = { selectedStatusFilter = "ALL" },
                                label = { Text("All ($totalCount)") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF38006B),
                                    selectedLabelColor = Color.White
                                )
                            )
                            FilterChip(
                                selected = selectedStatusFilter == "ONLINE",
                                onClick = { selectedStatusFilter = "ONLINE" },
                                label = { Text("Online ($onlineCount)") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = HomeLinkEmeraldOnline,
                                    selectedLabelColor = Color.Black
                                )
                            )
                            FilterChip(
                                selected = selectedStatusFilter == "PENDING",
                                onClick = { selectedStatusFilter = "PENDING" },
                                label = { Text("Pending ($pendingCount)") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = HomeLinkWarningAmber,
                                    selectedLabelColor = Color.Black
                                )
                            )
                            FilterChip(
                                selected = selectedStatusFilter == "SUSPENDED",
                                onClick = { selectedStatusFilter = "SUSPENDED" },
                                label = { Text("Suspended") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = HomeLinkErrorRed,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Client Cards List
            if (filteredClients.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.90f)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "No clients found",
                                tint = HomeLinkPurplePrimary,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No Client Records Found",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF38006B)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Try searching a different Client ID Number or register a new client.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        }
                    }
                }
            } else {
                items(
                    items = filteredClients,
                    key = { it.id }
                ) { client ->
                    ClientAccountCard(
                        client = client,
                        onEdit = { editingClient = client },
                        onDelete = { deletingClient = client },
                        onTestDiagnostics = { onTestClientDiagnostics(client.siteAddress) }
                    )
                }
            }
        }
    }

    // Register Client Modal Dialog
    if (showRegisterModal) {
        RegisterClientDialog(
            defaultNextId = nextIdNumber,
            onDismiss = { showRegisterModal = false },
            onConfirm = { idNum, name, phone, email, site, plan, status, notes ->
                onRegisterClient(idNum, name, phone, email, site, plan, status, notes)
                showRegisterModal = false
            }
        )
    }

    // Edit Client Modal Dialog
    editingClient?.let { clientToEdit ->
        EditClientDialog(
            client = clientToEdit,
            onDismiss = { editingClient = null },
            onConfirm = { updatedClient ->
                onUpdateClient(updatedClient)
                editingClient = null
            }
        )
    }

    // Delete Client Confirmation Dialog
    deletingClient?.let { clientToDelete ->
        AlertDialog(
            onDismissRequest = { deletingClient = null },
            title = {
                Text(
                    text = "Confirm Client Deletion",
                    fontWeight = FontWeight.Bold,
                    color = HomeLinkErrorRed
                )
            },
            text = {
                Text("Are you sure you want to remove Client ID #${clientToDelete.clientIdNumber} (${clientToDelete.clientName}) from the portal registry?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteClient(clientToDelete)
                        deletingClient = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = HomeLinkErrorRed)
                ) {
                    Text("Delete Client", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingClient = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ClientMetricChip(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = color
            )
        }
    }
}

@Composable
fun ClientAccountCard(
    client: ClientEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onTestDiagnostics: () -> Unit
) {
    val statusColor = when (client.status.uppercase()) {
        "ONLINE" -> HomeLinkEmeraldOnline
        "PENDING" -> HomeLinkWarningAmber
        "SUSPENDED", "OFFLINE" -> HomeLinkErrorRed
        else -> Color.Gray
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Top Bar: ID Number Badge & Status Chip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Prominent Client ID Number Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF38006B)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Badge,
                            contentDescription = "Client ID Badge",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "CLIENT ID: #${client.clientIdNumber}",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.8.sp
                            ),
                            color = Color.White
                        )
                    }
                }

                // Status Badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = statusColor.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, statusColor)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(statusColor)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = client.status.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = statusColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Client Name & Plan
            Text(
                text = client.clientName,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Speed,
                    contentDescription = "Subscription Plan",
                    tint = HomeLinkPurplePrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Plan: ${client.subscriptionPlan}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = HomeLinkPurplePrimary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Site Address / IP
            if (client.siteAddress.isNotBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Site Address",
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Site IP / Address: ${client.siteAddress}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray
                    )
                }
            }

            // Contact Phone & Email
            if (client.contactPhone.isNotBlank() || client.email.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (client.contactPhone.isNotBlank()) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Phone",
                            tint = Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = client.contactPhone,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.DarkGray
                        )
                    }
                    if (client.contactPhone.isNotBlank() && client.email.isNotBlank()) {
                        Text(text = " • ", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                    if (client.email.isNotBlank()) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email",
                            tint = Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = client.email,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.DarkGray
                        )
                    }
                }
            }

            // Notes
            if (client.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF3E8FF)
                ) {
                    Text(
                        text = "Note: ${client.notes}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF4A148C),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (client.siteAddress.isNotBlank()) {
                    OutlinedButton(
                        onClick = onTestDiagnostics,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = HomeLinkPurplePrimary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Run Ping Diagnostics",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Test Diagnostics", fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Client Details",
                        tint = HomeLinkPurplePrimary
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Client Record",
                        tint = HomeLinkErrorRed
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RegisterClientDialog(
    defaultNextId: String,
    onDismiss: () -> Unit,
    onConfirm: (
        idNumber: String,
        name: String,
        phone: String,
        email: String,
        siteAddress: String,
        plan: String,
        status: String,
        notes: String
    ) -> Unit
) {
    var idNumber by remember { mutableStateOf(defaultNextId) }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var siteAddress by remember { mutableStateOf("") }
    var plan by remember { mutableStateOf("Enterprise Fiber 1Gbps") }
    var status by remember { mutableStateOf("ONLINE") }
    var notes by remember { mutableStateOf("") }

    val planOptions = listOf(
        "Enterprise Fiber 1Gbps",
        "Pro Business 500Mbps",
        "HomeLink Gigabit Pro",
        "Standard Wireless",
        "Custom Dedicated Line"
    )

    val statusOptions = listOf("ONLINE", "PENDING", "OFFLINE", "SUSPENDED")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = HomeLinkPurplePrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Register Client Account", fontWeight = FontWeight.Bold, color = Color(0xFF38006B))
            }
        },
        text = {
            Column(
                modifier = Modifier.padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = idNumber,
                    onValueChange = { idNumber = it },
                    label = { Text("Client ID Number (Unique)") },
                    placeholder = { Text("e.g. 1005") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_register_id_number"),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = HomeLinkPurplePrimary)
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Client Name / Organization *") },
                    placeholder = { Text("e.g. Acro Tech Solutions") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_register_name"),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = HomeLinkPurplePrimary)
                )

                OutlinedTextField(
                    value = siteAddress,
                    onValueChange = { siteAddress = it },
                    label = { Text("Site IP Address or Domain") },
                    placeholder = { Text("e.g. http://192.168.1.50") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = HomeLinkPurplePrimary)
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Contact Phone Number") },
                    placeholder = { Text("+1 (555) 000-0000") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = HomeLinkPurplePrimary)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    placeholder = { Text("client@domain.com") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = HomeLinkPurplePrimary)
                )

                // Plan selection
                Text("Service Plan:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    planOptions.forEach { p ->
                        FilterChip(
                            selected = plan == p,
                            onClick = { plan = p },
                            label = { Text(p, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = HomeLinkPurplePrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                // Initial Status
                Text("Account Status:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    statusOptions.forEach { st ->
                        FilterChip(
                            selected = status == st,
                            onClick = { status = st },
                            label = { Text(st, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF38006B),
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Registration Notes") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = HomeLinkPurplePrimary)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(idNumber, name, phone, email, siteAddress, plan, status, notes)
                },
                enabled = idNumber.isNotBlank() && name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38006B))
            ) {
                Text("Register Client", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun EditClientDialog(
    client: ClientEntity,
    onDismiss: () -> Unit,
    onConfirm: (ClientEntity) -> Unit
) {
    var idNumber by remember { mutableStateOf(client.clientIdNumber) }
    var name by remember { mutableStateOf(client.clientName) }
    var phone by remember { mutableStateOf(client.contactPhone) }
    var email by remember { mutableStateOf(client.email) }
    var siteAddress by remember { mutableStateOf(client.siteAddress) }
    var plan by remember { mutableStateOf(client.subscriptionPlan) }
    var status by remember { mutableStateOf(client.status) }
    var notes by remember { mutableStateOf(client.notes) }

    val statusOptions = listOf("ONLINE", "PENDING", "OFFLINE", "SUSPENDED")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Edit Client Account #${client.clientIdNumber}", fontWeight = FontWeight.Bold, color = Color(0xFF38006B))
        },
        text = {
            Column(
                modifier = Modifier.padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = idNumber,
                    onValueChange = { idNumber = it },
                    label = { Text("Client ID Number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Client Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = siteAddress,
                    onValueChange = { siteAddress = it },
                    label = { Text("Site IP Address / Domain") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Status:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    statusOptions.forEach { st ->
                        FilterChip(
                            selected = status == st,
                            onClick = { status = st },
                            label = { Text(st, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF38006B),
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        client.copy(
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
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38006B))
            ) {
                Text("Save Changes", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
