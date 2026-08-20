package com.example.model

enum class AssetCategory(val displayName: String) {
    ALL("All"),
    MOST_TRADED("Most traded"),
    SHARES("Stocks"),
    CRYPTO("Crypto"),
    COMMODITIES("Commodities"),
    INDICES("Indices"),
    CURRENCIES("Forex")
}

data class Asset(
    val id: String,
    val symbol: String,
    val name: String,
    val category: AssetCategory,
    val price: Double,
    val changePercent: Double,
    val changeAmount: Double,
    val sparklinePoints: List<Float>,
    val high24h: Double,
    val low24h: Double,
    val volume: String,
    val marketCap: String,
    val isClosed: Boolean = false,
    val isMostTraded: Boolean = false,
    val isMostVolatile: Boolean = false,
    val iconEmoji: String = "📈",
    val description: String = "",
    // Fundamentals & In-depth Research
    val peRatio: Double = 28.4,
    val forwardPe: Double = 24.1,
    val eps: Double = 6.42,
    val dividendYield: Double = 0.55,
    val beta: Double = 1.18,
    val week52High: Double = price * 1.25,
    val week52Low: Double = price * 0.78,
    val avgVolume: String = "48.2M",
    val analystRating: String = "Strong Buy",
    val analystTargetPrice: Double = price * 1.15,
    val analystBuyCount: Int = 28,
    val analystHoldCount: Int = 6,
    val analystSellCount: Int = 2,
    val sector: String = "Technology",
    val bidPrice: Double = price * 0.9998,
    val askPrice: Double = price * 1.0002,
    val bidVolume: Int = 1450,
    val askVolume: Int = 1820
)

data class OrderBookLevel(
    val price: Double,
    val size: Double,
    val total: Double,
    val isBid: Boolean
)

data class CuratedWatchlist(
    val id: String,
    val title: String,
    val subtitle: String,
    val assetCount: Int,
    val iconEmoji: String,
    val accentColorHex: Long,
    val sampleTickers: List<String>
)

data class CustomWatchlist(
    val id: String,
    val name: String,
    val assetSymbols: MutableList<String> = mutableListOf(),
    val icon: String = "⭐"
)

data class StockImpactTag(
    val symbol: String,
    val changePercent: Double
)

data class NewsArticle(
    val id: String,
    val headline: String,
    val subtitle: String,
    val timeAgo: String,
    val publisher: String,
    val category: String,
    val tags: List<StockImpactTag>,
    val summary: String,
    val readMinutes: Int = 2,
    val sentiment: NewsSentiment = NewsSentiment.BULLISH
)

enum class NewsSentiment {
    BULLISH, BEARISH, NEUTRAL
}

data class PriceAlert(
    val id: String,
    val assetSymbol: String,
    val assetName: String,
    val targetPrice: Double,
    val isAbove: Boolean,
    val isActive: Boolean = true,
    val createdTimestamp: Long = System.currentTimeMillis()
)

enum class TradeType {
    BUY, SELL
}

enum class OrderType(val displayName: String) {
    MARKET("Market"),
    LIMIT("Limit"),
    STOP_LOSS("Stop"),
    STOP_LIMIT("Stop Limit"),
    TRAILING_STOP("Trailing Stop")
}

enum class OrderStatus(val displayName: String) {
    PENDING("Pending"),
    FILLED("Filled"),
    CANCELLED("Cancelled"),
    REJECTED("Rejected")
}

enum class TimeInForce(val displayName: String) {
    GTC("Good 'Til Cancelled"),
    DAY("Day Only"),
    IOC("Immediate Or Cancel")
}

data class TradeOrder(
    val id: String,
    val assetSymbol: String,
    val assetName: String,
    val tradeType: TradeType,
    val orderType: OrderType,
    val status: OrderStatus,
    val units: Double,
    val targetPrice: Double? = null,
    val executedPrice: Double? = null,
    val stopLoss: Double? = null,
    val takeProfit: Double? = null,
    val leverage: Int = 1,
    val timeInForce: TimeInForce = TimeInForce.GTC,
    val brokerName: String = "Golden Compass Execution Core",
    val timestamp: Long = System.currentTimeMillis()
)

data class TradePosition(
    val id: String,
    val assetSymbol: String,
    val assetName: String,
    val type: TradeType,
    val units: Double,
    val entryPrice: Double,
    val currentPrice: Double,
    val pnl: Double,
    val pnlPercent: Double,
    val leverage: Int = 1,
    val stopLoss: Double? = null,
    val takeProfit: Double? = null,
    val openTime: Long = System.currentTimeMillis()
)

