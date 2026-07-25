package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.TechnicianLogEntity
import com.example.data.model.UserRole
import com.example.ui.components.HomeLinkBackground
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TechnicianLogsScreen(
    logs: List<TechnicianLogEntity>,
    userRole: UserRole,
    onSubmitLog: (
        clientName: String,
        siteUrl: String,
        equipmentModel: String,
        signalDbm: Int,
        status: String,
        notes: String
    ) -> Unit,
    onDeleteLog: (TechnicianLogEntity) -> Unit
) {
    var isFormExpanded by remember { mutableStateOf(false) }

    var clientName by remember { mutableStateOf("") }
    var siteUrl by remember { mutableStateOf("") }
    var equipmentModel by remember { mutableStateOf("HomeLink WiFi 6 Router") }
    var signalDbm by remember { mutableFloatStateOf(-60f) }
    var selectedStatus by remember { mutableStateOf("RESOLVED") }
    var notes by remember { mutableStateOf("") }

    Scaffold(
        containerColor = Color.Transparent
    ) { padding ->
        HomeLinkBackground {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Header Title Card
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape),
                            color = Color.White
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_homelink_logo),
                                contentDescription = "Logo",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.padding(2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Technician Field Service Logs",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Log site setups, signal strength, and maintenance tickets",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Add New Log Form Expandable Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .padding(4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Engineering,
                                        contentDescription = "New Log",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "New Service Work Log Entry",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                IconButton(onClick = { isFormExpanded = !isFormExpanded }) {
                                    Icon(
                                        imageVector = if (isFormExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                        contentDescription = "Expand Form"
                                    )
                                }
                            }

                            AnimatedVisibility(visible = isFormExpanded) {
                                Column(
                                    modifier = Modifier.padding(top = 12.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    OutlinedTextField(
                                        value = clientName,
                                        onValueChange = { clientName = it },
                                        label = { Text("Client Name / Site Title") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    OutlinedTextField(
                                        value = siteUrl,
                                        onValueChange = { siteUrl = it },
                                        label = { Text("Configured IP / URL Address") },
                                        placeholder = { Text("192.168.1.1") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    OutlinedTextField(
                                        value = equipmentModel,
                                        onValueChange = { equipmentModel = it },
                                        label = { Text("Equipment / Router Model") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "Signal Strength: ${signalDbm.toInt()} dBm",
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                            val qualityText = when {
                                                signalDbm > -50 -> "Excellent (-30 to -50)"
                                                signalDbm > -65 -> "Good (-51 to -65)"
                                                signalDbm > -75 -> "Fair (-66 to -75)"
                                                else -> "Poor (< -75)"
                                            }
                                            Text(
                                                text = qualityText,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }

                                        Slider(
                                            value = signalDbm,
                                            onValueChange = { signalDbm = it },
                                            valueRange = -90f..-30f
                                        )
                                    }

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        StatusChip(
                                            text = "RESOLVED",
                                            selected = selectedStatus == "RESOLVED",
                                            onClick = { selectedStatus = "RESOLVED" },
                                            modifier = Modifier.weight(1f)
                                        )
                                        StatusChip(
                                            text = "IN_PROGRESS",
                                            selected = selectedStatus == "IN_PROGRESS",
                                            onClick = { selectedStatus = "IN_PROGRESS" },
                                            modifier = Modifier.weight(1f)
                                        )
                                        StatusChip(
                                            text = "ESCALATED",
                                            selected = selectedStatus == "ESCALATED",
                                            onClick = { selectedStatus = "ESCALATED" },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }

                                    OutlinedTextField(
                                        value = notes,
                                        onValueChange = { notes = it },
                                        label = { Text("Technician Service Notes") },
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Button(
                                        onClick = {
                                            if (clientName.isNotBlank()) {
                                                onSubmitLog(
                                                    clientName,
                                                    siteUrl,
                                                    equipmentModel,
                                                    signalDbm.toInt(),
                                                    selectedStatus,
                                                    notes
                                                )
                                                clientName = ""
                                                siteUrl = ""
                                                notes = ""
                                                isFormExpanded = false
                                            }
                                        },
                                        enabled = clientName.isNotBlank(),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp)
                                            .testTag("submit_work_log_button")
                                    ) {
                                        Text("SUBMIT WORK LOG ENTRY", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Recent Technician Logs Title
                item {
                    Text(
                        text = "Submitted Field Tickets & Activity (${logs.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                if (logs.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                        ) {
                            Text(
                                text = "No service work logs submitted yet. Click 'New Service Work Log Entry' above to add field records.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                } else {
                    items(logs, key = { it.id }) { logItem ->
                        TechnicianLogCardItem(
                            log = logItem,
                            userRole = userRole,
                            onDelete = { onDeleteLog(logItem) }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
        ),
        modifier = modifier
    ) {
        Text(text = text, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun TechnicianLogCardItem(
    log: TechnicianLogEntity,
    userRole: UserRole,
    onDelete: () -> Unit
) {
    val df = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    val dateStr = df.format(Date(log.timestamp))

    val (statusColor, statusIcon) = when (log.status) {
        "RESOLVED" -> Pair(Color(0xFF00E676), Icons.Default.CheckCircle)
        "IN_PROGRESS" -> Pair(Color(0xFFFFAB00), Icons.Default.Pending)
        else -> Pair(Color(0xFFFF5252), Icons.Default.ReportProblem)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = statusIcon,
                        contentDescription = log.status,
                        tint = statusColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = log.status,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = statusColor
                    )
                }

                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = log.clientName,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            if (log.siteAddressOrUrl.isNotBlank()) {
                Text(
                    text = "Config IP: ${log.siteAddressOrUrl}",
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.SignalCellularAlt,
                    contentDescription = "Signal",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${log.equipmentModel} (${log.signalStrengthDbm} dBm)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (log.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Notes: ${log.notes}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (userRole == UserRole.ADMIN) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Log",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}
