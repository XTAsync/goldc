package com.example.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Article
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShowChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Asset
import com.example.model.AssetCategory
import com.example.model.NewsArticle
import com.example.model.PriceAlert
import com.example.model.TradeType
import com.example.ui.theme.*
import java.util.Locale

// ----------------------------------------------------
// TOP BAR
// ----------------------------------------------------
@Composable
fun AppTopBar(
    balance: Double,
    onSearchClick: () -> Unit,
    onBalanceClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Golden Compass brand indicator & Search
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = SurfaceDark,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
            modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .clickable { onSearchClick() }
                .testTag("top_search_bar")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(GoldBeigeBg)
                        .border(1.dp, GoldBeige.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Explore,
                        contentDescription = "Golden Compass",
                        tint = GoldBeige,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Golden Compass Navigator...",
                    color = TextSecondary,
                    fontSize = 12.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Balance Pill in the center/right
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = SurfaceDark,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
            modifier = Modifier
                .height(44.dp)
                .clickable { onBalanceClick() }
                .testTag("balance_pill")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(if (balance > 0) TradingGreen else GoldBeige)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = String.format(Locale.US, "$%,.2f", balance),
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Profile Avatar on the right
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(SurfaceDark)
                .border(1.dp, GoldBeige.copy(alpha = 0.4f), CircleShape)
                .clickable { onProfileClick() }
                .testTag("profile_avatar_button"),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "GC",
                color = GoldBeige,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

// ----------------------------------------------------
// BOTTOM NAVIGATION
// ----------------------------------------------------
@Composable
fun AppBottomNavigation(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        Triple("Compass", Icons.Outlined.Explore, 0),
        Triple("Markets", Icons.Outlined.ShowChart, 1),
        Triple("Portfolio", Icons.Outlined.AccountBalanceWallet, 2),
        Triple("Orders", Icons.Default.ReceiptLong, 3),
        Triple("Navigator AI", Icons.Default.AutoAwesome, 4),
        Triple("Analytics", Icons.Default.Leaderboard, 5)
    )

    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        containerColor = BackgroundDark,
        tonalElevation = 0.dp
    ) {
        items.forEach { (label, icon, index) ->
            val isSelected = selectedTab == index
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(index) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        modifier = Modifier.size(20.dp)
                    )
                },
                label = {
                    Text(
                        text = label,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        maxLines = 1
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = GoldBeige,
                    selectedTextColor = GoldBeige,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary,
                    indicatorColor = SurfaceElevated
                )
            )
        }
    }
}

// ----------------------------------------------------
// FLOATING ACTION BUTTON (4-GRID SQUARE MENU)
// ----------------------------------------------------
@Composable
fun AppGridFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier
            .size(52.dp)
            .shadow(12.dp, CircleShape)
            .testTag("fab_grid_menu"),
        shape = CircleShape,
        containerColor = SurfaceElevated,
        contentColor = TextPrimary
    ) {
        Icon(
            imageVector = Icons.Default.GridView,
            contentDescription = "Main Menu",
            tint = TextPrimary,
            modifier = Modifier.size(22.dp)
        )
    }
}

