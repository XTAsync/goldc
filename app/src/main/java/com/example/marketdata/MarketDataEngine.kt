package com.example.marketdata

import com.example.model.Asset
import com.example.model.AssetCategory
import com.example.model.OrderBookLevel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class MarketDataEngine(private val scope: CoroutineScope) {

    private val _assets = MutableStateFlow<List<Asset>>(emptyList())
    val assets: StateFlow<List<Asset>> = _assets.asStateFlow()

    private val _isStreaming = MutableStateFlow(true)
    val isStreaming: StateFlow<Boolean> = _isStreaming.asStateFlow()

    private val _tickCount = MutableStateFlow(0L)
    val tickCount: StateFlow<Long> = _tickCount.asStateFlow()

    init {
        _assets.value = getInitialAssets()
        startLiveMarketStream()
    }

    private fun startLiveMarketStream() {
        scope.launch(Dispatchers.Default) {
            while (isActive) {
                delay(1200) // update price ticks every 1.2s
                if (_isStreaming.value) {
                    updateRandomAssetTicks()
                    _tickCount.update { it + 1 }
                }
            }
        }
    }

    private fun updateRandomAssetTicks() {
        _assets.update { currentList ->
            currentList.map { asset ->
                // Randomly perturb asset price by +/- 0.05% to 0.4%
                val shouldUpdate = Random.nextFloat() > 0.35f
                if (!shouldUpdate) return@map asset

                val volatilityFactor = if (asset.category == AssetCategory.CRYPTO) 0.0035 else 0.0012
                val deltaPercent = (Random.nextDouble() - 0.49) * volatilityFactor * 100.0
                val deltaAmount = asset.price * (deltaPercent / 100.0)
                val newPrice = max(0.01, asset.price + deltaAmount)
                val newChangeAmount = asset.changeAmount + deltaAmount
                val newChangePercent = (newChangeAmount / (newPrice - newChangeAmount)) * 100.0

                val newHigh = max(asset.high24h, newPrice)
                val newLow = min(asset.low24h, newPrice)

                // Update sparkline point
                val updatedSparkline = if (asset.sparklinePoints.isNotEmpty()) {
                    val points = asset.sparklinePoints.toMutableList()
                    val lastPoint = points.lastOrNull() ?: 50f
                    val newPoint = (lastPoint + (deltaPercent * 8).toFloat()).coerceIn(10f, 90f)
                    points.removeAt(0)
                    points.add(newPoint)
                    points
                } else {
                    asset.sparklinePoints
                }

                asset.copy(
                    price = roundToDecimals(newPrice, if (newPrice < 2.0) 4 else 2),
                    changeAmount = roundToDecimals(newChangeAmount, 2),
                    changePercent = roundToDecimals(newChangePercent, 2),
                    high24h = roundToDecimals(newHigh, 2),
                    low24h = roundToDecimals(newLow, 2),
                    sparklinePoints = updatedSparkline,
                    bidPrice = roundToDecimals(newPrice * 0.9998, if (newPrice < 2.0) 4 else 2),
                    askPrice = roundToDecimals(newPrice * 1.0002, if (newPrice < 2.0) 4 else 2),
                    bidVolume = (1000..5000).random(),
                    askVolume = (1000..5000).random()
                )
            }
        }
    }

    fun toggleStreaming(enabled: Boolean) {
        _isStreaming.value = enabled
    }

    fun generateOrderBook(asset: Asset): List<OrderBookLevel> {
        val levels = mutableListOf<OrderBookLevel>()
        val basePrice = asset.price

        // 4 Ask levels (Sell orders above current price)
        var cumulativeAsk = 0.0
        for (i in 4 downTo 1) {
            val price = basePrice * (1.0 + (i * 0.0008))
            val size = (10..150).random().toDouble()
            cumulativeAsk += size
            levels.add(
                OrderBookLevel(
                    price = roundToDecimals(price, if (basePrice < 2.0) 4 else 2),
                    size = size,
                    total = cumulativeAsk,
                    isBid = false
                )
            )
        }

        // 4 Bid levels (Buy orders below current price)
        var cumulativeBid = 0.0
        for (i in 1..4) {
            val price = basePrice * (1.0 - (i * 0.0008))
            val size = (15..180).random().toDouble()
            cumulativeBid += size
            levels.add(
                OrderBookLevel(
                    price = roundToDecimals(price, if (basePrice < 2.0) 4 else 2),
                    size = size,
                    total = cumulativeBid,
                    isBid = true
                )
            )
        }

        return levels
    }

    fun generateTimeframePoints(asset: Asset, timeframe: String): List<Float> {
        val baseSeed = asset.symbol.hashCode() + timeframe.hashCode()
        val random = Random(baseSeed)
        val pointCount = when (timeframe) {
            "1D" -> 30
            "1W" -> 35
            "1M" -> 40
            "1Y" -> 45
            "ALL" -> 50
            else -> 30
        }

        val points = mutableListOf<Float>()
        var cur = 45f + random.nextInt(15)
        val trend = if (asset.changePercent >= 0) 0.6f else -0.4f

        for (i in 0 until pointCount) {
            val change = (random.nextFloat() - 0.48f + (trend * 0.15f)) * 8f
            cur = (cur + change).coerceIn(10f, 90f)
            points.add(cur)
        }
        return points
    }

    private fun roundToDecimals(value: Double, decimals: Int): Double {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return Math.round(value * multiplier) / multiplier
    }

    private fun getInitialAssets(): List<Asset> = listOf(
        Asset(
            id = "a1",
            symbol = "NVDA",
            name = "NVIDIA Corporation",
            category = AssetCategory.SHARES,
            price = 128.85,
            changePercent = 4.32,
            changeAmount = 5.34,
            sparklinePoints = listOf(30f, 32f, 35f, 34f, 40f, 48f, 52f, 60f, 68f, 75f, 82f),
            high24h = 130.40,
            low24h = 124.10,
            volume = "62.4M",
            marketCap = "$3.16T",
            isMostTraded = true,
            iconEmoji = "🟢",
            description = "NVIDIA Corporation designs graphics processing units for gaming and professional markets, as well as system on a chip units for mobile computing and automotive.",
            peRatio = 64.2,
            forwardPe = 38.5,
            eps = 2.01,
            dividendYield = 0.03,
            beta = 1.68,
            week52High = 140.76,
            week52Low = 45.60,
            analystRating = "Strong Buy",
            analystTargetPrice = 150.00,
            analystBuyCount = 41,
            analystHoldCount = 3,
            analystSellCount = 0,
            sector = "Semiconductors & AI"
        ),
        Asset(
            id = "a2",
            symbol = "AAPL",
            name = "Apple Inc.",
            category = AssetCategory.SHARES,
            price = 224.23,
            changePercent = 1.45,
            changeAmount = 3.20,
            sparklinePoints = listOf(40f, 42f, 41f, 45f, 50f, 55f, 53f, 58f, 62f, 65f, 70f),
            high24h = 226.50,
            low24h = 221.80,
            volume = "48.1M",
            marketCap = "$3.44T",
            isMostTraded = true,
            iconEmoji = "🍎",
            description = "Apple Inc. designs, manufactures, and markets smartphones, personal computers, tablets, wearables, and accessories, and sells a variety of related services.",
            peRatio = 34.5,
            forwardPe = 29.8,
            eps = 6.50,
            dividendYield = 0.45,
            beta = 1.04,
            week52High = 237.23,
            week52Low = 164.08,
            analystRating = "Buy",
            analystTargetPrice = 245.00,
            analystBuyCount = 32,
            analystHoldCount = 10,
            analystSellCount = 1,
            sector = "Consumer Electronics"
        ),
        Asset(
            id = "a3",
            symbol = "TSLA",
            name = "Tesla, Inc.",
            category = AssetCategory.SHARES,
            price = 214.11,
            changePercent = -2.18,
            changeAmount = -4.77,
            sparklinePoints = listOf(70f, 68f, 62f, 65f, 58f, 52f, 48f, 50f, 44f, 42f, 38f),
            high24h = 220.80,
            low24h = 211.50,
            volume = "75.8M",
            marketCap = "$682.4B",
            isMostVolatile = true,
            isMostTraded = true,
            iconEmoji = "⚡",
            description = "Tesla, Inc. designs, develops, manufactures, sells, and leases electric vehicles, energy storage systems, and solar panels.",
            peRatio = 61.8,
            forwardPe = 45.2,
            eps = 3.46,
            dividendYield = 0.0,
            beta = 2.42,
            week52High = 271.00,
            week52Low = 138.80,
            analystRating = "Hold",
            analystTargetPrice = 210.00,
            analystBuyCount = 18,
            analystHoldCount = 16,
            analystSellCount = 9,
            sector = "Automotive & Clean Energy"
        ),
        Asset(
            id = "a4",
            symbol = "BTC/USD",
            name = "Bitcoin",
            category = AssetCategory.CRYPTO,
            price = 64280.00,
            changePercent = 3.84,
            changeAmount = 2378.00,
            sparklinePoints = listOf(35f, 38f, 42f, 40f, 48f, 55f, 62f, 58f, 68f, 74f, 80f),
            high24h = 65120.00,
            low24h = 61850.00,
            volume = "$34.2B",
            marketCap = "$1.27T",
            isMostTraded = true,
            isMostVolatile = true,
            iconEmoji = "🪙",
            description = "Bitcoin is a decentralized digital cryptocurrency created in 2009 by an unknown person or group using the pseudonym Satoshi Nakamoto.",
            peRatio = 0.0,
            forwardPe = 0.0,
            eps = 0.0,
            dividendYield = 0.0,
            beta = 2.85,
            week52High = 73750.00,
            week52Low = 26400.00,
            analystRating = "Strong Buy",
            analystTargetPrice = 85000.00,
            analystBuyCount = 48,
            analystHoldCount = 4,
            analystSellCount = 1,
            sector = "Digital Store of Value"
        ),
        Asset(
            id = "a5",
            symbol = "ETH/USD",
            name = "Ethereum",
            category = AssetCategory.CRYPTO,
            price = 3490.50,
            changePercent = 2.65,
            changeAmount = 90.10,
            sparklinePoints = listOf(40f, 42f, 45f, 44f, 50f, 56f, 60f, 58f, 65f, 70f, 74f),
            high24h = 3540.00,
            low24h = 3380.00,
            volume = "$18.6B",
            marketCap = "$419.8B",
            isMostTraded = true,
            iconEmoji = "🔷",
            description = "Ethereum is a decentralized, open-source blockchain with smart contract functionality, hosting Ether as its native token.",
            peRatio = 0.0,
            forwardPe = 0.0,
            eps = 0.0,
            dividendYield = 3.2,
            beta = 2.60,
            week52High = 4093.00,
            week52Low = 1520.00,
            analystRating = "Buy",
            analystTargetPrice = 4400.00,
            analystBuyCount = 38,
            analystHoldCount = 6,
            analystSellCount = 2,
            sector = "Smart Contract Platforms"
        ),
        Asset(
            id = "a6",
            symbol = "GOLD",
            name = "Spot Gold / USD",
            category = AssetCategory.COMMODITIES,
            price = 2418.60,
            changePercent = 0.78,
            changeAmount = 18.70,
            sparklinePoints = listOf(50f, 52f, 51f, 53f, 55f, 58f, 60f, 59f, 62f, 64f, 68f),
            high24h = 2426.00,
            low24h = 2398.20,
            volume = "$42.1B",
            marketCap = "$16.8T",
            isMostTraded = true,
            iconEmoji = "🥇",
            description = "Gold is a physical commodity and global currency reserve asset valued for hedging inflation and macroeconomic uncertainty.",
            peRatio = 0.0,
            forwardPe = 0.0,
            eps = 0.0,
            dividendYield = 0.0,
            beta = 0.12,
            week52High = 2483.70,
            week52Low = 1810.50,
            analystRating = "Buy",
            analystTargetPrice = 2600.00,
            analystBuyCount = 25,
            analystHoldCount = 8,
            analystSellCount = 1,
            sector = "Precious Metals"
        ),
        Asset(
            id = "a7",
            symbol = "EUR/USD",
            name = "Euro / US Dollar",
            category = AssetCategory.CURRENCIES,
            price = 1.0892,
            changePercent = -0.15,
            changeAmount = -0.0016,
            sparklinePoints = listOf(55f, 54f, 56f, 53f, 51f, 49f, 50f, 48f, 46f, 45f, 44f),
            high24h = 1.0925,
            low24h = 1.0870,
            volume = "$520B",
            marketCap = "N/A",
            iconEmoji = "💶",
            description = "The EUR/USD currency pair represents the quotation of the European single currency Euro against the US Dollar.",
            peRatio = 0.0,
            forwardPe = 0.0,
            eps = 0.0,
            dividendYield = 0.0,
            beta = 0.35,
            week52High = 1.1140,
            week52Low = 1.0450,
            analystRating = "Hold",
            analystTargetPrice = 1.1000,
            analystBuyCount = 14,
            analystHoldCount = 22,
            analystSellCount = 6,
            sector = "Foreign Exchange"
        ),
        Asset(
            id = "a8",
            symbol = "US500",
            name = "S&P 500 Index",
            category = AssetCategory.INDICES,
            price = 5567.20,
            changePercent = 0.92,
            changeAmount = 50.80,
            sparklinePoints = listOf(42f, 44f, 46f, 48f, 50f, 54f, 56f, 60f, 62f, 66f, 70f),
            high24h = 5580.40,
            low24h = 5512.10,
            volume = "$140B",
            marketCap = "$45.2T",
            isMostTraded = true,
            iconEmoji = "🏛️",
            description = "The Standard and Poor's 500 is a stock market index tracking the stock performance of 500 of the largest companies listed on stock exchanges in the United States.",
            peRatio = 26.8,
            forwardPe = 21.4,
            eps = 245.0,
            dividendYield = 1.35,
            beta = 1.00,
            week52High = 5669.67,
            week52Low = 4103.78,
            analystRating = "Buy",
            analystTargetPrice = 5800.00,
            analystBuyCount = 35,
            analystHoldCount = 12,
            analystSellCount = 2,
            sector = "Benchmark Index"
        ),
        Asset(
            id = "a9",
            symbol = "MSFT",
            name = "Microsoft Corporation",
            category = AssetCategory.SHARES,
            price = 448.90,
            changePercent = 1.82,
            changeAmount = 8.02,
            sparklinePoints = listOf(45f, 46f, 48f, 50f, 52f, 55f, 58f, 62f, 64f, 68f, 72f),
            high24h = 452.10,
            low24h = 442.30,
            volume = "22.4M",
            marketCap = "$3.34T",
            isMostTraded = true,
            iconEmoji = "💻",
            description = "Microsoft develops, licenses, and supports software, services, devices, and cloud solutions worldwide via Azure, Windows, and Office 365.",
            peRatio = 37.1,
            forwardPe = 31.2,
            eps = 11.80,
            dividendYield = 0.68,
            beta = 0.92,
            week52High = 468.35,
            week52Low = 309.45,
            analystRating = "Strong Buy",
            analystTargetPrice = 490.00,
            analystBuyCount = 39,
            analystHoldCount = 4,
            analystSellCount = 0,
            sector = "Cloud Computing & AI"
        ),
        Asset(
            id = "a10",
            symbol = "CRUDE_OIL",
            name = "WTI Crude Oil",
            category = AssetCategory.COMMODITIES,
            price = 78.45,
            changePercent = -1.34,
            changeAmount = -1.06,
            sparklinePoints = listOf(62f, 60f, 58f, 55f, 52f, 48f, 50f, 46f, 44f, 42f, 40f),
            high24h = 79.80,
            low24h = 77.90,
            volume = "$28.4B",
            marketCap = "N/A",
            isMostVolatile = true,
            iconEmoji = "🛢️",
            description = "West Texas Intermediate crude oil is a specific grade of crude oil and one of the main global oil benchmarks.",
            peRatio = 0.0,
            forwardPe = 0.0,
            eps = 0.0,
            dividendYield = 0.0,
            beta = 0.85,
            week52High = 95.03,
            week52Low = 67.71,
            analystRating = "Hold",
            analystTargetPrice = 82.00,
            analystBuyCount = 15,
            analystHoldCount = 18,
            analystSellCount = 5,
            sector = "Energy Commodities"
        )
    )
}
