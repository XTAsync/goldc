package com.example.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.example.components.AssetListItem
import com.example.model.Asset
import com.example.model.AssetCategory
import com.example.model.CuratedWatchlist
import com.example.model.CustomWatchlist
import com.example.ui.theme.*

@Composable
fun WatchlistsScreen(
    assets: List<Asset>,
    curatedWatchlists: List<CuratedWatchlist>,
    customWatchlists: List<CustomWatchlist>,
    selectedCategory: AssetCategory,
    onCategorySelected: (AssetCategory) -> Unit,
    onCreateWatchlistClick: () -> Unit,
    onDeleteWatchlist: (String) -> Unit,
    onToggleAssetInWatchlist: (String, String) -> Unit,
    onAssetClick: (Asset) -> Unit,
    onBuyClick: (Asset) -> Unit,
    onSellClick: (Asset) -> Unit,
    modifier: Modifier = Modifier
) {
    var isSortMenuOpen by remember { mutableStateOf(false) }
    var sortOption by remember { mutableStateOf("Default") }
    var selectedWatchlistTab by remember { mutableStateOf("all") } // "all", "curated", or customWatchlist.id

    val categories = listOf(
        AssetCategory.MOST_TRADED,
        AssetCategory.ALL,
        AssetCategory.INDICES,
        AssetCategory.COMMODITIES,
        AssetCategory.CRYPTO,
        AssetCategory.SHARES,
        AssetCategory.CURRENCIES
    )

    val activeCustomWatchlist = customWatchlists.find { it.id == selectedWatchlistTab }

    val displayedAssets = remember(assets, selectedCategory, sortOption, selectedWatchlistTab, customWatchlists) {
        val baseList = if (activeCustomWatchlist != null) {
            assets.filter { activeCustomWatchlist.assetSymbols.contains(it.symbol) }
        } else {
            when (selectedCategory) {
                AssetCategory.ALL -> assets
                AssetCategory.MOST_TRADED -> assets.filter { it.isMostTraded || it.isMostVolatile }
                else -> assets.filter { it.category == selectedCategory }
            }
        }

        when (sortOption) {
            "Top Gainers" -> baseList.sortedByDescending { it.changePercent }
            "Top Losers" -> baseList.sortedBy { it.changePercent }
            "Price High-Low" -> baseList.sortedByDescending { it.price }
            else -> baseList
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Top Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Watchlists & Markets",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Real-time streaming prices & custom lists",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }

                    // Create Watchlist Button
                    IconButton(
                        onClick = onCreateWatchlistClick,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(AccentTeal)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Watchlist", tint = BackgroundDark)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Watchlist Type Tabs (All Markets, Custom Lists)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        val isSel = selectedWatchlistTab == "all"
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSel) AccentTeal else SurfaceDark,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) AccentTeal else BorderDark),
                            modifier = Modifier.clickable { selectedWatchlistTab = "all" }
                        ) {
                            Text(
                                text = "🌐 All Markets",
                                color = if (isSel) BackgroundDark else TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }

                    items(customWatchlists) { wl ->
                        val isSel = selectedWatchlistTab == wl.id
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSel) AccentTeal else SurfaceDark,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) AccentTeal else BorderDark),
                            modifier = Modifier.clickable { selectedWatchlistTab = wl.id }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "${wl.icon} ${wl.name} (${wl.assetSymbols.size})", color = if (isSel) BackgroundDark else TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // If "all" is selected, show category pills
                if (selectedWatchlistTab == "all") {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(categories) { cat ->
                            val isSelected = selectedCategory == cat
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (isSelected) SurfaceElevated else SurfaceDark,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) AccentCyan else BorderDark
                                ),
                                modifier = Modifier.clickable { onCategorySelected(cat) }
                            ) {
                                Text(
                                    text = cat.displayName,
                                    color = if (isSelected) TextPrimary else TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                } else if (activeCustomWatchlist != null) {
                    // Custom watchlist toolbar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${activeCustomWatchlist.assetSymbols.size} symbols in list",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )

                        TextButton(
                            onClick = {
                                onDeleteWatchlist(activeCustomWatchlist.id)
                                selectedWatchlistTab = "all"
                            }
                        ) {
                            Text("Delete List", color = TradingRed, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }

        if (displayedAssets.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "No assets in this watchlist", color = TextPrimary, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "Add symbols from the Explore screen", color = TextSecondary, fontSize = 12.sp)
                    }
                }
            }
        } else {
            // Asset List Cards
            items(displayedAssets, key = { it.id }) { asset ->
                AssetListItem(
                    asset = asset,
                    onAssetClick = { onAssetClick(asset) },
                    onBuyClick = { onBuyClick(asset) },
                    onSellClick = { onSellClick(asset) }
                )
            }
        }
    }
}
