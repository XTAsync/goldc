package com.example.security

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

sealed class BiometricAvailability {
    object Available : BiometricAvailability()
    data class Unavailable(val reason: String) : BiometricAvailability()
    object NoneEnrolled : BiometricAvailability()
    object NoHardware : BiometricAvailability()
}

sealed class BiometricAuthResult {
    object Success : BiometricAuthResult()
    data class Error(val errorCode: Int, val errorMessage: String) : BiometricAuthResult()
    object Failed : BiometricAuthResult()
    object UserCanceled : BiometricAuthResult()
    object FallbackToPin : BiometricAuthResult()
}

class BiometricAuthManager(private val context: Context) {

    fun checkBiometricAvailability(): BiometricAvailability {
        val biometricManager = BiometricManager.from(context)
        val authenticators = Authenticators.BIOMETRIC_STRONG or Authenticators.BIOMETRIC_WEAK or Authenticators.DEVICE_CREDENTIAL
        
        return when (biometricManager.canAuthenticate(authenticators)) {
            BiometricManager.BIOMETRIC_SUCCESS -> BiometricAvailability.Available
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> BiometricAvailability.NoHardware
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> BiometricAvailability.Unavailable("Biometric hardware is currently unavailable")
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricAvailability.NoneEnrolled
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> BiometricAvailability.Unavailable("Security update required")
            else -> BiometricAvailability.Unavailable("Biometrics not supported on this device")
        }
    }

    fun canAuthenticateStrongOrWeak(): Boolean {
        val biometricManager = BiometricManager.from(context)
        val authenticators = Authenticators.BIOMETRIC_STRONG or Authenticators.BIOMETRIC_WEAK
        return biometricManager.canAuthenticate(authenticators) == BiometricManager.BIOMETRIC_SUCCESS
    }

    fun promptBiometric(
        activity: FragmentActivity,
        title: String = "AlphaTrade Security",
        subtitle: String = "Authenticate to unlock",
        description: String = "Scan your fingerprint or use biometric verification to access your account",
        negativeButtonText: String = "Use PIN Passcode",
        onResult: (BiometricAuthResult) -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(activity)

        val promptInfoBuilder = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setDescription(description)
            .setConfirmationRequired(false)

        // If DEVICE_CREDENTIAL is used, negativeButton cannot be set.
        // We use BIOMETRIC_STRONG | BIOMETRIC_WEAK with negativeButtonText for custom PIN fallback
        promptInfoBuilder.setAllowedAuthenticators(Authenticators.BIOMETRIC_STRONG or Authenticators.BIOMETRIC_WEAK)
        promptInfoBuilder.setNegativeButtonText(negativeButtonText)

        val biometricPrompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onResult(BiometricAuthResult.Success)
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    when (errorCode) {
                        BiometricPrompt.ERROR_NEGATIVE_BUTTON -> {
                            onResult(BiometricAuthResult.FallbackToPin)
                        }
                        BiometricPrompt.ERROR_USER_CANCELED, BiometricPrompt.ERROR_CANCELED -> {
                            onResult(BiometricAuthResult.UserCanceled)
                        }
                        else -> {
                            onResult(BiometricAuthResult.Error(errorCode, errString.toString()))
                        }
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onResult(BiometricAuthResult.Failed)
                }
            }
        )

        try {
            biometricPrompt.authenticate(promptInfoBuilder.build())
        } catch (e: Exception) {
            onResult(BiometricAuthResult.Error(-1, e.message ?: "Authentication failed to start"))
        }
    }
}