data class ClosedTrade(
    val id: String,
    val assetSymbol: String,
    val assetName: String,
    val type: TradeType,
    val units: Double,
    val entryPrice: Double,
    val exitPrice: Double,
    val realizedPnl: Double,
    val returnPercent: Double,
    val leverage: Int,
    val closeTime: Long = System.currentTimeMillis()
)

data class MarketCompassBearing(
    val headingDegrees: Float = 38f,
    val cardinalDirection: String = "NNE 038°",
    val regimeName: String = "Bullish Momentum Tide",
    val macroCurrent: String = "Expansionary Liquidity Flow",
    val magneticFieldStrength: Int = 88, // 0-100%
    val latitude: String = "40.7128° N",
    val longitude: String = "74.0060° W",
    val riskQuadrant: String = "Quadrant I (High Alpha / Controlled Vol)",
    val waypointRecommendation: String = "Accumulate Tech & Crypto Breakouts on 4h Dips"
)

enum class BrokerType(val displayName: String, val badge: String, val latencyMs: Int) {
    GOLDEN_COMPASS_CORE("Golden Compass Direct DMA", "Institutional Routing", 8),
    ALPACA_MARKETS("Alpaca Securities", "SEC/FINRA Broker", 45),
    INTERACTIVE_BROKERS("Interactive Brokers (IBKR)", "Global Gateway", 28),
    BINANCE_GLOBAL("Binance Crypto Engine", "Crypto Liquidity", 35)
}

data class BrokerConfiguration(
    val selectedBroker: BrokerType = BrokerType.GOLDEN_COMPASS_CORE,
    val isLiveAccount: Boolean = false, // false = Paper/Sandbox, true = Real Brokerage
    val apiKey: String = "pk_live_sec_goldencompass_88921a",
    val apiSecret: String = "••••••••••••••••••••••••",
    val isConnected: Boolean = true,
    val serverPingMs: Int = 12
)

enum class TransactionType(val displayName: String) {
    DEPOSIT("Deposit"),
    WITHDRAWAL("Withdrawal"),
    TRADE_REALIZED("Trade P&L"),
    DIVIDEND("Dividend"),
    INTEREST("Overnight Interest")
}

data class FundingTransaction(
    val id: String,
    val type: TransactionType,
    val amount: Double,
    val method: String,
    val status: String = "Completed",
    val timestamp: Long = System.currentTimeMillis(),
    val referenceId: String = "TXN-${System.currentTimeMillis().toString().takeLast(6)}"
)

data class InAppNotification(
    val id: String,
    val title: String,
    val message: String,
    val timeAgo: String,
    val type: NotificationType,
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

enum class NotificationType {
    TRADE_FILL, PRICE_ALERT, MARKET_NEWS, SECURITY, SYSTEM
}

data class PortfolioAnalytics(
    val totalTrades: Int,
    val winningTrades: Int,
    val losingTrades: Int,
    val winRatePercent: Double,
    val totalRealizedPnl: Double,
    val totalReturnPercent: Double,
    val profitFactor: Double,
    val sharpeRatio: Double,
    val maxDrawdownPercent: Double,
    val bestTradePnl: Double,
    val worstTradePnl: Double,
    val avgWinPnl: Double,
    val avgLossPnl: Double
)

data class AiResearchReport(
    val symbol: String,
    val companyName: String,
    val rating: String, // "BUY", "HOLD", "SELL", "STRONG BUY"
    val summary: String,
    val bullCase: List<String>,
    val bearCase: List<String>,
    val technicalVerdict: String,
    val keyCatalysts: List<String>,
    val riskAssessment: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class AiChatMessage(
    val id: String,
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

enum class VerificationStatus {
    NOT_STARTED,
    PHONE_VERIFIED,
    DOCUMENTS_SUBMITTED,
    QUESTIONNAIRE_COMPLETED,
    VERIFIED
}

data class OnboardingFormData(
    val phoneNumber: String = "712 345 678",
    val countryCode: String = "+1",
    val countryName: String = "United States",
    val countryFlag: String = "🇺🇸",
    val acceptedPolicies: Set<String> = emptySet(),
    val idUploaded: Boolean = false,
    val selfieTaken: Boolean = false,
    val employmentStatus: String = "Employed",
    val cfdExperience: String = "2-4 years",
    val qualifications: Set<String> = setOf("I have relevant work experience"),
    val industry: String = "Financial services",
    val grossIncome: String = "$100,000-$199,999",
    val originOfWealth: Set<String> = setOf("Employment", "Savings"),
    val accountCurrency: String = "US Dollar (USD)",
    val currencyCode: String = "USD"
)
