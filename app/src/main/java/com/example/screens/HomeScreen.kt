package com.example.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.SparklineChart
import com.example.model.Asset
import com.example.model.CuratedWatchlist
import com.example.model.PriceAlert
import com.example.model.TradePosition
import com.example.model.VerificationStatus
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BorderDark
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.GoldBeige
import com.example.ui.theme.GoldBeigeBg
import com.example.ui.theme.GoldBeigeLight
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TradingGreen
import com.example.ui.theme.TradingGreenBg
import com.example.ui.theme.TradingGreenLight
import com.example.ui.theme.TradingRed
import com.example.ui.theme.TradingRedBg
import com.example.ui.theme.TradingRedLight
import java.util.Locale

@Composable
fun HomeScreen(
    assets: List<Asset>,
    curatedWatchlists: List<CuratedWatchlist>,
    priceAlerts: List<PriceAlert>,
    positions: List<TradePosition>,
    verificationStatus: VerificationStatus,
    onStartVerification: () -> Unit,
    onAssetClick: (Asset) -> Unit,
    onAddAlertClick: () -> Unit,
    onExplorePortfolioClick: () -> Unit,
    onCuratedClick: (CuratedWatchlist) -> Unit,
    modifier: Modifier = Modifier
) {
    val mostTradedAssets = assets.filter { it.isMostTraded }.take(6)
    val mostVolatileAssets = assets.filter { it.isMostVolatile || it.isClosed }.take(3)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Verification Banner if not verified
        if (verificationStatus != VerificationStatus.VERIFIED) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clickable { onStartVerification() }
                        .testTag("verification_banner"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = GoldBeigeBg),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldBeige.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(GoldBeige.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Verification",
                                tint = GoldBeige,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Complete identity verification",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Unlock live real-money execution & deposits",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = GoldBeige,
                            modifier = Modifier.padding(start = 6.dp)
                        ) {
                            Text(
                                text = "Verify",
                                color = Color.Black,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }
        }

        // Golden Compass Market Navigator Flagship Card
        item {
            GoldenCompassNavigatorCard(
                onNavigateExplore = onExplorePortfolioClick
            )
        }

        // Section: Curated Watchlists (Horizontal Scrolling)
        item {
            Column(modifier = Modifier.padding(top = 10.dp)) {
                Text(
                    text = "Curated watchlists",
                    color = TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(curatedWatchlists) { watchlist ->
                        CuratedWatchlistCard(
                            watchlist = watchlist,
                            onClick = { onCuratedClick(watchlist) }
                        )
                    }
                }
            }
        }

        // Section: Most Traded (6-Asset Grid)
        item {
            Column(modifier = Modifier.padding(top = 22.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Most traded",
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 2-Column x 3-Row Grid
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val pairs = mostTradedAssets.chunked(2)
                    pairs.forEach { pair ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            pair.forEach { asset ->
                                MostTradedAssetGridCard(
                                    asset = asset,
                                    onClick = { onAssetClick(asset) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (pair.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }

        // Section: Most Volatile
        item {
            Column(modifier = Modifier.padding(top = 22.dp)) {
                Text(
                    text = "Most volatile",
                    color = TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
                ) {
                    Column(modifier = Modifier.padding(vertical = 6.dp)) {
                        mostVolatileAssets.forEachIndexed { index, asset ->
                            VolatileAssetRow(
                                asset = asset,
                                onClick = { onAssetClick(asset) }
                            )
                            if (index < mostVolatileAssets.size - 1) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                        .height(1.dp)
                                        .background(BorderDark.copy(alpha = 0.5f))
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section: Price Alerts
        item {
            Column(modifier = Modifier.padding(top = 22.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Price alerts",
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SurfaceDark,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
                        modifier = Modifier
                            .clickable { onAddAlertClick() }
                            .testTag("add_alert_pill")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add alert",
                                tint = GoldBeige,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Add alert",
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        if (priceAlerts.isEmpty()) {
                            Text("No active price alerts set.", color = TextSecondary, fontSize = 13.sp)
                        } else {
                            priceAlerts.take(2).forEach { alert ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(GoldBeige)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = alert.assetSymbol,
                                            color = TextPrimary,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Text(
                                        text = "${if (alert.isAbove) "≥" else "≤"} $${String.format(Locale.US, "%,.2f", alert.targetPrice)}",
                                        color = if (alert.isAbove) TradingGreen else TradingRed,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section: Portfolio Widget (Radar Icon, "No open trades" / Open trade summary, White "Explore" button)
        item {
            Column(modifier = Modifier.padding(top = 22.dp, start = 16.dp, end = 16.dp)) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .testTag("portfolio_home_widget"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Portfolio",
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )

                            // Golden Radar Icon
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(GoldBeigeBg)
                                    .border(1.dp, GoldBeige.copy(alpha = 0.4f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Radar,
                                    contentDescription = "Radar",
                                    tint = GoldBeige,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        if (positions.isEmpty()) {
                            Text(
                                text = "No open trades",
                                color = TextSecondary,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Explore global markets to place your first trade.",
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                        } else {
                            val totalPnl = positions.sumOf { it.pnl }
                            val isPos = totalPnl >= 0
                            Text(
                                text = "${positions.size} Active Position${if (positions.size > 1) "s" else ""}",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Unrealized P&L: ${if (isPos) "+" else ""}$${String.format(Locale.US, "%,.2f", totalPnl)}",
                                color = if (isPos) TradingGreen else TradingRed,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // White "Explore" Button
                        Button(
                            onClick = onExplorePortfolioClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("portfolio_explore_button"),
                            shape = RoundedCornerShape(23.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color.Black
                            )
                        ) {
                            Text(
                                text = "Explore",
                                color = Color.Black,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// CURATED WATCHLIST CARD (Horizontal Card)
// ----------------------------------------------------
@Composable
fun CuratedWatchlistCard(
    watchlist: CuratedWatchlist,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .size(width = 150.dp, height = 150.dp)
            .clickable { onClick() }
            .testTag("curated_card_${watchlist.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Subtle top corner gradient mesh
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(watchlist.accentColorHex).copy(alpha = 0.25f), Color.Transparent),
                        center = Offset(size.width * 0.85f, size.height * 0.15f),
                        radius = size.width * 0.7f
                    )
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Icon / Emoji in Glass Badge
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceElevated)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = watchlist.iconEmoji, fontSize = 20.sp)
                }

                // Title & Subtitle
                Column {
                    Text(
                        text = watchlist.title,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${watchlist.assetCount} assets",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

// ----------------------------------------------------
// MOST TRADED 6-ASSET GRID CARD
// ----------------------------------------------------
@Composable
fun MostTradedAssetGridCard(
    asset: Asset,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isPos = asset.changePercent >= 0

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable { onClick() }
            .testTag("most_traded_${asset.symbol}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = asset.iconEmoji, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = asset.symbol,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Percentage Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isPos) TradingGreenBg else TradingRedBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isPos) TradingGreen.copy(0.4f) else TradingRed.copy(0.4f))
                ) {
                    Text(
                        text = "${if (isPos) "+" else ""}${String.format(Locale.US, "%.2f", asset.changePercent)}%",
                        color = if (isPos) TradingGreenLight else TradingRedLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = if (asset.price < 1.0) String.format(Locale.US, "%.6f", asset.price)
                    else String.format(Locale.US, "$%,.2f", asset.price),
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )

                // Sparkline
                Box(
                    modifier = Modifier
                        .width(46.dp)
                        .height(18.dp)
                ) {
                    SparklineChart(
                        points = asset.sparklinePoints,
                        isPositive = isPos,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

// ----------------------------------------------------
// VOLATILE ASSET ROW (List Card)
// ----------------------------------------------------
@Composable
fun VolatileAssetRow(
    asset: Asset,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isPos = asset.changePercent >= 0

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(SurfaceElevated),
            contentAlignment = Alignment.Center
        ) {
            Text(text = asset.iconEmoji, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Symbol & Lock / Closed indicator
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = asset.symbol,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                if (asset.isClosed) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Closed",
                        tint = TextMuted,
                        modifier = Modifier.size(11.dp)
                    )
                }
            }
            Text(
                text = asset.name,
                color = TextSecondary,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Sparkline
        Box(
            modifier = Modifier
                .width(54.dp)
                .height(20.dp)
        ) {
            SparklineChart(
                points = asset.sparklinePoints,
                isPositive = isPos,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Price & Percentage
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = if (asset.price < 1.0) String.format(Locale.US, "%.8f", asset.price)
                else String.format(Locale.US, "$%,.2f", asset.price),
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "${if (isPos) "+" else ""}${String.format(Locale.US, "%.2f", asset.changePercent)}%",
                color = if (isPos) TradingGreen else TradingRed,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// ----------------------------------------------------
// GOLDEN COMPASS MARKET NAVIGATOR WIDGET
// ----------------------------------------------------
@Composable
fun GoldenCompassNavigatorCard(
    onNavigateExplore: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedMode by remember { mutableIntStateOf(0) } // 0: True North, 1: Macro Tide, 2: Volatility

    val headingTarget = when (selectedMode) {
        0 -> 38f // NNE
        1 -> 72f // ENE
        else -> 315f // NW (Hedge)
    }

    val animatedAngle by animateFloatAsState(
        targetValue = headingTarget,
        animationSpec = tween(durationMillis = 800),
        label = "compass_needle_rotation"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
        ),
        label = "compass_glow"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("golden_compass_navigator_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF10141D)),
        border = androidx.compose.foundation.BorderStroke(1.dp, GoldBeige.copy(alpha = 0.45f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF181B26),
                            Color(0xFF0D1017)
                        )
                    )
                )
                .padding(18.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(GoldBeigeBg)
                            .border(1.dp, GoldBeige.copy(alpha = 0.6f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Explore,
                            contentDescription = "Golden Compass",
                            tint = GoldBeige,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "GOLDEN COMPASS",
                            color = GoldBeige,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Market Regime Navigator",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = GoldBeigeBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldBeige.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(TradingGreen)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "LIVE BEARING",
                            color = GoldBeige,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Interactive Compass Dial & Readouts
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Nautical Compass Dial Canvas
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val center = Offset(size.width / 2, size.height / 2)
                        val radius = size.minDimension / 2 - 4f

                        // Outer ring
                        drawCircle(
                            color = Color(0xFFD4AF37).copy(alpha = glowAlpha * 0.5f),
                            radius = radius,
                            center = center,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
                        )
                        // Inner secondary ring
                        drawCircle(
                            color = Color(0xFF2A344A),
                            radius = radius * 0.75f,
                            center = center,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx())
                        )

                        // Draw ticks for cardinal points
                        for (i in 0 until 12) {
                            val angleRad = Math.toRadians((i * 30).toDouble())
                            val tickLen = if (i % 3 == 0) 10f else 5f
                            val startX = (center.x + (radius - tickLen) * Math.cos(angleRad)).toFloat()
                            val startY = (center.y + (radius - tickLen) * Math.sin(angleRad)).toFloat()
                            val endX = (center.x + radius * Math.cos(angleRad)).toFloat()
                            val endY = (center.y + radius * Math.sin(angleRad)).toFloat()

                            drawLine(
                                color = if (i % 3 == 0) Color(0xFFD4AF37) else Color(0xFF64748B),
                                start = Offset(startX, startY),
                                end = Offset(endX, endY),
                                strokeWidth = if (i % 3 == 0) 2.dp.toPx() else 1.dp.toPx()
                            )
                        }

                        // Compass Needle pointing to animatedAngle
                        val needleAngleRad = Math.toRadians((animatedAngle - 90).toDouble())
                        val needleLength = radius * 0.8f

                        val tipX = (center.x + needleLength * Math.cos(needleAngleRad)).toFloat()
                        val tipY = (center.y + needleLength * Math.sin(needleAngleRad)).toFloat()

                        val backX = (center.x - needleLength * 0.45f * Math.cos(needleAngleRad)).toFloat()
                        val backY = (center.y - needleLength * 0.45f * Math.sin(needleAngleRad)).toFloat()

                        // North pointer (Gold/Crimson)
                        drawLine(
                            color = Color(0xFFEF4444),
                            start = center,
                            end = Offset(tipX, tipY),
                            strokeWidth = 3.dp.toPx(),
                            cap = androidx.compose.ui.graphics.StrokeCap.Round
                        )
                        // South pointer (Steel Blue)
                        drawLine(
                            color = Color(0xFF38BDF8),
                            start = center,
                            end = Offset(backX, backY),
                            strokeWidth = 2.dp.toPx(),
                            cap = androidx.compose.ui.graphics.StrokeCap.Round
                        )

                        // Center Pivot Hub
                        drawCircle(
                            color = Color(0xFFD4AF37),
                            radius = 4.dp.toPx(),
                            center = center
                        )
                    }

                    // Floating degree indicator badge inside
                    Text(
                        text = "${animatedAngle.toInt()}°",
                        color = GoldBeige,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .background(Color(0xFF0F172A), RoundedCornerShape(4.dp))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Compass Coordinates & Intelligence
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = when (selectedMode) {
                            0 -> "038° NNE • Alpha Trend"
                            1 -> "072° ENE • Macro Surge"
                            else -> "315° NW • Volatility Wall"
                        },
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = when (selectedMode) {
                            0 -> "Market Latitude: 40.71° N • Long: 74.00° W\nTrue North: Institutional Accumulation"
                            1 -> "Global Liquidity: +$42.8B Fed Tailwinds\nSector Flow: Tech & Crypto Leading"
                            else -> "VIX Meridian: 14.2 (Low Volatility Zone)\nHedge Waypoint: Anchored at 4,950 S&P"
                        },
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Strength meter
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Signal Pull: ",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                        Text(
                            text = if (selectedMode == 0) "88% (High)" else if (selectedMode == 1) "94% (Strong)" else "72% (Neutral)",
                            color = TradingGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Compass Mode Switcher Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F131C), RoundedCornerShape(12.dp))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("True North", "Macro Tide", "Volatility").forEachIndexed { index, modeTitle ->
                    val isSelected = selectedMode == index
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedMode = index },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) GoldBeige else Color.Transparent
                    ) {
                        Text(
                            text = modeTitle,
                            color = if (isSelected) Color.Black else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}
