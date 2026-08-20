package com.example.screens

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.NewsArticle
import com.example.model.StockImpactTag
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BorderDark
import com.example.ui.theme.GoldBeige
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
fun NewsScreen(
    newsArticles: List<NewsArticle>,
    selectedArticle: NewsArticle?,
    onArticleClick: (NewsArticle) -> Unit,
    onCloseArticleDetail: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Latest, 1: For you
    val tabs = listOf("Latest", "For you")

    val filteredNews = remember(newsArticles, selectedTab) {
        val targetCat = tabs[selectedTab]
        newsArticles.filter { it.category == targetCat || it.category == "All" }.ifEmpty { newsArticles }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Header: "News" with "Show all" button
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "News",
                        color = TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    TextButton(
                        onClick = { /* Show all news */ },
                        modifier = Modifier.testTag("news_show_all_button")
                    ) {
                        Text(
                            text = "Show all",
                            color = GoldBeige,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Tabs: Latest, For you
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceDark)
                        .border(1.dp, BorderDark, RoundedCornerShape(12.dp))
                        .padding(3.dp)
                ) {
                    tabs.forEachIndexed { index, tabName ->
                        val isSelected = selectedTab == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) SurfaceElevated else Color.Transparent)
                                .clickable { selectedTab = index }
                                .testTag("news_tab_$tabName"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tabName,
                                color = if (isSelected) TextPrimary else TextSecondary,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(6.dp))
        }

        // News Item Cards
        items(filteredNews) { article ->
            NewsCardItem(
                article = article,
                onClick = { onArticleClick(article) }
            )
        }
    }

    // Modal Sheet for News Detail
    if (selectedArticle != null) {
        NewsDetailModalSheet(
            article = selectedArticle,
            onDismiss = onCloseArticleDetail
        )
    }
}

// ----------------------------------------------------
// NEWS CARD ITEM
// ----------------------------------------------------
@Composable
fun NewsCardItem(
    article: NewsArticle,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() }
            .testTag("news_article_${article.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Headline
            Text(
                text = article.headline,
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 21.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle: "7 minutes ago • Reuters News"
            Text(
                text = article.subtitle,
                color = TextSecondary,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Tags for specific stocks with impact (e.g. HIMS +5.51% green, IBRX -2.35% red)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                article.tags.forEach { tag ->
                    val isPos = tag.changePercent >= 0
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isPos) TradingGreenBg else TradingRedBg,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isPos) TradingGreen.copy(0.4f) else TradingRed.copy(0.4f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = tag.symbol,
                                color = TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${if (isPos) "+" else ""}${String.format(Locale.US, "%.2f", tag.changePercent)}%",
                                color = if (isPos) TradingGreenLight else TradingRedLight,
                                fontSize = 11.sp,
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
// NEWS DETAIL MODAL SHEET
// ----------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsDetailModalSheet(
    article: NewsArticle,
    onDismiss: () -> Unit
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(article.publisher, color = GoldBeige, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = article.headline,
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 26.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "${article.timeAgo} • ${article.readMinutes} min read",
                color = TextSecondary,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Impact tags
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                article.tags.forEach { tag ->
                    val isPos = tag.changePercent >= 0
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isPos) TradingGreenBg else TradingRedBg
                    ) {
                        Text(
                            text = "${tag.symbol} ${if (isPos) "+" else ""}${String.format(Locale.US, "%.2f", tag.changePercent)}%",
                            color = if (isPos) TradingGreenLight else TradingRedLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Article body summary
            Text(
                text = article.summary,
                color = TextPrimary,
                fontSize = 14.sp,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
