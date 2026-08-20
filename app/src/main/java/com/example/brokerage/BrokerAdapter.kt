package com.example.brokerage

import com.example.model.BrokerConfiguration
import com.example.model.BrokerType
import com.example.model.OrderStatus
import com.example.model.OrderType
import com.example.model.TradeOrder
import com.example.model.TradeType
import kotlinx.coroutines.delay
import java.util.UUID

sealed class ExecutionResult {
    data class Filled(
        val order: TradeOrder,
        val fillPrice: Double,
        val fillTime: Long = System.currentTimeMillis()
    ) : ExecutionResult()

    data class Pending(
        val order: TradeOrder
    ) : ExecutionResult()

    data class Rejected(
        val reason: String
    ) : ExecutionResult()
}

interface BrokerAdapter {
    val brokerType: BrokerType
    suspend fun ping(): Int
    suspend fun routeOrder(
        assetSymbol: String,
        assetName: String,
        tradeType: TradeType,
        orderType: OrderType,
        units: Double,
        currentMarketPrice: Double,
        targetPrice: Double?,
        stopLoss: Double?,
        takeProfit: Double?,
        leverage: Int
    ): ExecutionResult
}

class GoldenCompassDirectAdapter(private val config: BrokerConfiguration) : BrokerAdapter {
    override val brokerType = BrokerType.GOLDEN_COMPASS_CORE

    override suspend fun ping(): Int {
        delay(12)
        return 12
    }

    override suspend fun routeOrder(
        assetSymbol: String,
        assetName: String,
        tradeType: TradeType,
        orderType: OrderType,
        units: Double,
        currentMarketPrice: Double,
        targetPrice: Double?,
        stopLoss: Double?,
        takeProfit: Double?,
        leverage: Int
    ): ExecutionResult {
        delay(120) // Low latency institutional DMA route
        val orderId = "ORD-" + UUID.randomUUID().toString().take(8).uppercase()

        return if (orderType == OrderType.MARKET) {
            // Execution price with realistic tiny micro-spread
            val fillPrice = if (tradeType == TradeType.BUY) currentMarketPrice * 1.0001 else currentMarketPrice * 0.9999
            val filledOrder = TradeOrder(
                id = orderId,
                assetSymbol = assetSymbol,
                assetName = assetName,
                tradeType = tradeType,
                orderType = orderType,
                status = OrderStatus.FILLED,
                units = units,
                targetPrice = targetPrice,
                executedPrice = Math.round(fillPrice * 100.0) / 100.0,
                stopLoss = stopLoss,
                takeProfit = takeProfit,
                leverage = leverage,
                brokerName = brokerType.displayName
            )
            ExecutionResult.Filled(filledOrder, filledOrder.executedPrice ?: currentMarketPrice)
        } else {
            // Limit order queued for target price trigger
            val pendingOrder = TradeOrder(
                id = orderId,
                assetSymbol = assetSymbol,
                assetName = assetName,
                tradeType = tradeType,
                orderType = orderType,
                status = OrderStatus.PENDING,
                units = units,
                targetPrice = targetPrice ?: currentMarketPrice,
                stopLoss = stopLoss,
                takeProfit = takeProfit,
                leverage = leverage,
                brokerName = brokerType.displayName
            )
            ExecutionResult.Pending(pendingOrder)
        }
    }
}

class AlpacaBrokerAdapter(private val config: BrokerConfiguration) : BrokerAdapter {
    override val brokerType = BrokerType.ALPACA_MARKETS

    override suspend fun ping(): Int {
        delay(45)
        return 45
    }

    override suspend fun routeOrder(
        assetSymbol: String,
        assetName: String,
        tradeType: TradeType,
        orderType: OrderType,
        units: Double,
        currentMarketPrice: Double,
        targetPrice: Double?,
        stopLoss: Double?,
        takeProfit: Double?,
        leverage: Int
    ): ExecutionResult {
        delay(220)
        val orderId = "ALP-" + UUID.randomUUID().toString().take(8).uppercase()
        return if (orderType == OrderType.MARKET) {
            val fillPrice = currentMarketPrice
            val filledOrder = TradeOrder(
                id = orderId,
                assetSymbol = assetSymbol,
                assetName = assetName,
                tradeType = tradeType,
                orderType = orderType,
                status = OrderStatus.FILLED,
                units = units,
                executedPrice = fillPrice,
                stopLoss = stopLoss,
                takeProfit = takeProfit,
                leverage = leverage,
                brokerName = brokerType.displayName
            )
            ExecutionResult.Filled(filledOrder, fillPrice)
        } else {
            val pendingOrder = TradeOrder(
                id = orderId,
                assetSymbol = assetSymbol,
                assetName = assetName,
                tradeType = tradeType,
                orderType = orderType,
                status = OrderStatus.PENDING,
                units = units,
                targetPrice = targetPrice ?: currentMarketPrice,
                stopLoss = stopLoss,
                takeProfit = takeProfit,
                leverage = leverage,
                brokerName = brokerType.displayName
            )
            ExecutionResult.Pending(pendingOrder)
        }
    }
}

