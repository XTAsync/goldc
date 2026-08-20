package com.example.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.BrokerConfiguration
import com.example.model.BrokerType
import com.example.ui.theme.*

@Composable
fun BrokerSettingsDialog(
    currentConfig: BrokerConfiguration,
    onSaveConfig: (BrokerConfiguration) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedBroker by remember { mutableStateOf(currentConfig.selectedBroker) }
    var isLiveAccount by remember { mutableStateOf(currentConfig.isLiveAccount) }
    var apiKey by remember { mutableStateOf(currentConfig.apiKey) }
    var apiSecret by remember { mutableStateOf(currentConfig.apiSecret) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            color = SurfaceElevated,
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Brokerage Adapter",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Multi-Broker Routing & DMA Execution",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Sandbox vs Live Mode Switcher
                Surface(
                    color = BackgroundDark,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isLiveAccount) "Real Live Brokerage" else "Paper Sandbox Simulator",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isLiveAccount) TradingGreen else AccentCyan,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isLiveAccount) "Real capital execution" else "Simulated fills & real market data",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                        }

                        Switch(
                            checked = isLiveAccount,
                            onCheckedChange = { isLiveAccount = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = TradingGreen,
                                checkedTrackColor = TradingGreen.copy(alpha = 0.3f),
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = BorderDark
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Select Execution Gateway",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Broker Options
                BrokerType.values().forEach { broker ->
                    val isSelected = selectedBroker == broker
                    Surface(
                        color = if (isSelected) AccentTeal.copy(alpha = 0.12f) else BackgroundDark,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) AccentTeal else BorderDark
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { selectedBroker = broker }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = broker.displayName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isSelected) AccentTeal else TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = broker.badge,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(TradingGreen)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${broker.latencyMs}ms",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // API Key input
                Text(
                    text = "API Key / Client ID",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                TextField(
                    value = apiKey,
                    onValueChange = { apiKey = it },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = BackgroundDark,
                        unfocusedContainerColor = BackgroundDark,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BorderDark, RoundedCornerShape(10.dp))
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        onSaveConfig(
                            currentConfig.copy(
                                selectedBroker = selectedBroker,
                                isLiveAccount = isLiveAccount,
                                apiKey = apiKey,
                                apiSecret = apiSecret,
                                serverPingMs = selectedBroker.latencyMs
                            )
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentTeal, contentColor = BackgroundDark),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Connect & Apply Adapter", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
