package com.example.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VerificationStatus
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
    verificationStatus: VerificationStatus,
    unreadInboxCount: Int,
    isBiometricEnabled: Boolean = true,
    brokerName: String = "Golden Compass Direct DMA",
    isLiveAccount: Boolean = false,
    onInboxClick: () -> Unit,
    onGetHelpClick: () -> Unit,
    onStartOnboarding: () -> Unit,
    onDepositClick: () -> Unit,
    onWithdrawClick: () -> Unit,
    onBrokerSettingsClick: () -> Unit,
    onSecurityClick: () -> Unit = {},
    onLockAppClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // User Profile Header
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_header_card"),
                shape = RoundedCornerShape(20.dp),
                color = SurfaceElevated,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // User Avatar
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(BackgroundDark)
                                .border(2.dp, AccentTeal, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "RC",
                                color = AccentTeal,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Ross Claude",
                                    color = TextPrimary,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Verified",
                                    tint = AccentTeal,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Institutional Account #RC-948201",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Inbox & Help Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = BackgroundDark,
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clickable { onInboxClick() }
                                .testTag("inbox_button")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Notifications, contentDescription = "Inbox", tint = TextPrimary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Alerts & Inbox", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                if (unreadInboxCount > 0) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(18.dp)
                                            .clip(CircleShape)
                                            .background(TradingGreen),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "$unreadInboxCount",
                                            color = BackgroundDark,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = BackgroundDark,
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clickable { onGetHelpClick() }
                                .testTag("get_help_button")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.HelpOutline, contentDescription = "Get help", tint = TextPrimary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Live Support", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }

        // Brokerage Gateway Section
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = SurfaceElevated,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
            ) {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    ProfileMenuRow(
                        title = "Brokerage Gateway Adapter",
                        icon = Icons.Default.Hub,
                        tagText = if (isLiveAccount) "LIVE: $brokerName" else "SANDBOX: $brokerName",
                        tagColor = if (isLiveAccount) TradingGreen else AccentCyan,
                        onClick = onBrokerSettingsClick
                    )
                    ProfileDivider()

                    ProfileMenuRow(
                        title = "Deposit Funds (ACH, Wire, Crypto)",
                        icon = Icons.Default.Payment,
                        tagText = "Instant",
                        tagColor = TradingGreen,
                        onClick = onDepositClick
                    )
                    ProfileDivider()

                    ProfileMenuRow(
                        title = "Withdraw Capital",
                        icon = Icons.Default.AccountBalance,
                        onClick = onWithdrawClick
                    )
                }
            }
        }

        // Security & Verification Section
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = SurfaceElevated,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
            ) {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    ProfileMenuRow(
                        title = "Biometric Security & Passcode",
                        icon = Icons.Default.Fingerprint,
                        tagText = if (isBiometricEnabled) "Active" else "Off",
                        tagColor = if (isBiometricEnabled) TradingGreen else TextSecondary,
                        onClick = onSecurityClick
                    )
                    ProfileDivider()

                    ProfileMenuRow(
                        title = "Lock App Now",
                        icon = Icons.Default.Lock,
                        onClick = onLockAppClick
                    )
                    ProfileDivider()

                    ProfileMenuRow(
                        title = "Tier 3 Pro Trader Verification",
                        icon = Icons.Default.VerifiedUser,
                        tagText = if (verificationStatus == VerificationStatus.VERIFIED) "Completed" else "Pending",
                        tagColor = if (verificationStatus == VerificationStatus.VERIFIED) AccentTeal else TradingRed,
                        onClick = onStartOnboarding
                    )
                }
            }
        }

        // Legal, Info & Compliance
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = SurfaceElevated,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
            ) {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    ProfileMenuRow(
                        title = "Legal Disclosures & Risk Notice",
                        icon = Icons.Default.Policy,
                        onClick = {}
                    )
                    ProfileDivider()

                    ProfileMenuRow(
                        title = "Platform Version",
                        icon = Icons.Default.Info,
                        tagText = "v4.2.0-PRO",
                        tagColor = TextMuted,
                        onClick = {}
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileMenuRow(
    title: String,
    icon: ImageVector,
    tagText: String? = null,
    tagColor: Color = TextSecondary,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = title, tint = TextSecondary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(14.dp))
            Text(text = title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (tagText != null) {
                Surface(
                    color = tagColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = tagText,
                        color = tagColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun ProfileDivider() {
    HorizontalDivider(color = BorderDark, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
}
