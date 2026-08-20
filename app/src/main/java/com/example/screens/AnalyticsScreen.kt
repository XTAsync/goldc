package com.example.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ClosedTrade
import com.example.model.PortfolioAnalytics
import com.example.model.TradePosition
import com.example.model.TradeType
import com.example.ui.theme.*

@Composable
fun AnalyticsScreen(
    analytics: PortfolioAnalytics,
    positions: List<TradePosition>,
    closedTrades: List<ClosedTrade>,
    totalEquity: Double,
    marginUsed: Double,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Portfolio Analytics",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Institutional Performance & Risk Metrics",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                Surface(
                    color = AccentTeal.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "ALPHA SCORE: 88/100",
                        style = MaterialTheme.typography.labelSmall,
                        color = AccentTeal,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Key Quantitative KPIs (4-Grid)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AnalyticsKpiCard(
                    title = "Win Rate",
                    value = "${String.format("%.1f", analytics.winRatePercent)}%",
                    subText = "${analytics.winningTrades}W / ${analytics.losingTrades}L",
                    valueColor = TradingGreen,
                    modifier = Modifier.weight(1f)
                )
                AnalyticsKpiCard(
                    title = "Profit Factor",
                    value = "${String.format("%.2f", analytics.profitFactor)}",
                    subText = "Gross P/L Ratio",
                    valueColor = AccentCyan,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AnalyticsKpiCard(
                    title = "Sharpe Ratio",
                    value = "${String.format("%.2f", analytics.sharpeRatio)}",
                    subText = "Risk-Adjusted Return",
                    valueColor = TextPrimary,
                    modifier = Modifier.weight(1f)
                )
                AnalyticsKpiCard(
                    title = "Max Drawdown",
                    value = "-${String.format("%.1f", analytics.maxDrawdownPercent)}%",
                    subText = "Controlled Risk",
                    valueColor = Color(0xFFF59E0B),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Cumulative Return Growth Chart
        item {
            Surface(
                color = SurfaceElevated,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Equity Growth Trajectory",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Total Realized P&L: +$${String.format("%.2f", analytics.totalRealizedPnl)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TradingGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Surface(
                            color = TradingGreenBg,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "+${String.format("%.2f", analytics.totalReturnPercent)}%",
                                style = MaterialTheme.typography.labelSmall,
                                color = TradingGreen,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Canvas Growth Curve
                    val curvePoints = listOf(10f, 15f, 12f, 22f, 28f, 25f, 38f, 45f, 42f, 58f, 65f, 72f, 85f)
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                    ) {
                        val width = size.width
                        val height = size.height
                        val stepX = width / (curvePoints.size - 1)
                        val path = Path()

                        curvePoints.forEachIndexed { index, point ->
                            val x = index * stepX
                            val y = height - (point / 100f * height)
                            if (index == 0) {
                                path.moveTo(x, y)
                            } else {
                                val prevX = (index - 1) * stepX
                                val prevY = height - (curvePoints[index - 1] / 100f * height)
                                val midX = (prevX + x) / 2
                                path.cubicTo(midX, prevY, midX, y, x, y)
                            }
                        }

                        drawPath(
                            path = path,
                            color = AccentTeal,
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Initial Capital", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Text(text = "Current Equity ($${String.format("%.2f", totalEquity)})", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    }
                }
            }
        }

        // Best Trade vs Worst Trade Breakdown
        item {
            Surface(
                color = SurfaceElevated,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Trade Distribution & P&L Spread",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            color = TradingGreenBg,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = "Best Winning Trade", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "+$${String.format("%.2f", analytics.bestTradePnl)}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TradingGreen,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(text = "Avg Win: +$${String.format("%.2f", analytics.avgWinPnl)}", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            }
                        }

                        Surface(
                            color = TradingRedBg,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = "Worst Trade (Stop Loss)", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "$${String.format("%.2f", analytics.worstTradePnl)}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TradingRed,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(text = "Avg Loss: -$${String.format("%.2f", analytics.avgLossPnl)}", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            }
                        }
                    }
                }
            }
        }

        // Asset Allocation & Margin Exposure Bar
        item {
            Surface(
                color = SurfaceElevated,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Margin & Asset Allocation",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Margin Used: $${String.format("%.2f", marginUsed)} • Free Margin: $${String.format("%.2f", totalEquity - marginUsed)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Multi-color segmented allocation bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(14.dp)
                            .clip(RoundedCornerShape(7.dp))
                    ) {
                        Box(modifier = Modifier.weight(0.45f).fillMaxHeight().background(Color(0xFF3B82F6)))
                        Box(modifier = Modifier.weight(0.30f).fillMaxHeight().background(Color(0xFF10B981)))
                        Box(modifier = Modifier.weight(0.15f).fillMaxHeight().background(Color(0xFFF59E0B)))
                        Box(modifier = Modifier.weight(0.10f).fillMaxHeight().background(Color(0xFF8B5CF6)))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        AllocationLegendItem(name = "Equities (45%)", color = Color(0xFF3B82F6))
                        AllocationLegendItem(name = "Crypto (30%)", color = Color(0xFF10B981))
                        AllocationLegendItem(name = "Commodities (15%)", color = Color(0xFFF59E0B))
                        AllocationLegendItem(name = "Forex (10%)", color = Color(0xFF8B5CF6))
                    }
                }
            }
        }
    }
}

@Composable
fun AnalyticsKpiCard(
    title: String,
    value: String,
    subText: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = SurfaceElevated,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = title, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = valueColor,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subText, style = MaterialTheme.typography.labelSmall, color = TextMuted)
        }
    }
}

@Composable
fun AllocationLegendItem(name: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = name, style = MaterialTheme.typography.labelSmall, color = TextSecondary, fontSize = 10.sp)
    }
}
