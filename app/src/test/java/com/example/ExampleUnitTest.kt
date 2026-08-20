package com.example

import com.example.viewmodel.TradingViewModel
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testSecurityStateTransitions() {
    val viewModel = TradingViewModel()
    // Initial state is locked by default for security
    assertTrue(viewModel.uiState.value.isAppLocked)
    assertTrue(viewModel.uiState.value.isBiometricEnabled)

    // Unlock
    viewModel.unlockApp()
    assertFalse(viewModel.uiState.value.isAppLocked)

    // Lock again
    viewModel.lockApp()
    assertTrue(viewModel.uiState.value.isAppLocked)

    // Change PIN
    viewModel.setAppPin("9876")
    assertEquals("9876", viewModel.uiState.value.appPin)

    // Toggle biometrics
    viewModel.setBiometricEnabled(false)
    assertFalse(viewModel.uiState.value.isBiometricEnabled)
  }
}
