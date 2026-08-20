package com.example.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ClosedTrade
import com.example.model.TradePosition
import com.example.model.TradeType
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PortfolioScreen(
    balance: Double,
    investedAmount: Double,
    marginUsed: Double,
    positions: List<TradePosition>,
    closedTrades: List<ClosedTrade>,
    onDepositClick: () -> Unit,
    onWithdrawClick: () -> Unit,
    onExploreMarketsClick: () -> Unit,
    onClosePosition: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedPortfolioTab by remember { mutableStateOf(0) } // 0: Open Positions, 1: Realized Closed Trades
    val totalUnrealizedPnl = positions.sumOf { it.pnl }
    val totalPortfolioValue = balance + investedAmount + totalUnrealizedPnl
    val isTotalPnlPos = totalUnrealizedPnl >= 0

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Portfolio Valuation Card
        item {
            Surface(
                color = SurfaceElevated,
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("portfolio_summary_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Net Account Equity",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = String.format(Locale.US, "$%,.2f", totalPortfolioValue),
                        color = TextPrimary,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Unrealized P&L: ",
                                color = TextSecondary,
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "${if (isTotalPnlPos) "+" else ""}${String.format(Locale.US, "$%,.2f", totalUnrealizedPnl)}",
                                color = if (isTotalPnlPos) TradingGreen else TradingRed,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Surface(
                            color = if (isTotalPnlPos) TradingGreenBg else TradingRedBg,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "${if (isTotalPnlPos) "+" else ""}${String.format(Locale.US, "%.2f", if (investedAmount > 0) (totalUnrealizedPnl / investedAmount) * 100.0 else 0.0)}%",
                                color = if (isTotalPnlPos) TradingGreen else TradingRed,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Balances row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = BackgroundDark,
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Available Cash", color = TextSecondary, fontSize = 11.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = String.format(Locale.US, "$%,.2f", balance),
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = BackgroundDark,
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Margin Used", color = TextSecondary, fontSize = 11.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = String.format(Locale.US, "$%,.2f", marginUsed),
                                    color = AccentCyan,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Deposit & Withdraw Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onDepositClick,
                            colors = ButtonDefaults.buttonColors(containerColor = TradingGreen, contentColor = Color.White),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Deposit", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = onWithdrawClick,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.ArrowUpward, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Withdraw", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Sub Tabs: Open Positions vs Closed History
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceElevated, RoundedCornerShape(12.dp))
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (selectedPortfolioTab == 0) AccentTeal else Color.Transparent)
                        .clickable { selectedPortfolioTab = 0 }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Open Positions (${positions.size})",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (selectedPortfolioTab == 0) BackgroundDark else TextSecondary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (selectedPortfolioTab == 1) AccentTeal else Color.Transparent)
                        .clickable { selectedPortfolioTab = 1 }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Closed Ledger (${closedTrades.size})",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (selectedPortfolioTab == 1) BackgroundDark else TextSecondary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (selectedPortfolioTab == 0) {
            if (positions.isEmpty()) {
                item {
                    EmptyPortfolioCard(onExploreMarkets = onExploreMarketsClick)
                }
            } else {
                items(positions, key = { it.id }) { position ->
                    PositionCard(
                        position = position,
                        onClose = { onClosePosition(position.id) }
                    )
                }
            }
        } else {
            if (closedTrades.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "No realized closed trades yet", color = TextSecondary)
                    }
                }
            } else {
                items(closedTrades, key = { it.id }) { closedTrade ->
                    ClosedTradeItemCard(trade = closedTrade)
                }
            }
        }
    }
}

@Composable
fun PositionCard(
    position: TradePosition,
    onClose: () -> Unit
) {
    val isBuy = position.type == TradeType.BUY
    val typeColor = if (isBuy) TradingGreen else TradingRed
    val isProfit = position.pnl >= 0

    Surface(
        color = SurfaceElevated,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = if (isBuy) TradingGreenBg else TradingRedBg,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = position.type.name,
                            style = MaterialTheme.typography.labelSmall,
                            color = typeColor,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = position.assetSymbol,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = BackgroundDark,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "${position.leverage}x Lev",
                            style = MaterialTheme.typography.labelSmall,
                            color = AccentCyan,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${if (isProfit) "+" else ""}${String.format(Locale.US, "$%,.2f", position.pnl)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isProfit) TradingGreen else TradingRed,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${if (isProfit) "+" else ""}${String.format(Locale.US, "%.2f", position.pnlPercent)}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isProfit) TradingGreen else TradingRed,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Entry Price", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    Text(text = "$${String.format("%.2f", position.entryPrice)}", style = MaterialTheme.typography.bodySmall, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                }
                Column {
                    Text(text = "Current Price", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    Text(text = "$${String.format("%.2f", position.currentPrice)}", style = MaterialTheme.typography.bodySmall, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                }
                Column {
                    Text(text = "Size (Units)", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    Text(text = "${position.units}", style = MaterialTheme.typography.bodySmall, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                }
            }

            if (position.stopLoss != null || position.takeProfit != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BackgroundDark, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (position.stopLoss != null) {
                        Text(text = "Stop Loss: $${String.format("%.2f", position.stopLoss)}", style = MaterialTheme.typography.labelSmall, color = TradingRed)
                    }
                    if (position.takeProfit != null) {
                        Text(text = "Take Profit: $${String.format("%.2f", position.takeProfit)}", style = MaterialTheme.typography.labelSmall, color = TradingGreen)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onClose,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(34.dp)
            ) {
                Text(text = "Close Position", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ClosedTradeItemCard(trade: ClosedTrade) {
    val isProfit = trade.realizedPnl >= 0
    val dateFormat = remember { SimpleDateFormat("MM/dd HH:mm", Locale.getDefault()) }

    Surface(
        color = SurfaceElevated,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${trade.type.name} ${trade.assetSymbol}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "(${trade.units} units)",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Entry: $${String.format("%.2f", trade.entryPrice)} → Exit: $${String.format("%.2f", trade.exitPrice)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${if (isProfit) "+" else ""}$${String.format("%.2f", trade.realizedPnl)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isProfit) TradingGreen else TradingRed,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = dateFormat.format(Date(trade.closeTime)),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun EmptyPortfolioCard(onExploreMarkets: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(60.dp),
            shape = CircleShape,
            color = SurfaceElevated
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(imageVector = Icons.Default.ShowChart, contentDescription = null, tint = AccentTeal, modifier = Modifier.size(30.dp))
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "No Open Positions",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Execute live orders across global stocks, crypto, commodities, and indices.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onExploreMarkets,
            colors = ButtonDefaults.buttonColors(containerColor = AccentTeal, contentColor = BackgroundDark),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "Discover Markets", fontWeight = FontWeight.Bold)
        }
    }
}
