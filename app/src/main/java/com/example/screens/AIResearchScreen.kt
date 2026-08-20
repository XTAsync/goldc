package com.example.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AiChatMessage
import com.example.model.AiResearchReport
import com.example.model.Asset
import com.example.model.TradeType
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AIResearchScreen(
    assets: List<Asset>,
    selectedAsset: Asset?,
    aiReport: AiResearchReport?,
    isLoadingReport: Boolean,
    chatMessages: List<AiChatMessage>,
    isLoadingChat: Boolean,
    onSelectAsset: (Asset) -> Unit,
    onSendMessage: (String) -> Unit,
    onBuy: (Asset) -> Unit,
    onSell: (Asset) -> Unit,
    modifier: Modifier = Modifier
) {
    var viewMode by remember { mutableStateOf(0) } // 0: Deep Stock Analysis, 1: AI Market Copilot Chat
    var inputQuery by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val currentAsset = selectedAsset ?: assets.firstOrNull()

    // Auto-scroll chat on new message
    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty() && viewMode == 1) {
            coroutineScope.launch {
                listState.animateScrollToItem(chatMessages.size - 1)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        // Hero Header with Gemini Badge
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1E293B),
                            BackgroundDark
                        )
                    )
                )
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = AccentTeal.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = AccentTeal,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "GEMINI 3.5 FLASH",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AccentTeal,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "AI Quantitative Research",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    color = SurfaceElevated,
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(TradingGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Live Model",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Mode Switcher (Deep Dive Report vs AI Assistant Chat)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .background(SurfaceElevated, RoundedCornerShape(12.dp))
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (viewMode == 0) AccentTeal else Color.Transparent)
                    .clickable { viewMode = 0 }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Stock Deep Dive",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (viewMode == 0) BackgroundDark else TextSecondary,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (viewMode == 1) AccentTeal else Color.Transparent)
                    .clickable { viewMode = 1 }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "AI Trading Copilot",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (viewMode == 1) BackgroundDark else TextSecondary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (viewMode == 0) {
            // STOCK DEEP DIVE REPORT VIEW
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Horizontal Ticker Pill Selector
                item {
                    Text(
                        text = "Select Asset to Analyze",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(assets) { asset ->
                            val isSelected = currentAsset?.id == asset.id
                            Surface(
                                color = if (isSelected) AccentTeal else SurfaceElevated,
                                shape = RoundedCornerShape(20.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) AccentTeal else BorderDark
                                ),
                                modifier = Modifier.clickable { onSelectAsset(asset) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = asset.iconEmoji, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = asset.symbol,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (isSelected) BackgroundDark else TextPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                if (isLoadingReport) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(color = AccentTeal)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Analyzing ${currentAsset?.symbol ?: "Asset"}...",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Synthesizing fundamental metrics, risk factor matrix, and technical indicators with Gemini 3.5 Flash",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                } else if (aiReport != null && currentAsset != null) {
                    // Rating & Summary Hero Card
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
                                            text = "${currentAsset.name} (${currentAsset.symbol})",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = TextPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "$${currentAsset.price} • ${if (currentAsset.changePercent >= 0) "+" else ""}${currentAsset.changePercent}% today",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (currentAsset.changePercent >= 0) TradingGreen else TradingRed,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    Surface(
                                        color = when (aiReport.rating) {
                                            "STRONG BUY" -> TradingGreen
                                            "BUY" -> TradingGreen.copy(alpha = 0.8f)
                                            "SELL" -> TradingRed
                                            else -> Color(0xFFF59E0B)
                                        },
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = aiReport.rating,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = BackgroundDark,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = aiReport.summary,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextPrimary,
                                    lineHeight = 20.sp
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Quick Action Buttons
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Button(
                                        onClick = { onBuy(currentAsset) },
                                        colors = ButtonDefaults.buttonColors(containerColor = TradingGreen, contentColor = Color.White),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Buy ${currentAsset.symbol}", fontWeight = FontWeight.Bold)
                                    }
                                    Button(
                                        onClick = { onSell(currentAsset) },
                                        colors = ButtonDefaults.buttonColors(containerColor = TradingRed, contentColor = Color.White),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Sell / Short", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // Bull Case Breakdown
                    item {
                        Surface(
                            color = SurfaceElevated,
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.TrendingUp,
                                        contentDescription = null,
                                        tint = TradingGreen,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Institutional Bull Thesis",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = TradingGreen,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                aiReport.bullCase.forEach { point ->
                                    Row(
                                        modifier = Modifier.padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Text(text = "• ", color = TradingGreen, fontWeight = FontWeight.Bold)
                                        Text(text = point, style = MaterialTheme.typography.bodySmall, color = TextPrimary, lineHeight = 18.sp)
                                    }
                                }
                            }
                        }
                    }

                    // Bear Case Breakdown
                    item {
                        Surface(
                            color = SurfaceElevated,
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.TrendingDown,
                                        contentDescription = null,
                                        tint = TradingRed,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Risk Factors & Bear Thesis",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = TradingRed,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                aiReport.bearCase.forEach { point ->
                                    Row(
                                        modifier = Modifier.padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Text(text = "• ", color = TradingRed, fontWeight = FontWeight.Bold)
                                        Text(text = point, style = MaterialTheme.typography.bodySmall, color = TextPrimary, lineHeight = 18.sp)
                                    }
                                }
                            }
                        }
                    }

                    // Technical Analysis & Catalysts
                    item {
                        Surface(
                            color = SurfaceElevated,
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Technical Verdict & Momentum",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = AccentCyan,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = aiReport.technicalVerdict,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextPrimary,
                                    lineHeight = 18.sp
                                )

                                Spacer(modifier = Modifier.height(14.dp))
                                HorizontalDivider(color = BorderDark)
                                Spacer(modifier = Modifier.height(14.dp))

                                Text(
                                    text = "Key Upcoming Catalysts",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                aiReport.keyCatalysts.forEach { catalyst ->
                                    Row(
                                        modifier = Modifier.padding(vertical = 3.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Text(text = "⚡ ", fontSize = 12.sp)
                                        Text(text = catalyst, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // INTERACTIVE AI COPILOT CHAT VIEW
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(chatMessages, key = { it.id }) { msg ->
                        AiChatMessageBubble(message = msg)
                    }

                    if (isLoadingChat) {
                        item {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    color = AccentTeal,
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Gemini is analyzing market data...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }

                // Quick Prompt Suggestion Chips
                LazyRow(
                    modifier = Modifier.padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val suggestions = listOf(
                        "Analyze ${currentAsset?.symbol ?: "NVDA"} technicals",
                        "Best entry price & stop-loss?",
                        "How to hedge market volatility?",
                        "Macro outlook on inflation & Fed"
                    )
                    items(suggestions) { suggestion ->
                        Surface(
                            color = SurfaceElevated,
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
                            modifier = Modifier.clickable { onSendMessage(suggestion) }
                        ) {
                            Text(
                                text = suggestion,
                                style = MaterialTheme.typography.labelSmall,
                                color = AccentTeal,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // Input Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = inputQuery,
                        onValueChange = { inputQuery = it },
                        placeholder = { Text("Ask Gemini about any asset, strategy...", color = TextMuted) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = SurfaceElevated,
                            unfocusedContainerColor = SurfaceElevated,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, BorderDark, RoundedCornerShape(24.dp))
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (inputQuery.isNotBlank()) {
                                onSendMessage(inputQuery)
                                inputQuery = ""
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .background(AccentTeal, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send",
                            tint = BackgroundDark
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AiChatMessageBubble(message: AiChatMessage) {
    val isUser = message.isUser

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Surface(
                modifier = Modifier.size(32.dp),
                shape = CircleShape,
                color = AccentTeal.copy(alpha = 0.2f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = AccentTeal,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Surface(
            color = if (isUser) AccentTeal else SurfaceElevated,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            border = if (!isUser) androidx.compose.foundation.BorderStroke(1.dp, BorderDark) else null,
            modifier = Modifier.widthIn(max = 290.dp)
        ) {
            Text(
                text = message.text,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isUser) BackgroundDark else TextPrimary,
                lineHeight = 20.sp,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
            )
        }
    }
}