// ----------------------------------------------------
// ASSET LIST ITEM CARD
// ----------------------------------------------------
@Composable
fun AssetListItem(
    asset: Asset,
    onAssetClick: () -> Unit,
    onBuyClick: () -> Unit,
    onSellClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isPos = asset.changePercent >= 0

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .clickable { onAssetClick() }
            .testTag("asset_item_${asset.symbol}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Icon + Symbol + Name
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(SurfaceElevated)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = asset.iconEmoji, fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1.3f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = asset.symbol,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    if (asset.isClosed) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Market Closed",
                            tint = TextMuted,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = asset.name,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Middle: Price + Percent + Sparkline
            Column(
                modifier = Modifier.weight(1.4f),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = if (asset.price < 1.0) String.format(Locale.US, "%.8f", asset.price)
                    else String.format(Locale.US, "$%,.2f", asset.price),
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "${if (isPos) "+" else ""}${String.format(Locale.US, "%.2f", asset.changePercent)}%",
                        color = if (isPos) TradingGreen else TradingRed,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .width(46.dp)
                            .height(18.dp)
                    ) {
                        SparklineChart(
                            points = asset.sparklinePoints,
                            isPositive = isPos,
                            useBlueForPositive = false,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Right: Two Pill Buttons: Sell and Buy
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Sell Pill
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = TradingRedBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, TradingRed.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .height(30.dp)
                        .clickable { onSellClick() }
                        .testTag("sell_button_${asset.symbol}")
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 9.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Sell",
                            color = TradingRedLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Buy Pill
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = TradingGreenBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, TradingGreen.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .height(30.dp)
                        .clickable { onBuyClick() }
                        .testTag("buy_button_${asset.symbol}")
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 9.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Buy",
                            color = TradingGreenLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// TRADE ORDER MODAL BOTTOM SHEET
// ----------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TradeOrderBottomSheet(
    asset: Asset,
    initialTradeType: TradeType,
    onDismiss: () -> Unit,
    onConfirmOrder: (TradeType, Double) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var tradeType by remember { mutableStateOf(initialTradeType) }
    var lotSize by remember { mutableDoubleStateOf(1.0) }
    var leverage by remember { mutableIntStateOf(10) }
    var isLimitOrder by remember { mutableStateOf(false) }
    var limitPriceText by remember(asset) { mutableStateOf(String.format(Locale.US, "%.2f", asset.price)) }
    var stopLossEnabled by remember { mutableStateOf(false) }
    var stopLossText by remember(asset) { mutableStateOf(String.format(Locale.US, "%.2f", asset.price * 0.95)) }
    var takeProfitEnabled by remember { mutableStateOf(false) }
    var takeProfitText by remember(asset) { mutableStateOf(String.format(Locale.US, "%.2f", asset.price * 1.08)) }

    val activePrice = if (isLimitOrder) (limitPriceText.toDoubleOrNull() ?: asset.price) else asset.price
    val totalNotional = activePrice * lotSize
    val estMargin = totalNotional / leverage

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = BackgroundDark,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(BorderDark)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .navigationBarsPadding()
        ) {
            // Header: Asset Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = asset.iconEmoji, fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = asset.symbol,
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = asset.name,
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = String.format(Locale.US, "$%,.2f", asset.price),
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${if (asset.changePercent >= 0) "+" else ""}${String.format(Locale.US, "%.2f", asset.changePercent)}%",
                        color = if (asset.changePercent >= 0) TradingGreen else TradingRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Buy / Sell Selector Switcher
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .clip(RoundedCornerShape(21.dp))
                    .background(SurfaceDark)
                    .border(1.dp, BorderDark, RoundedCornerShape(21.dp))
                    .padding(3.dp)
            ) {
                // Sell Tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (tradeType == TradeType.SELL) TradingRed else Color.Transparent)
                        .clickable { tradeType = TradeType.SELL },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "SELL / SHORT",
                        color = if (tradeType == TradeType.SELL) Color.White else TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Buy Tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (tradeType == TradeType.BUY) TradingGreen else Color.Transparent)
                        .clickable { tradeType = TradeType.BUY },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "BUY / LONG",
                        color = if (tradeType == TradeType.BUY) Color.White else TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Order Type (Market vs Limit)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Order Execution Type", color = TextSecondary, fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (!isLimitOrder) AccentTeal else SurfaceDark,
                        modifier = Modifier.clickable { isLimitOrder = false }
                    ) {
                        Text(
                            text = "Market",
                            color = if (!isLimitOrder) BackgroundDark else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isLimitOrder) AccentTeal else SurfaceDark,
                        modifier = Modifier.clickable { isLimitOrder = true }
                    ) {
                        Text(
                            text = "Limit Order",
                            color = if (isLimitOrder) BackgroundDark else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Lot Size Stepper
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceDark)
                    .border(1.dp, BorderDark, RoundedCornerShape(12.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { if (lotSize > 0.5) lotSize = ((lotSize - 0.5) * 10).toLong() / 10.0 },
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(SurfaceElevated)
                ) {
                    Text("-", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = String.format(Locale.US, "%.1f Units", lotSize),
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Notional $${String.format(Locale.US, "%,.2f", totalNotional)}",
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                }

                IconButton(
                    onClick = { if (lotSize < 50.0) lotSize = ((lotSize + 0.5) * 10).toLong() / 10.0 },
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(SurfaceElevated)
                ) {
                    Text("+", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Leverage selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Leverage Multiplier", color = TextSecondary, fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf(1, 5, 10, 20, 50).forEach { lev ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (leverage == lev) AccentCyan else SurfaceDark,
                            modifier = Modifier.clickable { leverage = lev }
                        ) {
                            Text(
                                text = "${lev}x",
                                color = if (leverage == lev) BackgroundDark else TextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Order metrics
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Required Margin", color = TextSecondary, fontSize = 11.sp)
                        Text(String.format(Locale.US, "$%,.2f", estMargin), color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Broker Route", color = TextSecondary, fontSize = 11.sp)
                        Text("AlphaTrade DMA Direct", color = AccentTeal, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Button
            Button(
                onClick = { onConfirmOrder(tradeType, lotSize) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("confirm_order_button"),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (tradeType == TradeType.BUY) TradingGreen else TradingRed
                )
            ) {
                Text(
                    text = "Transmit ${tradeType.name} Order ($${String.format(Locale.US, "%,.2f", estMargin)} Margin)",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

// ----------------------------------------------------
// ASSET DETAIL SHEET
// ----------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetDetailSheet(
    asset: Asset,
    onDismiss: () -> Unit,
    onBuy: () -> Unit,
    onSell: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedInterval by remember { mutableStateOf("1D") }
    val intervals = listOf("1D", "1W", "1M", "1Y", "ALL")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = BackgroundDark,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(BorderDark)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .navigationBarsPadding()
        ) {
            // Title & Price Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(asset.iconEmoji, fontSize = 22.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(asset.symbol, color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(asset.name, color = TextSecondary, fontSize = 12.sp)
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Price & Percent
            Text(
                text = String.format(Locale.US, "$%,.2f", asset.price),
                color = TextPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${if (asset.changePercent >= 0) "+" else ""}${String.format(Locale.US, "%.2f", asset.changePercent)}% (${if (asset.changeAmount >= 0) "+" else ""}$${String.format(Locale.US, "%.2f", asset.changeAmount)})",
                    color = if (asset.changePercent >= 0) TradingGreen else TradingRed,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Today", color = TextMuted, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Big Chart View
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceDark)
                    .border(1.dp, BorderDark, RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                SparklineChart(
                    points = asset.sparklinePoints,
                    isPositive = asset.changePercent >= 0,
                    modifier = Modifier.fillMaxSize(),
                    strokeWidth = 2.5.dp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Interval Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                intervals.forEach { interval ->
                    val isSel = selectedInterval == interval
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSel) SurfaceElevated else Color.Transparent)
                            .clickable { selectedInterval = interval }
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = interval,
                            color = if (isSel) TextPrimary else TextMuted,
                            fontSize = 12.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Key Stats Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("24h High", color = TextMuted, fontSize = 11.sp)
                        Text(String.format(Locale.US, "$%,.2f", asset.high24h), color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("24h Low", color = TextMuted, fontSize = 11.sp)
                        Text(String.format(Locale.US, "$%,.2f", asset.low24h), color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Volume", color = TextMuted, fontSize = 11.sp)
                        Text(asset.volume, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons (Sell & Buy)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onSell,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TradingRed)
                ) {
                    Text("Sell", color = Color.White, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onBuy,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TradingGreen)
                ) {
                    Text("Buy", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

// ----------------------------------------------------
// QUICK ACTION FAB MENU SHEET
// ----------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickActionMenuSheet(
    onDismiss: () -> Unit,
    onDepositClick: () -> Unit,
    onAddAlertClick: () -> Unit,
    onSearchClick: () -> Unit,
    onVerifyClick: () -> Unit,
    onHelpClick: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = BackgroundDark,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(BorderDark)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .navigationBarsPadding()
        ) {
            Text(
                text = "Quick Actions",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            val menuItems = listOf(
                Triple("Deposit Funds", "Instant funding with card or crypto", Icons.Outlined.AccountBalanceWallet) to onDepositClick,
                Triple("Set Price Alert", "Get notified when target hits", Icons.Default.Notifications) to onAddAlertClick,
                Triple("Search Markets", "Discover global stocks & crypto", Icons.Default.Search) to onSearchClick,
                Triple("Verify Identity", "Complete onboarding verification", Icons.Default.Check) to onVerifyClick,
                Triple("Support & Help", "Chat with investment advisors", Icons.Default.HelpOutline) to onHelpClick
            )

            menuItems.forEach { (item, action) ->
                val (title, sub, icon) = item
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { action() },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(SurfaceElevated),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(icon, contentDescription = title, tint = TextPrimary, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text(sub, color = TextSecondary, fontSize = 11.sp)
                        }
                        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ----------------------------------------------------
// PRICE ALERT MODAL SHEET
// ----------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceAlertSheet(
    assets: List<Asset>,
    existingAlerts: List<PriceAlert>,
    onDismiss: () -> Unit,
    onAddAlert: (String, Double, Boolean) -> Unit,
    onDeleteAlert: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedAssetSymbol by remember { mutableStateOf(assets.firstOrNull()?.symbol ?: "Gold") }
    val currentAsset = assets.find { it.symbol == selectedAssetSymbol } ?: assets.firstOrNull()
    var targetPriceText by remember(selectedAssetSymbol) {
        mutableStateOf(String.format(Locale.US, "%.2f", (currentAsset?.price ?: 100.0) * 1.05))
    }
    var isAbove by remember { mutableStateOf(true) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = BackgroundDark,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(BorderDark)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .navigationBarsPadding()
        ) {
            Text(
                text = "Price Alerts",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Receive instant push alerts on key asset price levels",
                color = TextSecondary,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Select Asset Chips
            Text("Select Asset", color = TextSecondary, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(assets.take(8)) { a ->
                    val isSel = a.symbol == selectedAssetSymbol
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSel) SurfaceElevated else SurfaceDark)
                            .border(1.dp, if (isSel) ElectricBlue else BorderDark, RoundedCornerShape(20.dp))
                            .clickable {
                                selectedAssetSymbol = a.symbol
                                targetPriceText = String.format(Locale.US, "%.2f", a.price * 1.05)
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "${a.iconEmoji} ${a.symbol}",
                            color = if (isSel) TextPrimary else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Trigger Condition: Above or Below
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceDark)
                    .border(1.dp, BorderDark, RoundedCornerShape(12.dp))
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isAbove) SurfaceElevated else Color.Transparent)
                        .clickable { isAbove = true }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Rises Above (≥)",
                        color = if (isAbove) TradingGreen else TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (!isAbove) SurfaceElevated else Color.Transparent)
                        .clickable { isAbove = false }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Drops Below (≤)",
                        color = if (!isAbove) TradingRed else TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Target Price Input
            OutlinedTextField(
                value = targetPriceText,
                onValueChange = { targetPriceText = it },
                label = { Text("Target Price ($)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ElectricBlue,
                    unfocusedBorderColor = BorderDark,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = SurfaceDark,
                    unfocusedContainerColor = SurfaceDark
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val price = targetPriceText.toDoubleOrNull() ?: (currentAsset?.price ?: 100.0)
                    onAddAlert(selectedAssetSymbol, price, isAbove)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GoldBeige)
            ) {
                Text("Create Alert", color = Color.Black, fontWeight = FontWeight.Bold)
            }

            if (existingAlerts.isNotEmpty()) {
                Spacer(modifier = Modifier.height(18.dp))
                Text("Active Alerts", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                existingAlerts.forEach { alert ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(alert.assetSymbol, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    text = "${if (alert.isAbove) "≥" else "≤"} $${String.format(Locale.US, "%,.2f", alert.targetPrice)}",
                                    color = if (alert.isAbove) TradingGreen else TradingRed,
                                    fontSize = 11.sp
                                )
                            }
                            IconButton(
                                onClick = { onDeleteAlert(alert.id) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Delete", tint = TextMuted, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

// ----------------------------------------------------
// DEPOSIT SHEET (SIMULATED FUNDS)
// ----------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepositSheet(
    onDismiss: () -> Unit,
    onDepositAmount: (Double) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val amounts = listOf(500.0, 1000.0, 5000.0, 10000.0, 25000.0)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = BackgroundDark,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(BorderDark)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .navigationBarsPadding()
        ) {
            Text("Deposit Trading Capital", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("Add funds to trade global indices, stocks, crypto & forex", color = TextSecondary, fontSize = 12.sp)

            Spacer(modifier = Modifier.height(18.dp))

            amounts.forEach { amt ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onDepositAmount(amt) },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(String.format(Locale.US, "$%,d USD", amt.toLong()), color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = SurfaceElevated
                        ) {
                            Text(
                                text = "Instant Credit",
                                color = TradingGreen,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ----------------------------------------------------
// SEARCH OVERLAY MODAL
// ----------------------------------------------------
@Composable
fun SearchOverlay(
    query: String,
    onQueryChange: (String) -> Unit,
    assets: List<Asset>,
    onClose: () -> Unit,
    onAssetSelected: (Asset) -> Unit,
    onBuy: (Asset) -> Unit,
    onSell: (Asset) -> Unit
) {
    val filtered = remember(query, assets) {
        if (query.isBlank()) assets else {
            assets.filter {
                it.symbol.contains(query, ignoreCase = true) ||
                it.name.contains(query, ignoreCase = true) ||
                it.category.name.contains(query, ignoreCase = true)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Search Input Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = SurfaceDark,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = query,
                            onValueChange = onQueryChange,
                            placeholder = { Text("Search stocks, indices, crypto...", color = TextSecondary, fontSize = 13.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            HorizontalDivider(color = BorderDark)

            // Results List
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(filtered) { asset ->
                    AssetListItem(
                        asset = asset,
                        onAssetClick = { onAssetSelected(asset) },
                        onBuyClick = { onBuy(asset) },
                        onSellClick = { onSell(asset) }
                    )
                }
            }
        }
    }
}
