package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.GeminiResearchService
import com.example.brokerage.BrokerManager
import com.example.brokerage.ExecutionResult
import com.example.marketdata.MarketDataEngine
import com.example.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class TradingUiState(
    val currentTab: Int = 0, // 0: Home, 1: Watchlists, 2: Portfolio, 3: Orders, 4: AI Copilot, 5: Analytics, 6: News, 7: Profile, 8: Onboarding
    val assets: List<Asset> = emptyList(),
    val curatedWatchlists: List<CuratedWatchlist> = emptyList(),
    val customWatchlists: List<CustomWatchlist> = emptyList(),
    val activeWatchlistId: String = "curated_all",
    val newsArticles: List<NewsArticle> = emptyList(),
    val priceAlerts: List<PriceAlert> = emptyList(),
    val positions: List<TradePosition> = emptyList(),
    val orders: List<TradeOrder> = emptyList(),
    val closedTrades: List<ClosedTrade> = emptyList(),
    val fundingTransactions: List<FundingTransaction> = emptyList(),
    val notifications: List<InAppNotification> = emptyList(),
    val unreadNotifications: Int = 2,
    // Balances & Margin
    val balance: Double = 25000.00,
    val investedAmount: Double = 0.00,
    val marginUsed: Double = 0.00,
    val selectedCategory: AssetCategory = AssetCategory.ALL,
    val searchQuery: String = "",
    val isSearchOpen: Boolean = false,
    val isFabMenuOpen: Boolean = false,
    val isTradeSheetOpen: Boolean = false,
    val isPriceAlertSheetOpen: Boolean = false,
    val isDepositSheetOpen: Boolean = false,
    val isWithdrawSheetOpen: Boolean = false,
    val isHelpSheetOpen: Boolean = false,
    val isInboxSheetOpen: Boolean = false,
    val isNotificationSheetOpen: Boolean = false,
    val isBrokerSettingsOpen: Boolean = false,
    val isCustomWatchlistDialogOpen: Boolean = false,
    // Trade sheet inputs
    val selectedAssetForTrade: Asset? = null,
    val selectedTradeType: TradeType = TradeType.BUY,
    val selectedOrderType: OrderType = OrderType.MARKET,
    val tradeLotSize: Double = 1.0,
    val tradeLimitPrice: Double = 0.0,
    val tradeStopLoss: Double? = null,
    val tradeTakeProfit: Double? = null,
    val tradeLeverage: Int = 10,
    val tradeTimeInForce: TimeInForce = TimeInForce.GTC,
    // Detail and AI Research
    val selectedAssetForDetail: Asset? = null,
    val selectedNewsArticle: NewsArticle? = null,
    val currentResearchReport: AiResearchReport? = null,
    val isAiAnalyzing: Boolean = false,
    val aiChatMessages: List<AiChatMessage> = emptyList(),
    val isAiChatLoading: Boolean = false,
    // Analytics
    val analytics: PortfolioAnalytics = PortfolioAnalytics(
        totalTrades = 18,
        winningTrades = 13,
        losingTrades = 5,
        winRatePercent = 72.2,
        totalRealizedPnl = 3420.50,
        totalReturnPercent = 14.8,
        profitFactor = 2.85,
        sharpeRatio = 2.14,
        maxDrawdownPercent = 4.2,
        bestTradePnl = 1240.00,
        worstTradePnl = -310.00,
        avgWinPnl = 312.00,
        avgLossPnl = -127.00
    ),
    // Broker configuration
    val brokerConfig: BrokerConfiguration = BrokerConfiguration(),
    // Security & Biometric state
    val isAppLocked: Boolean = false,
    val isBiometricEnabled: Boolean = true,
    val requireBiometricOnLaunch: Boolean = true,
    val requireBiometricForTrade: Boolean = false,
    val appPin: String = "1234",
    val isSecuritySheetOpen: Boolean = false,
    val biometricAuthError: String? = null,
    // Onboarding flow state
    val verificationStatus: VerificationStatus = VerificationStatus.VERIFIED,
    val onboardingStep: Int = 1,
    val questionnaireSubStep: Int = 1,
    val onboardingData: OnboardingFormData = OnboardingFormData(),
    val userToastMessage: String? = null
)

class TradingViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(TradingUiState())
    val uiState: StateFlow<TradingUiState> = _uiState.asStateFlow()

    private val marketDataEngine = MarketDataEngine(viewModelScope)
    private val geminiResearchService = GeminiResearchService()
    private val brokerManager = BrokerManager()

    init {
        setupInitialCollections()
        observeMarketData()
        startLimitOrderMonitoringLoop()
        startPriceAlertMonitoringLoop()
    }

    private fun setupInitialCollections() {
        val defaultCustomWatchlists = listOf(
            CustomWatchlist("cw_tech", "Mega Cap Tech", mutableListOf("NVDA", "AAPL", "MSFT"), "🚀"),
            CustomWatchlist("cw_crypto", "Crypto Alpha", mutableListOf("BTC/USD", "ETH/USD"), "💎"),
            CustomWatchlist("cw_macro", "Safe Havens & FX", mutableListOf("GOLD", "EUR/USD", "CRUDE_OIL"), "🛡️")
        )

        val defaultCurated = listOf(
            CuratedWatchlist(
                id = "curated_top",
                title = "Most Traded Assets",
                subtitle = "Highest 24h global volume",
                assetCount = 6,
                iconEmoji = "🔥",
                accentColorHex = 0xFFF59E0B,
                sampleTickers = listOf("NVDA", "BTC/USD", "AAPL", "GOLD")
            ),
            CuratedWatchlist(
                id = "curated_tech",
                title = "AI & Semiconductors",
                subtitle = "Hardware & compute leaders",
                assetCount = 4,
                iconEmoji = "🤖",
                accentColorHex = 0xFF3B82F6,
                sampleTickers = listOf("NVDA", "MSFT", "AAPL")
            ),
            CuratedWatchlist(
                id = "curated_crypto",
                title = "Crypto Blue Chips",
                subtitle = "Decentralized stores of value",
                assetCount = 3,
                iconEmoji = "🪙",
                accentColorHex = 0xFF10B981,
                sampleTickers = listOf("BTC/USD", "ETH/USD")
            )
        )

        val defaultNews = listOf(
            NewsArticle(
                id = "n1",
                headline = "Fed Holds Benchmark Rates Steady as Core Inflation Moderates",
                subtitle = "Federal Open Market Committee signals potential rate cuts as labor market achieves balance.",
                timeAgo = "12m ago",
                publisher = "Bloomberg Markets",
                category = "Macro & Policy",
                tags = listOf(StockImpactTag("US500", 0.92), StockImpactTag("GOLD", 0.78)),
                summary = "The Federal Reserve kept interest rates unchanged in its latest policy decision while opening the door to easing later this year. Equities rallied with tech leading gains.",
                readMinutes = 3,
                sentiment = NewsSentiment.BULLISH
            ),
            NewsArticle(
                id = "n2",
                headline = "NVIDIA Surges as Next-Gen Blackwell AI Data Center Shipments Accelerate",
                subtitle = "Cloud hyperscalers expand multi-billion dollar capital expenditure commitments.",
                timeAgo = "45m ago",
                publisher = "Financial Times",
                category = "Technology",
                tags = listOf(StockImpactTag("NVDA", 4.32), StockImpactTag("MSFT", 1.82)),
                summary = "Demand for next-generation AI accelerators continues to outstrip supply, driving record gross margins for compute infrastructure leaders.",
                readMinutes = 2,
                sentiment = NewsSentiment.BULLISH
            ),
            NewsArticle(
                id = "n3",
                headline = "Bitcoin Consolidates Above $96,000 as Institutional Spot Inflows Top $2.4B",
                subtitle = "Corporate treasuries and ETF aggregators continue high-velocity accumulation.",
                timeAgo = "2h ago",
                publisher = "CoinDesk Global",
                category = "Cryptocurrency",
                tags = listOf(StockImpactTag("BTC/USD", 3.25), StockImpactTag("ETH/USD", 1.94)),
                summary = "Spot crypto ETFs registered net positive flows for the 14th consecutive trading session, underscoring resilient institutional demand.",
                readMinutes = 4,
                sentiment = NewsSentiment.BULLISH
            )
        )

        val initialPositions = listOf(
            TradePosition(
                id = "pos_nvda",
                assetSymbol = "NVDA",
                assetName = "NVIDIA Corporation",
                type = TradeType.BUY,
                units = 15.0,
                entryPrice = 138.20,
                currentPrice = 142.50,
                pnl = (142.50 - 138.20) * 15.0,
                pnlPercent = ((142.50 - 138.20) / 138.20) * 100.0,
                leverage = 10,
                stopLoss = 132.00,
                takeProfit = 155.00
            ),
            TradePosition(
                id = "pos_btc",
                assetSymbol = "BTC/USD",
                assetName = "Bitcoin",
                type = TradeType.BUY,
                units = 0.25,
                entryPrice = 94800.00,
                currentPrice = 96450.00,
                pnl = (96450.00 - 94800.00) * 0.25,
                pnlPercent = ((96450.00 - 94800.00) / 94800.00) * 100.0,
                leverage = 5,
                stopLoss = 91000.00,
                takeProfit = 102000.00
            )
        )

        val initialClosedTrades = listOf(
            ClosedTrade(
                id = "cl_1",
                assetSymbol = "AAPL",
                assetName = "Apple Inc.",
                type = TradeType.BUY,
                units = 20.0,
                entryPrice = 224.50,
                exitPrice = 232.80,
                realizedPnl = (232.80 - 224.50) * 20.0,
                returnPercent = 3.7,
                leverage = 5,
                closeTime = System.currentTimeMillis() - 86400000L
            ),
            ClosedTrade(
                id = "cl_2",
                assetSymbol = "GOLD",
                assetName = "Gold (Spot Ounce)",
                type = TradeType.BUY,
                units = 5.0,
                entryPrice = 2710.00,
                exitPrice = 2748.50,
                realizedPnl = (2748.50 - 2710.00) * 5.0,
                returnPercent = 1.42,
                leverage = 10,
                closeTime = System.currentTimeMillis() - 172800000L
            )
        )

        val initialOrders = listOf(
            TradeOrder(
                id = "ord_limit_tsla",
                assetSymbol = "TSLA",
                assetName = "Tesla Inc.",
                tradeType = TradeType.BUY,
                orderType = OrderType.LIMIT,
                status = OrderStatus.PENDING,
                units = 10.0,
                targetPrice = 235.00,
                leverage = 5,
                brokerName = "Golden Compass Direct DMA"
            )
        )

        val initialNotifications = listOf(
            InAppNotification(
                id = "notif_1",
                title = "Order Filled: NVDA",
                message = "Bought 15.0 units NVDA @ $138.20 via Golden Compass DMA",
                timeAgo = "10m ago",
                type = NotificationType.TRADE_FILL,
                isRead = false
            ),
            InAppNotification(
                id = "notif_2",
                title = "Market Milestone: BTC",
                message = "Bitcoin broke above $96,000 with strong volume",
                timeAgo = "1h ago",
                type = NotificationType.PRICE_ALERT,
                isRead = false
            )
        )

        val invested = initialPositions.sumOf { (it.entryPrice * it.units) / it.leverage }
        val margin = invested

        _uiState.update {
            it.copy(
                customWatchlists = defaultCustomWatchlists,
                curatedWatchlists = defaultCurated,
                newsArticles = defaultNews,
                positions = initialPositions,
                closedTrades = initialClosedTrades,
                orders = initialOrders,
                notifications = initialNotifications,
                investedAmount = invested,
                marginUsed = margin
            )
        }
    }

    private fun observeMarketData() {
        viewModelScope.launch {
            marketDataEngine.assets.collect { latestAssets ->
                if (latestAssets.isNotEmpty()) {
                    _uiState.update { state ->
                        val updatedPositions = state.positions.map { pos ->
                            val currentAsset = latestAssets.find { it.symbol == pos.assetSymbol }
                            if (currentAsset != null) {
                                val curPrice = currentAsset.price
                                val diff = if (pos.type == TradeType.BUY) (curPrice - pos.entryPrice) else (pos.entryPrice - curPrice)
                                val pnl = diff * pos.units * pos.leverage
                                val pnlPct = (diff / pos.entryPrice) * 100.0 * pos.leverage
                                pos.copy(
                                    currentPrice = curPrice,
                                    pnl = pnl,
                                    pnlPercent = pnlPct
                                )
                            } else pos
                        }

                        val invested = updatedPositions.sumOf { (it.entryPrice * it.units) / it.leverage }

                        state.copy(
                            assets = latestAssets,
                            positions = updatedPositions,
                            investedAmount = invested,
                            marginUsed = invested
                        )
                    }
                }
            }
        }
    }

    private fun startLimitOrderMonitoringLoop() {
        viewModelScope.launch {
            while (true) {
                delay(3000)
                val state = _uiState.value
                val pendingOrders = state.orders.filter { it.status == OrderStatus.PENDING && it.orderType == OrderType.LIMIT }
                if (pendingOrders.isNotEmpty()) {
                    for (order in pendingOrders) {
                        val asset = state.assets.find { it.symbol == order.assetSymbol }
                        if (asset != null && order.targetPrice != null) {
                            val trigger = if (order.tradeType == TradeType.BUY) {
                                asset.price <= order.targetPrice
                            } else {
                                asset.price >= order.targetPrice
                            }

                            if (trigger) {
                                executePendingOrder(order, asset.price)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun executePendingOrder(order: TradeOrder, fillPrice: Double) {
        val newPosition = TradePosition(
            id = "pos_" + UUID.randomUUID().toString().take(6),
            assetSymbol = order.assetSymbol,
            assetName = order.assetName,
            type = order.tradeType,
            units = order.units,
            entryPrice = fillPrice,
            currentPrice = fillPrice,
            pnl = 0.0,
            pnlPercent = 0.0,
            leverage = order.leverage,
            stopLoss = order.stopLoss,
            takeProfit = order.takeProfit
        )

        val updatedOrders = _uiState.value.orders.map {
            if (it.id == order.id) it.copy(status = OrderStatus.FILLED, executedPrice = fillPrice) else it
        }

        val notif = InAppNotification(
            id = "notif_" + UUID.randomUUID().toString().take(6),
            title = "Limit Order Filled: ${order.assetSymbol}",
            message = "${order.tradeType.name} ${order.units} @ $${String.format("%.2f", fillPrice)}",
            timeAgo = "Just now",
            type = NotificationType.TRADE_FILL,
            isRead = false
        )

        _uiState.update {
            it.copy(
                orders = updatedOrders,
                positions = it.positions + newPosition,
                notifications = listOf(notif) + it.notifications,
                unreadNotifications = it.unreadNotifications + 1,
                userToastMessage = "Limit Order Filled: ${order.assetSymbol} @ $${String.format("%.2f", fillPrice)}"
            )
        }
    }

    private fun startPriceAlertMonitoringLoop() {
        viewModelScope.launch {
            while (true) {
                delay(4000)
                val state = _uiState.value
                state.priceAlerts.filter { it.isActive }.forEach { alert ->
                    val asset = state.assets.find { it.symbol == alert.assetSymbol }
                    if (asset != null) {
                        val triggered = if (alert.isAbove) asset.price >= alert.targetPrice else asset.price <= alert.targetPrice
                        if (triggered) {
                            val notif = InAppNotification(
                                id = "notif_alert_" + UUID.randomUUID().toString().take(6),
                                title = "Price Alert: ${alert.assetSymbol}",
                                message = "${alert.assetSymbol} target of $${String.format("%.2f", alert.targetPrice)} has been met! (Current: $${String.format("%.2f", asset.price)})",
                                timeAgo = "Just now",
                                type = NotificationType.PRICE_ALERT,
                                isRead = false
                            )

                            val updatedAlerts = state.priceAlerts.map { if (it.id == alert.id) it.copy(isActive = false) else it }
                            _uiState.update {
                                it.copy(
                                    priceAlerts = updatedAlerts,
                                    notifications = listOf(notif) + it.notifications,
                                    unreadNotifications = it.unreadNotifications + 1,
                                    userToastMessage = "🔔 Price Alert Hit: ${alert.assetSymbol} $${String.format("%.2f", alert.targetPrice)}"
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // --- NAVIGATION & TABS ---
    fun setTab(tabIndex: Int) {
        _uiState.update { it.copy(currentTab = tabIndex) }
    }

    fun setSelectedCategory(cat: AssetCategory) {
        _uiState.update { it.copy(selectedCategory = cat) }
    }

    fun toggleSearch(open: Boolean) {
        _uiState.update { it.copy(isSearchOpen = open, searchQuery = if (!open) "" else it.searchQuery) }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun toggleFabMenu(open: Boolean? = null) {
        _uiState.update { it.copy(isFabMenuOpen = open ?: !it.isFabMenuOpen) }
    }

    // --- TRADE EXECUTION ---
    fun openTradeSheet(asset: Asset, tradeType: TradeType = TradeType.BUY) {
        _uiState.update {
            it.copy(
                selectedAssetForTrade = asset,
                selectedTradeType = tradeType,
                isTradeSheetOpen = true,
                isFabMenuOpen = false,
                tradeLotSize = 1.0,
                tradeLimitPrice = asset.price,
                tradeLeverage = 10
            )
        }
    }

    fun closeTradeSheet() {
        _uiState.update { it.copy(isTradeSheetOpen = false, selectedAssetForTrade = null) }
    }

    fun executeTrade() {
        val state = _uiState.value
        val asset = state.selectedAssetForTrade ?: return
        val units = state.tradeLotSize
        val leverage = state.tradeLeverage
        val requiredMargin = (asset.price * units) / leverage

        if (requiredMargin > state.balance) {
            _uiState.update { it.copy(userToastMessage = "Insufficient margin! Deposit funds to trade.") }
            return
        }

        viewModelScope.launch {
            val adapter = brokerManager.getAdapter(state.brokerConfig)
            val execution = adapter.routeOrder(
                assetSymbol = asset.symbol,
                assetName = asset.name,
                tradeType = state.selectedTradeType,
                orderType = state.selectedOrderType,
                units = units,
                currentMarketPrice = asset.price,
                targetPrice = if (state.selectedOrderType == OrderType.LIMIT) state.tradeLimitPrice else null,
                stopLoss = state.tradeStopLoss,
                takeProfit = state.tradeTakeProfit,
                leverage = leverage
            )

            if (execution is ExecutionResult.Filled) {
                val filledOrder = execution.order
                val fillPrice = execution.fillPrice
                val newPosition = TradePosition(
                    id = "pos_" + UUID.randomUUID().toString().take(6),
                    assetSymbol = asset.symbol,
                    assetName = asset.name,
                    type = state.selectedTradeType,
                    units = units,
                    entryPrice = fillPrice,
                    currentPrice = fillPrice,
                    pnl = 0.0,
                    pnlPercent = 0.0,
                    leverage = leverage,
                    stopLoss = state.tradeStopLoss,
                    takeProfit = state.tradeTakeProfit
                )

                val notif = InAppNotification(
                    id = "notif_" + UUID.randomUUID().toString().take(6),
                    title = "Order Executed: ${asset.symbol}",
                    message = "${state.selectedTradeType.name} ${units} units filled @ $${String.format("%.2f", fillPrice)}",
                    timeAgo = "Just now",
                    type = NotificationType.TRADE_FILL,
                    isRead = false
                )

                _uiState.update {
                    it.copy(
                        balance = it.balance - requiredMargin,
                        positions = listOf(newPosition) + it.positions,
                        orders = listOf(filledOrder) + it.orders,
                        notifications = listOf(notif) + it.notifications,
                        unreadNotifications = it.unreadNotifications + 1,
                        isTradeSheetOpen = false,
                        userToastMessage = "${state.selectedTradeType.name} ${units} ${asset.symbol} filled successfully!"
                    )
                }
            } else if (execution is ExecutionResult.Pending) {
                val pendingOrder = execution.order
                _uiState.update {
                    it.copy(
                        orders = listOf(pendingOrder) + it.orders,
                        isTradeSheetOpen = false,
                        userToastMessage = "Limit order placed: ${pendingOrder.assetSymbol} @ $${String.format("%.2f", pendingOrder.targetPrice ?: 0.0)}"
                    )
                }
            } else if (execution is ExecutionResult.Rejected) {
                _uiState.update {
                    it.copy(userToastMessage = "Broker rejected order: ${execution.reason}")
                }
            }
        }
    }

    fun closePosition(positionId: String) {
        val state = _uiState.value
        val position = state.positions.find { it.id == positionId } ?: return

        val closed = ClosedTrade(
            id = "cl_" + UUID.randomUUID().toString().take(6),
            assetSymbol = position.assetSymbol,
            assetName = position.assetName,
            type = position.type,
            units = position.units,
            entryPrice = position.entryPrice,
            exitPrice = position.currentPrice,
            realizedPnl = position.pnl,
            returnPercent = position.pnlPercent,
            leverage = position.leverage,
            closeTime = System.currentTimeMillis()
        )

        val returnedMargin = (position.entryPrice * position.units) / position.leverage + position.pnl

        val notif = InAppNotification(
            id = "notif_" + UUID.randomUUID().toString().take(6),
            title = "Position Closed: ${position.assetSymbol}",
            message = "Realized P&L: ${if (position.pnl >= 0) "+" else ""}$${String.format("%.2f", position.pnl)}",
            timeAgo = "Just now",
            type = NotificationType.TRADE_FILL,
            isRead = false
        )

        _uiState.update {
            it.copy(
                balance = it.balance + returnedMargin,
                positions = it.positions.filterNot { p -> p.id == positionId },
                closedTrades = listOf(closed) + it.closedTrades,
                notifications = listOf(notif) + it.notifications,
                unreadNotifications = it.unreadNotifications + 1,
                userToastMessage = "Closed ${position.assetSymbol} (${if (position.pnl >= 0) "+" else ""}$${String.format("%.2f", position.pnl)})"
            )
        }
    }

    fun cancelOrder(orderId: String) {
        _uiState.update { state ->
            val updated = state.orders.map {
                if (it.id == orderId) it.copy(status = OrderStatus.CANCELLED) else it
            }
            state.copy(
                orders = updated,
                userToastMessage = "Order cancelled"
            )
        }
    }

    // --- ASSET DETAIL & AI RESEARCH ---
    fun openAssetDetail(asset: Asset) {
        _uiState.update { it.copy(selectedAssetForDetail = asset) }
    }

    fun closeAssetDetail() {
        _uiState.update { it.copy(selectedAssetForDetail = null) }
    }

    fun analyzeAsset(asset: Asset) {
        viewModelScope.launch {
            _uiState.update { it.copy(isAiAnalyzing = true) }
            val reportResult = geminiResearchService.generateStockResearch(asset)
            val report = reportResult.getOrNull()
            _uiState.update {
                it.copy(
                    currentResearchReport = report,
                    isAiAnalyzing = false,
                    userToastMessage = "AI Quantitative report generated for ${asset.symbol}"
                )
            }
        }
    }

    fun sendAiQuery(query: String) {
        if (query.isBlank()) return
        val userMsg = AiChatMessage(
            id = UUID.randomUUID().toString(),
            text = query,
            isUser = true
        )

        _uiState.update {
            it.copy(
                aiChatMessages = it.aiChatMessages + userMsg,
                isAiChatLoading = true
            )
        }

        viewModelScope.launch {
            val contextAsset = _uiState.value.selectedAssetForDetail ?: _uiState.value.assets.firstOrNull()
            val responseResult = geminiResearchService.queryMarketChatbot(query, contextAsset, _uiState.value.aiChatMessages)
            val responseText = responseResult.getOrDefault("I'm analyzing the current order book and macroeconomic metrics. Let's look at key resistance levels.")
            val aiMsg = AiChatMessage(
                id = UUID.randomUUID().toString(),
                text = responseText,
                isUser = false
            )
            _uiState.update {
                it.copy(
                    aiChatMessages = it.aiChatMessages + aiMsg,
                    isAiChatLoading = false
                )
            }
        }
    }

    // --- CUSTOM WATCHLISTS ---
    fun toggleCustomWatchlistDialog(open: Boolean) {
        _uiState.update { it.copy(isCustomWatchlistDialogOpen = open) }
    }

    fun createCustomWatchlist(name: String, icon: String, symbols: List<String>) {
        if (name.isBlank()) return
        val newWl = CustomWatchlist(
            id = "cw_" + UUID.randomUUID().toString().take(6),
            name = name,
            icon = icon.ifBlank { "⭐" },
            assetSymbols = symbols.toMutableList()
        )
        _uiState.update {
            it.copy(
                customWatchlists = it.customWatchlists + newWl,
                isCustomWatchlistDialogOpen = false,
                userToastMessage = "Created watchlist: $name"
            )
        }
    }

    fun deleteCustomWatchlist(id: String) {
        _uiState.update {
            it.copy(
                customWatchlists = it.customWatchlists.filterNot { w -> w.id == id },
                userToastMessage = "Watchlist removed"
            )
        }
    }

    fun toggleAssetInCustomWatchlist(watchlistId: String, symbol: String) {
        _uiState.update { state ->
            val updated = state.customWatchlists.map { wl ->
                if (wl.id == watchlistId) {
                    val newList = wl.assetSymbols.toMutableList()
                    if (newList.contains(symbol)) {
                        newList.remove(symbol)
                    } else {
                        newList.add(symbol)
                    }
                    wl.copy(assetSymbols = newList)
                } else wl
            }
            state.copy(customWatchlists = updated)
        }
    }

    // --- FUNDING FLOWS ---
    fun toggleDepositSheet(open: Boolean) {
        _uiState.update { it.copy(isDepositSheetOpen = open, isFabMenuOpen = false) }
    }

    fun toggleWithdrawSheet(open: Boolean) {
        _uiState.update { it.copy(isWithdrawSheetOpen = open, isFabMenuOpen = false) }
    }

    fun depositFunds(amount: Double, method: String = "Instant Card / Apple Pay") {
        if (amount <= 0) return
        val txn = FundingTransaction(
            id = "dep_" + UUID.randomUUID().toString().take(6),
            type = TransactionType.DEPOSIT,
            amount = amount,
            method = method
        )
        _uiState.update {
            it.copy(
                balance = it.balance + amount,
                fundingTransactions = listOf(txn) + it.fundingTransactions,
                isDepositSheetOpen = false,
                userToastMessage = "Deposited $${String.format("%.2f", amount)} successfully"
            )
        }
    }

    fun withdrawFunds(amount: Double, destination: String = "Bank Wire (Chase ****4821)") {
        if (amount <= 0 || amount > _uiState.value.balance) {
            _uiState.update { it.copy(userToastMessage = "Invalid withdrawal amount") }
            return
        }
        val txn = FundingTransaction(
            id = "wth_" + UUID.randomUUID().toString().take(6),
            type = TransactionType.WITHDRAWAL,
            amount = amount,
            method = destination
        )
        _uiState.update {
            it.copy(
                balance = it.balance - amount,
                fundingTransactions = listOf(txn) + it.fundingTransactions,
                isWithdrawSheetOpen = false,
                userToastMessage = "Withdrawal request of $${String.format("%.2f", amount)} submitted"
            )
        }
    }

    // --- BROKER CONFIGURATION ---
    fun toggleBrokerSettings(open: Boolean) {
        _uiState.update { it.copy(isBrokerSettingsOpen = open) }
    }

    fun updateBrokerConfig(newConfig: BrokerConfiguration) {
        _uiState.update {
            it.copy(
                brokerConfig = newConfig,
                isBrokerSettingsOpen = false,
                userToastMessage = "Connected to ${newConfig.selectedBroker.displayName} (${if (newConfig.isLiveAccount) "LIVE" else "SANDBOX"})"
            )
        }
    }

    // --- PRICE ALERTS ---
    fun togglePriceAlertSheet(open: Boolean) {
        _uiState.update { it.copy(isPriceAlertSheetOpen = open, isFabMenuOpen = false) }
    }

    fun addPriceAlert(assetSymbol: String, targetPrice: Double, isAbove: Boolean) {
        val asset = _uiState.value.assets.find { it.symbol == assetSymbol }
        val newAlert = PriceAlert(
            id = "alert_" + UUID.randomUUID().toString().take(6),
            assetSymbol = assetSymbol,
            assetName = asset?.name ?: assetSymbol,
            targetPrice = targetPrice,
            isAbove = isAbove
        )
        _uiState.update {
            it.copy(
                priceAlerts = it.priceAlerts + newAlert,
                isPriceAlertSheetOpen = false,
                userToastMessage = "Alert set for $assetSymbol at $${String.format("%.2f", targetPrice)}"
            )
        }
    }

    fun deletePriceAlert(alertId: String) {
        _uiState.update {
            it.copy(
                priceAlerts = it.priceAlerts.filterNot { a -> a.id == alertId },
                userToastMessage = "Alert removed"
            )
        }
    }

    // --- NOTIFICATIONS & INBOX ---
    fun toggleNotificationSheet(open: Boolean) {
        _uiState.update { it.copy(isNotificationSheetOpen = open, isFabMenuOpen = false) }
    }

    fun markNotificationsRead() {
        _uiState.update { state ->
            val updated = state.notifications.map { it.copy(isRead = true) }
            state.copy(notifications = updated, unreadNotifications = 0)
        }
    }

    fun clearNotifications() {
        _uiState.update {
            it.copy(notifications = emptyList(), unreadNotifications = 0)
        }
    }

    fun toggleHelpSheet(open: Boolean) {
        _uiState.update { it.copy(isHelpSheetOpen = open, isFabMenuOpen = false) }
    }

    fun openNewsDetail(article: NewsArticle) {
        _uiState.update { it.copy(selectedNewsArticle = article) }
    }

    fun closeNewsDetail() {
        _uiState.update { it.copy(selectedNewsArticle = null) }
    }

    fun clearToastMessage() {
        _uiState.update { it.copy(userToastMessage = null) }
    }

    // --- SECURITY & BIOMETRIC AUTH ---
    fun unlockApp() {
        _uiState.update {
            it.copy(
                isAppLocked = false,
                biometricAuthError = null,
                userToastMessage = "Identity verified. Portfolio unlocked."
            )
        }
    }

    fun lockApp() {
        _uiState.update {
            it.copy(
                isAppLocked = true,
                biometricAuthError = null
            )
        }
    }

    fun setBiometricAuthError(error: String?) {
        _uiState.update { it.copy(biometricAuthError = error) }
    }

    fun toggleSecuritySheet(open: Boolean) {
        _uiState.update { it.copy(isSecuritySheetOpen = open, isFabMenuOpen = false) }
    }

    fun setBiometricEnabled(enabled: Boolean) {
        _uiState.update {
            it.copy(
                isBiometricEnabled = enabled,
                userToastMessage = if (enabled) "Biometric unlock enabled" else "Biometric unlock disabled"
            )
        }
    }

    fun setRequireBiometricOnLaunch(require: Boolean) {
        _uiState.update { it.copy(requireBiometricOnLaunch = require) }
    }

    fun setRequireBiometricForTrade(require: Boolean) {
        _uiState.update { it.copy(requireBiometricForTrade = require) }
    }

    fun setAppPin(newPin: String) {
        _uiState.update {
            it.copy(
                appPin = newPin,
                userToastMessage = "Passcode PIN updated successfully"
            )
        }
    }

    // --- ONBOARDING FLOW ---
    fun startOnboarding(step: Int = 1) {
        _uiState.update { it.copy(currentTab = 8, onboardingStep = step) }
    }

    fun setOnboardingStep(step: Int) {
        _uiState.update { it.copy(onboardingStep = step) }
    }

    fun setQuestionnaireSubStep(subStep: Int) {
        _uiState.update { it.copy(questionnaireSubStep = subStep) }
    }

    fun onKeypadDigit(digit: String) {
        _uiState.update { state ->
            val cur = state.onboardingData.phoneNumber
            if (cur.length < 12) {
                state.copy(onboardingData = state.onboardingData.copy(phoneNumber = cur + digit))
            } else state
        }
    }

    fun onKeypadDelete() {
        _uiState.update { state ->
            val cur = state.onboardingData.phoneNumber
            if (cur.isNotEmpty()) {
                state.copy(onboardingData = state.onboardingData.copy(phoneNumber = cur.dropLast(1)))
            } else state
        }
    }

    fun setCountry(code: String, name: String, flag: String) {
        _uiState.update { state ->
            state.copy(onboardingData = state.onboardingData.copy(countryCode = code, countryName = name, countryFlag = flag))
        }
    }

    fun togglePolicy(policy: String) {
        _uiState.update { state ->
            val cur = state.onboardingData.acceptedPolicies.toMutableSet()
            if (cur.contains(policy)) cur.remove(policy) else cur.add(policy)
            state.copy(onboardingData = state.onboardingData.copy(acceptedPolicies = cur))
        }
    }

    fun acceptAllPolicies(policies: List<String>) {
        _uiState.update { state ->
            state.copy(onboardingData = state.onboardingData.copy(acceptedPolicies = policies.toSet()))
        }
    }

    fun setDocumentUploaded(isId: Boolean, value: Boolean) {
        _uiState.update { state ->
            val data = if (isId) state.onboardingData.copy(idUploaded = value) else state.onboardingData.copy(selfieTaken = value)
            state.copy(onboardingData = data)
        }
    }

    fun setEmploymentStatus(status: String) {
        _uiState.update { state -> state.copy(onboardingData = state.onboardingData.copy(employmentStatus = status)) }
    }

    fun setCfdExperience(exp: String) {
        _uiState.update { state -> state.copy(onboardingData = state.onboardingData.copy(cfdExperience = exp)) }
    }

    fun toggleQualification(q: String) {
        _uiState.update { state ->
            val cur = state.onboardingData.qualifications.toMutableSet()
            if (cur.contains(q)) cur.remove(q) else cur.add(q)
            state.copy(onboardingData = state.onboardingData.copy(qualifications = cur))
        }
    }

    fun setIndustry(ind: String) {
        _uiState.update { state -> state.copy(onboardingData = state.onboardingData.copy(industry = ind)) }
    }

    fun setGrossIncome(income: String) {
        _uiState.update { state -> state.copy(onboardingData = state.onboardingData.copy(grossIncome = income)) }
    }

    fun toggleWealthOrigin(origin: String) {
        _uiState.update { state ->
            val cur = state.onboardingData.originOfWealth.toMutableSet()
            if (cur.contains(origin)) cur.remove(origin) else cur.add(origin)
            state.copy(onboardingData = state.onboardingData.copy(originOfWealth = cur))
        }
    }

    fun setCurrency(name: String, code: String) {
        _uiState.update { state ->
            state.copy(onboardingData = state.onboardingData.copy(accountCurrency = name, currencyCode = code))
        }
    }

    fun completeOnboarding() {
        _uiState.update {
            it.copy(
                verificationStatus = VerificationStatus.VERIFIED,
                currentTab = 0,
                userToastMessage = "🎉 Onboarding and Verification complete! Live Trading is now enabled."
            )
        }
    }
}
