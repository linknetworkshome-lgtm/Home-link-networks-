package com.example.ui.screens

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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Wifi
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatMessageEntity
import com.example.data.model.ClientEntity
import com.example.ui.components.HomeLinkBackground
import com.example.ui.theme.HomeLinkCyanAccent
import com.example.ui.theme.HomeLinkEmeraldOnline
import com.example.ui.theme.HomeLinkErrorRed
import com.example.ui.theme.HomeLinkPurplePrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SupportChatScreen(
    chatMessages: List<ChatMessageEntity>,
    clients: List<ClientEntity>,
    onSendMessage: (
        text: String,
        relatedClientId: String,
        relatedClientName: String,
        requestType: String
    ) -> Unit,
    onClearChatHistory: () -> Unit
) {
    var messageText by remember { mutableStateOf("") }
    var selectedClient by remember { mutableStateOf<ClientEntity?>(null) }
    var clientDropdownExpanded by remember { mutableStateOf(false) }
    var selectedRequestType by remember { mutableStateOf("PROVISION_REQUEST") }

    val listState = rememberLazyListState()

    // Auto-scroll to bottom on new chat messages
    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    HomeLinkBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            // Header Card - Support Desk Status
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF38006B)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SupportAgent,
                                    contentDescription = "Customer Care Chat",
                                    tint = HomeLinkCyanAccent,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Technician & Admin Dispatch Chat",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(HomeLinkEmeraldOnline)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Live Internal Channel • Field Techs & Admin Direct",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = HomeLinkEmeraldOnline
                                    )
                                }
                            }
                        }

                        IconButton(onClick = onClearChatHistory) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Clear Chat History",
                                tint = HomeLinkErrorRed.copy(alpha = 0.8f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Client Selector Dropdown for Field Techs
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { clientDropdownExpanded = true }
                                .testTag("dropdown_select_chat_client"),
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF280C42),
                            border = androidx.compose.foundation.BorderStroke(1.dp, HomeLinkPurplePrimary.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Badge,
                                        contentDescription = "Client Badge",
                                        tint = HomeLinkCyanAccent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = selectedClient?.let { "Attached Client: #${it.clientIdNumber} - ${it.clientName}" }
                                            ?: "Select Client Site to Connect (Optional)",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                        color = Color.White
                                    )
                                }
                                Text(
                                    text = if (clientDropdownExpanded) "▲" else "▼",
                                    color = HomeLinkCyanAccent,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = clientDropdownExpanded,
                            onDismissRequest = { clientDropdownExpanded = false },
                            modifier = Modifier.background(Color(0xFF1C0A36))
                        ) {
                            DropdownMenuItem(
                                text = { Text("None (General Inquiry)", color = Color.LightGray) },
                                onClick = {
                                    selectedClient = null
                                    clientDropdownExpanded = false
                                }
                            )
                            clients.forEach { client ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "#${client.clientIdNumber} - ${client.clientName} (${client.status})",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    },
                                    onClick = {
                                        selectedClient = client
                                        clientDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Message Stream List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = chatMessages,
                    key = { it.id }
                ) { msg ->
                    ChatMessageBubble(msg = msg)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Request Action Chips
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                FilterChip(
                    selected = selectedRequestType == "PROVISION_REQUEST",
                    onClick = {
                        selectedRequestType = "PROVISION_REQUEST"
                        val clientInfo = selectedClient?.let { "for Client #${it.clientIdNumber} (${it.clientName})" } ?: ""
                        messageText = "Admin: Please authorize and activate company internet connection $clientInfo."
                    },
                    label = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Wifi, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Request Connection", fontSize = 11.sp)
                        }
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF38006B),
                        selectedLabelColor = Color.White
                    )
                )

                FilterChip(
                    selected = selectedRequestType == "SIGNAL_CHECK",
                    onClick = {
                        selectedRequestType = "SIGNAL_CHECK"
                        val clientInfo = selectedClient?.let { "for Client #${it.clientIdNumber} (${it.clientName})" } ?: ""
                        messageText = "Admin: Please check optical line signal and gateway speed $clientInfo."
                    },
                    label = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Check Signal", fontSize = 11.sp)
                        }
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF38006B),
                        selectedLabelColor = Color.White
                    )
                )

                FilterChip(
                    selected = selectedRequestType == "GENERAL_INQUIRY",
                    onClick = {
                        selectedRequestType = "GENERAL_INQUIRY"
                    },
                    label = {
                        Text("General Question", fontSize = 11.sp)
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF38006B),
                        selectedLabelColor = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Text Input Box & Send Button
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        placeholder = { Text("Type message to Customer Care...") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_chat_message"),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                if (messageText.isNotBlank()) {
                                    onSendMessage(
                                        messageText,
                                        selectedClient?.clientIdNumber ?: "",
                                        selectedClient?.clientName ?: "",
                                        selectedRequestType
                                    )
                                    messageText = ""
                                }
                            }
                        ),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = HomeLinkPurplePrimary,
                            unfocusedBorderColor = Color(0xFF38006B),
                            focusedContainerColor = Color(0xFF0F0022),
                            unfocusedContainerColor = Color(0xFF0F0022),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (messageText.isNotBlank()) {
                                onSendMessage(
                                    messageText,
                                    selectedClient?.clientIdNumber ?: "",
                                    selectedClient?.clientName ?: "",
                                    selectedRequestType
                                )
                                messageText = ""
                            }
                        },
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(HomeLinkPurplePrimary)
                            .testTag("btn_send_chat_message")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send Chat Message",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatMessageBubble(msg: ChatMessageEntity) {
    val isTechnician = msg.senderRole.equals("TECHNICIAN", ignoreCase = true)
    val timeFormatted = remember(msg.timestamp) {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        sdf.format(Date(msg.timestamp))
    }

    val bubbleColor = if (isTechnician) Color(0xFF38006B) else Color(0xFF1A0033)
    val borderColor = if (isTechnician) HomeLinkPurplePrimary else HomeLinkCyanAccent
    val textColor = Color.White

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isTechnician) Alignment.End else Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            Icon(
                imageVector = if (isTechnician) Icons.Default.Person else Icons.Default.SupportAgent,
                contentDescription = null,
                tint = if (isTechnician) HomeLinkPurplePrimary else HomeLinkCyanAccent,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "${msg.senderName} • $timeFormatted",
                style = MaterialTheme.typography.labelSmall,
                color = Color.LightGray
            )
        }

        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isTechnician) 16.dp else 4.dp,
                bottomEnd = if (isTechnician) 4.dp else 16.dp
            ),
            color = bubbleColor,
            border = androidx.compose.foundation.BorderStroke(1.dp, borderColor.copy(alpha = 0.6f)),
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                if (msg.relatedClientIdNumber.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = HomeLinkCyanAccent.copy(alpha = 0.15f),
                        modifier = Modifier.padding(bottom = 6.dp)
                    ) {
                        Text(
                            text = "CLIENT REQ #${msg.relatedClientIdNumber} - ${msg.relatedClientName}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = HomeLinkCyanAccent,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = msg.messageText,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                    color = textColor
                )
            }
        }
    }
}
