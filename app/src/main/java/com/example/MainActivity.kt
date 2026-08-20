package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.components.*
import com.example.model.TradeType
import com.example.screens.*
import com.example.security.BiometricAuthManager
import com.example.security.BiometricAuthResult
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.TradingViewModel

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                GoldenCompassApp(activity = this)
            }
        }
    }
}

@Composable
fun GoldenCompassApp(
    activity: FragmentActivity? = null,
    viewModel: TradingViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val currentActivity = activity ?: (context as? FragmentActivity)
    val snackbarHostState = remember { SnackbarHostState() }

    val biometricAuthManager = remember(context) { BiometricAuthManager(context) }
    val biometricAvailability = remember(context) { biometricAuthManager.checkBiometricAvailability() }

    fun triggerBiometrics(
        title: String = "Golden Compass Biometrics",
        subtitle: String = "Authenticate to unlock",
        description: String = "Scan your fingerprint or face to verify your identity",
        onSuccess: () -> Unit
    ) {
        if (currentActivity != null) {
            biometricAuthManager.promptBiometric(
                activity = currentActivity,
                title = title,
                subtitle = subtitle,
                description = description,
                negativeButtonText = "Use Passcode",
                onResult = { result ->
                    when (result) {
                        is BiometricAuthResult.Success -> {
                            onSuccess()
                        }
                        is BiometricAuthResult.Error -> {
                            viewModel.setBiometricAuthError(result.errorMessage)
                        }
                        is BiometricAuthResult.Failed -> {
                            viewModel.setBiometricAuthError("Biometric verification not recognized")
                        }
                        is BiometricAuthResult.UserCanceled -> {
                            // Canceled by user
                        }
                        is BiometricAuthResult.FallbackToPin -> {
                            // Fallback to PIN
                        }
                    }
                }
            )
        }
    }

    LaunchedEffect(uiState.userToastMessage) {
        uiState.userToastMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearToastMessage()
        }
    }

    // Auto-prompt biometrics when app is locked on launch
    LaunchedEffect(uiState.isAppLocked) {
        if (uiState.isAppLocked && uiState.isBiometricEnabled && uiState.requireBiometricOnLaunch) {
            triggerBiometrics(
                title = "Golden Compass Security",
                subtitle = "Portfolio Locked",
                description = "Scan fingerprint to access your live account"
            ) {
                viewModel.unlockApp()
            }
        }
    }

    val isOnboardingFlow = uiState.currentTab == 8

    // IF APP IS LOCKED: SHOW BIOMETRIC LOCK SCREEN
    if (uiState.isAppLocked) {
        BiometricLockScreen(
            biometricAvailability = biometricAvailability,
            correctPin = uiState.appPin,
            authError = uiState.biometricAuthError,
            onTriggerBiometrics = {
                triggerBiometrics(
                    title = "Golden Compass Security",
                    subtitle = "Verify Identity",
                    description = "Scan your fingerprint to unlock"
                ) {
                    viewModel.unlockApp()
                }
            },
            onUnlockSuccess = {
                viewModel.unlockApp()
            }
        )
        return
    }

    // MAIN AUTHENTICATED APP INTERFACE
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BackgroundDark,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (!isOnboardingFlow && !uiState.isSearchOpen) {
                AppTopBar(
                    balance = uiState.balance,
                    onSearchClick = { viewModel.toggleSearch(true) },
                    onBalanceClick = { viewModel.toggleDepositSheet(true) },
                    onProfileClick = {
                        if (uiState.currentTab == 7) viewModel.setTab(0) else viewModel.setTab(7)
                    }
                )
            }
        },
        bottomBar = {
            if (!isOnboardingFlow && !uiState.isSearchOpen) {
                AppBottomNavigation(
                    selectedTab = if (uiState.currentTab in 0..5) uiState.currentTab else -1,
                    onTabSelected = { index -> viewModel.setTab(index) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BackgroundDark)
        ) {
            // Main Screen Content based on Active Tab
            AnimatedContent(
                targetState = uiState.currentTab,
                transitionSpec = { fadeIn().togetherWith(fadeOut()) },
                label = "MainTabAnimation"
            ) { tab ->
                when (tab) {
                    0 -> HomeScreen(
                        assets = uiState.assets,
                        curatedWatchlists = uiState.curatedWatchlists,
                        priceAlerts = uiState.priceAlerts,
                        positions = uiState.positions,
                        verificationStatus = uiState.verificationStatus,
                        onStartVerification = { viewModel.startOnboarding(1) },
                        onAssetClick = { asset -> viewModel.openAssetDetail(asset) },
                        onAddAlertClick = { viewModel.togglePriceAlertSheet(true) },
                        onExplorePortfolioClick = { viewModel.setTab(2) },
                        onCuratedClick = { viewModel.setTab(1) }
                    )
                    1 -> WatchlistsScreen(
                        assets = uiState.assets,
                        curatedWatchlists = uiState.curatedWatchlists,
                        customWatchlists = uiState.customWatchlists,
                        selectedCategory = uiState.selectedCategory,
                        onCategorySelected = { cat -> viewModel.setSelectedCategory(cat) },
                        onCreateWatchlistClick = { viewModel.toggleCustomWatchlistDialog(true) },
                        onDeleteWatchlist = { id -> viewModel.deleteCustomWatchlist(id) },
                        onToggleAssetInWatchlist = { wId, sym -> viewModel.toggleAssetInCustomWatchlist(wId, sym) },
                        onAssetClick = { asset -> viewModel.openAssetDetail(asset) },
                        onBuyClick = { asset -> viewModel.openTradeSheet(asset, TradeType.BUY) },
                        onSellClick = { asset -> viewModel.openTradeSheet(asset, TradeType.SELL) }
                    )
                    2 -> PortfolioScreen(
                        balance = uiState.balance,
                        investedAmount = uiState.investedAmount,
                        marginUsed = uiState.marginUsed,
                        positions = uiState.positions,
                        closedTrades = uiState.closedTrades,
                        onDepositClick = { viewModel.toggleDepositSheet(true) },
                        onWithdrawClick = { viewModel.toggleWithdrawSheet(true) },
                        onExploreMarketsClick = { viewModel.setTab(1) },
                        onClosePosition = { posId -> viewModel.closePosition(posId) }
                    )
                    3 -> OrdersScreen(
                        orders = uiState.orders,
                        onCancelOrder = { orderId -> viewModel.cancelOrder(orderId) },
                        onExploreMarkets = { viewModel.setTab(1) }
                    )
                    4 -> AIResearchScreen(
                        assets = uiState.assets,
                        selectedAsset = uiState.selectedAssetForDetail ?: uiState.assets.firstOrNull(),
                        aiReport = uiState.currentResearchReport,
                        isLoadingReport = uiState.isAiAnalyzing,
                        chatMessages = uiState.aiChatMessages,
                        isLoadingChat = uiState.isAiChatLoading,
                        onSelectAsset = { asset -> viewModel.openAssetDetail(asset) },
                        onSendMessage = { query -> viewModel.sendAiQuery(query) },
                        onBuy = { asset -> viewModel.openTradeSheet(asset, TradeType.BUY) },
                        onSell = { asset -> viewModel.openTradeSheet(asset, TradeType.SELL) }
                    )
                    5 -> AnalyticsScreen(
                        analytics = uiState.analytics,
                        positions = uiState.positions,
                        closedTrades = uiState.closedTrades,
                        totalEquity = uiState.balance + uiState.investedAmount + uiState.positions.sumOf { it.pnl },
                        marginUsed = uiState.marginUsed
                    )
                    6 -> NewsScreen(
                        newsArticles = uiState.newsArticles,
                        selectedArticle = uiState.selectedNewsArticle,
                        onArticleClick = { article -> viewModel.openNewsDetail(article) },
                        onCloseArticleDetail = { viewModel.closeNewsDetail() }
                    )
                    7 -> ProfileScreen(
                        verificationStatus = uiState.verificationStatus,
                        unreadInboxCount = uiState.unreadNotifications,
                        isBiometricEnabled = uiState.isBiometricEnabled,
                        brokerName = uiState.brokerConfig.selectedBroker.displayName,
                        isLiveAccount = uiState.brokerConfig.isLiveAccount,
                        onInboxClick = { viewModel.toggleNotificationSheet(true) },
                        onGetHelpClick = { viewModel.toggleHelpSheet(true) },
                        onStartOnboarding = { viewModel.startOnboarding(1) },
                        onDepositClick = { viewModel.toggleDepositSheet(true) },
                        onWithdrawClick = { viewModel.toggleWithdrawSheet(true) },
                        onBrokerSettingsClick = { viewModel.toggleBrokerSettings(true) },
                        onSecurityClick = { viewModel.toggleSecuritySheet(true) },
                        onLockAppClick = { viewModel.lockApp() }
                    )
                    8 -> OnboardingScreen(
                        currentStep = uiState.onboardingStep,
                        questionnaireSubStep = uiState.questionnaireSubStep,
                        formData = uiState.onboardingData,
                        onStepChange = { step -> viewModel.setOnboardingStep(step) },
                        onQuestionnaireSubStepChange = { subStep -> viewModel.setQuestionnaireSubStep(subStep) },
                        onKeypadDigit = { d -> viewModel.onKeypadDigit(d) },
                        onKeypadDelete = { viewModel.onKeypadDelete() },
                        onSetCountry = { code, name, flag -> viewModel.setCountry(code, name, flag) },
                        onTogglePolicy = { p -> viewModel.togglePolicy(p) },
                        onAcceptAllPolicies = { list -> viewModel.acceptAllPolicies(list) },
                        onSetDocumentUploaded = { isId, v -> viewModel.setDocumentUploaded(isId, v) },
                        onSetEmployment = { emp -> viewModel.setEmploymentStatus(emp) },
                        onSetCfdExp = { exp -> viewModel.setCfdExperience(exp) },
                        onToggleQualification = { q -> viewModel.toggleQualification(q) },
                        onSetIndustry = { ind -> viewModel.setIndustry(ind) },
                        onSetIncome = { inc -> viewModel.setGrossIncome(inc) },
                        onToggleWealth = { w -> viewModel.toggleWealthOrigin(w) },
                        onSetCurrency = { name, code -> viewModel.setCurrency(name, code) },
                        onComplete = { viewModel.completeOnboarding() },
                        onBackToHome = { viewModel.setTab(0) }
                    )
                }
            }

            // Floating Action Button (FAB)
            if (!isOnboardingFlow && !uiState.isSearchOpen) {
                AppGridFab(
                    onClick = { viewModel.toggleFabMenu() },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 16.dp, end = 16.dp)
                )
            }

            // Full-screen Search Overlay
            if (uiState.isSearchOpen) {
                SearchOverlay(
                    query = uiState.searchQuery,
                    onQueryChange = { q -> viewModel.setSearchQuery(q) },
                    assets = uiState.assets,
                    onClose = { viewModel.toggleSearch(false) },
                    onAssetSelected = { asset ->
                        viewModel.toggleSearch(false)
                        viewModel.openAssetDetail(asset)
                    },
                    onBuy = { asset ->
                        viewModel.toggleSearch(false)
                        viewModel.openTradeSheet(asset, TradeType.BUY)
                    },
                    onSell = { asset ->
                        viewModel.toggleSearch(false)
                        viewModel.openTradeSheet(asset, TradeType.SELL)
                    }
                )
            }
        }
    }

    // Modal Bottom Sheets & Dialogs
    if (uiState.isFabMenuOpen) {
        QuickActionMenuSheet(
            onDismiss = { viewModel.toggleFabMenu(false) },
            onDepositClick = { viewModel.toggleDepositSheet(true) },
            onAddAlertClick = { viewModel.togglePriceAlertSheet(true) },
            onSearchClick = {
                viewModel.toggleFabMenu(false)
                viewModel.toggleSearch(true)
            },
            onVerifyClick = { viewModel.startOnboarding(1) },
            onHelpClick = { viewModel.toggleHelpSheet(true) }
        )
    }

    if (uiState.isTradeSheetOpen && uiState.selectedAssetForTrade != null) {
        TradeOrderBottomSheet(
            asset = uiState.selectedAssetForTrade!!,
            initialTradeType = uiState.selectedTradeType,
            onDismiss = { viewModel.closeTradeSheet() },
            onConfirmOrder = { _, _ ->
                if (uiState.requireBiometricForTrade && uiState.isBiometricEnabled) {
                    triggerBiometrics(
                        title = "Confirm Order Execution",
                        subtitle = "${uiState.selectedTradeType} ${uiState.tradeLotSize} ${uiState.selectedAssetForTrade?.symbol}",
                        description = "Verify biometrics to authorize market order"
                    ) {
                        viewModel.executeTrade()
                    }
                } else {
                    viewModel.executeTrade()
                }
            }
        )
    }

    if (uiState.selectedAssetForDetail != null) {
        AssetDetailSheet(
            asset = uiState.selectedAssetForDetail!!,
            onDismiss = { viewModel.closeAssetDetail() },
            onBuy = {
                val asset = uiState.selectedAssetForDetail!!
                viewModel.closeAssetDetail()
                viewModel.openTradeSheet(asset, TradeType.BUY)
            },
            onSell = {
                val asset = uiState.selectedAssetForDetail!!
                viewModel.closeAssetDetail()
                viewModel.openTradeSheet(asset, TradeType.SELL)
            }
        )
    }

    if (uiState.isPriceAlertSheetOpen) {
        PriceAlertSheet(
            assets = uiState.assets,
            existingAlerts = uiState.priceAlerts,
            onDismiss = { viewModel.togglePriceAlertSheet(false) },
            onAddAlert = { sym, price, above -> viewModel.addPriceAlert(sym, price, above) },
            onDeleteAlert = { id -> viewModel.deletePriceAlert(id) }
        )
    }

    if (uiState.isDepositSheetOpen) {
        FullDepositBottomSheet(
            currentBalance = uiState.balance,
            onDismiss = { viewModel.toggleDepositSheet(false) },
            onDepositAmount = { amt, method -> viewModel.depositFunds(amt, method) }
        )
    }

    if (uiState.isWithdrawSheetOpen) {
        FullWithdrawBottomSheet(
            currentBalance = uiState.balance,
            onDismiss = { viewModel.toggleWithdrawSheet(false) },
            onWithdrawAmount = { amt, dest -> viewModel.withdrawFunds(amt, dest) }
        )
    }

    if (uiState.isNotificationSheetOpen) {
        NotificationCenterSheet(
            notifications = uiState.notifications,
            onDismiss = { viewModel.toggleNotificationSheet(false) },
            onMarkAllRead = { viewModel.markNotificationsRead() }
        )
    }

    if (uiState.isCustomWatchlistDialogOpen) {
        CreateCustomWatchlistDialog(
            onDismiss = { viewModel.toggleCustomWatchlistDialog(false) },
            onCreateWatchlist = { name, icon ->
                viewModel.createCustomWatchlist(name, icon, emptyList())
            }
        )
    }

    if (uiState.isBrokerSettingsOpen) {
        BrokerSettingsDialog(
            currentConfig = uiState.brokerConfig,
            onDismiss = { viewModel.toggleBrokerSettings(false) },
            onSaveConfig = { newConfig -> viewModel.updateBrokerConfig(newConfig) }
        )
    }

    if (uiState.isHelpSheetOpen) {
        HelpSupportBottomSheet(
            onDismiss = { viewModel.toggleHelpSheet(false) }
        )
    }

    if (uiState.isSecuritySheetOpen) {
        SecuritySettingsBottomSheet(
            isBiometricEnabled = uiState.isBiometricEnabled,
            requireOnLaunch = uiState.requireBiometricOnLaunch,
            requireForTrade = uiState.requireBiometricForTrade,
            currentPin = uiState.appPin,
            biometricAvailability = biometricAvailability,
            onToggleBiometricEnabled = { v -> viewModel.setBiometricEnabled(v) },
            onToggleRequireOnLaunch = { v -> viewModel.setRequireBiometricOnLaunch(v) },
            onToggleRequireForTrade = { v -> viewModel.setRequireBiometricForTrade(v) },
            onChangePin = { p -> viewModel.setAppPin(p) },
            onTestBiometricPrompt = {
                triggerBiometrics(
                    title = "Biometric Test",
                    subtitle = "Sensor Verification",
                    description = "Testing hardware biometric authentication prompt"
                ) {
                    Toast.makeText(context, "Biometric authentication successful!", Toast.LENGTH_SHORT).show()
                }
            },
            onLockAppNow = {
                viewModel.toggleSecuritySheet(false)
                viewModel.lockApp()
            },
            onDismiss = { viewModel.toggleSecuritySheet(false) }
        )
    }
}