class InteractiveBrokersAdapter(private val config: BrokerConfiguration) : BrokerAdapter {
    override val brokerType = BrokerType.INTERACTIVE_BROKERS

    override suspend fun ping(): Int {
        delay(28)
        return 28
    }

    override suspend fun routeOrder(
        assetSymbol: String,
        assetName: String,
        tradeType: TradeType,
        orderType: OrderType,
        units: Double,
        currentMarketPrice: Double,
        targetPrice: Double?,
        stopLoss: Double?,
        takeProfit: Double?,
        leverage: Int
    ): ExecutionResult {
        delay(180)
        val orderId = "IBKR-" + UUID.randomUUID().toString().take(8).uppercase()
        return if (orderType == OrderType.MARKET) {
            val fillPrice = currentMarketPrice
            val filledOrder = TradeOrder(
                id = orderId,
                assetSymbol = assetSymbol,
                assetName = assetName,
                tradeType = tradeType,
                orderType = orderType,
                status = OrderStatus.FILLED,
                units = units,
                executedPrice = fillPrice,
                stopLoss = stopLoss,
                takeProfit = takeProfit,
                leverage = leverage,
                brokerName = brokerType.displayName
            )
            ExecutionResult.Filled(filledOrder, fillPrice)
        } else {
            val pendingOrder = TradeOrder(
                id = orderId,
                assetSymbol = assetSymbol,
                assetName = assetName,
                tradeType = tradeType,
                orderType = orderType,
                status = OrderStatus.PENDING,
                units = units,
                targetPrice = targetPrice ?: currentMarketPrice,
                stopLoss = stopLoss,
                takeProfit = takeProfit,
                leverage = leverage,
                brokerName = brokerType.displayName
            )
            ExecutionResult.Pending(pendingOrder)
        }
    }
}

class BinanceAdapter(private val config: BrokerConfiguration) : BrokerAdapter {
    override val brokerType = BrokerType.BINANCE_GLOBAL

    override suspend fun ping(): Int {
        delay(35)
        return 35
    }

    override suspend fun routeOrder(
        assetSymbol: String,
        assetName: String,
        tradeType: TradeType,
        orderType: OrderType,
        units: Double,
        currentMarketPrice: Double,
        targetPrice: Double?,
        stopLoss: Double?,
        takeProfit: Double?,
        leverage: Int
    ): ExecutionResult {
        delay(160)
        val orderId = "BN-" + UUID.randomUUID().toString().take(8).uppercase()
        return if (orderType == OrderType.MARKET) {
            val fillPrice = currentMarketPrice
            val filledOrder = TradeOrder(
                id = orderId,
                assetSymbol = assetSymbol,
                assetName = assetName,
                tradeType = tradeType,
                orderType = orderType,
                status = OrderStatus.FILLED,
                units = units,
                executedPrice = fillPrice,
                stopLoss = stopLoss,
                takeProfit = takeProfit,
                leverage = leverage,
                brokerName = brokerType.displayName
            )
            ExecutionResult.Filled(filledOrder, fillPrice)
        } else {
            val pendingOrder = TradeOrder(
                id = orderId,
                assetSymbol = assetSymbol,
                assetName = assetName,
                tradeType = tradeType,
                orderType = orderType,
                status = OrderStatus.PENDING,
                units = units,
                targetPrice = targetPrice ?: currentMarketPrice,
                stopLoss = stopLoss,
                takeProfit = takeProfit,
                leverage = leverage,
                brokerName = brokerType.displayName
            )
            ExecutionResult.Pending(pendingOrder)
        }
    }
}

class BrokerManager {
    fun getAdapter(config: BrokerConfiguration): BrokerAdapter {
        return when (config.selectedBroker) {
            BrokerType.GOLDEN_COMPASS_CORE -> GoldenCompassDirectAdapter(config)
            BrokerType.ALPACA_MARKETS -> AlpacaBrokerAdapter(config)
            BrokerType.INTERACTIVE_BROKERS -> InteractiveBrokersAdapter(config)
            BrokerType.BINANCE_GLOBAL -> BinanceAdapter(config)
        }
    }
}
