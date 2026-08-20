package com.example.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.OnboardingFormData
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BorderDark
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.GoldBeige
import com.example.ui.theme.GoldBeigeBg
import com.example.ui.theme.GoldBeigeLight
import com.example.ui.theme.KeypadGrey
import com.example.ui.theme.KeypadGreyActive
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TradingGreen

@Composable
fun OnboardingScreen(
    currentStep: Int,
    questionnaireSubStep: Int,
    formData: OnboardingFormData,
    onStepChange: (Int) -> Unit,
    onQuestionnaireSubStepChange: (Int) -> Unit,
    onKeypadDigit: (String) -> Unit,
    onKeypadDelete: () -> Unit,
    onSetCountry: (String, String, String) -> Unit,
    onTogglePolicy: (String) -> Unit,
    onAcceptAllPolicies: (List<String>) -> Unit,
    onSetDocumentUploaded: (Boolean, Boolean) -> Unit,
    onSetEmployment: (String) -> Unit,
    onSetCfdExp: (String) -> Unit,
    onToggleQualification: (String) -> Unit,
    onSetIndustry: (String) -> Unit,
    onSetIncome: (String) -> Unit,
    onToggleWealth: (String) -> Unit,
    onSetCurrency: (String, String) -> Unit,
    onComplete: () -> Unit,
    onBackToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Top Navigation / Back Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    if (currentStep == 5 && questionnaireSubStep > 1) {
                        onQuestionnaireSubStepChange(questionnaireSubStep - 1)
                    } else if (currentStep > 1) {
                        onStepChange(currentStep - 1)
                    } else {
                        onBackToHome()
                    }
                }
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }

            // Step Progress Bar
            val totalSteps = 5
            val currentProgress = if (currentStep == 5) {
                (4f + (questionnaireSubStep.toFloat() / 7f)) / totalSteps.toFloat()
            } else {
                currentStep.toFloat() / totalSteps.toFloat()
            }

            LinearProgressIndicator(
                progress = { currentProgress },
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = GoldBeige,
                trackColor = SurfaceElevated,
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "Step $currentStep of 5",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // Body Content Based on Current Step
        AnimatedContent(
            targetState = currentStep,
            transitionSpec = {
                (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                    slideOutHorizontally { width -> -width } + fadeOut()
                )
            },
            label = "OnboardingStepAnimation"
        ) { step ->
            when (step) {
                1 -> PhoneEntryStep(
                    formData = formData,
                    onKeypadDigit = onKeypadDigit,
                    onKeypadDelete = onKeypadDelete,
                    onSetCountry = onSetCountry,
                    onContinue = { onStepChange(2) }
                )
                2 -> TermsAndPoliciesStep(
                    formData = formData,
                    onTogglePolicy = onTogglePolicy,
                    onAcceptAll = onAcceptAllPolicies,
                    onConfirm = { onStepChange(3) }
                )
                3 -> CompleteAccountSetupPromptStep(
                    onVerifyIdentity = { onStepChange(4) }
                )
                4 -> DocumentVerificationStep(
                    formData = formData,
                    onSetDocument = onSetDocumentUploaded,
                    onContinue = { onStepChange(5) }
                )
                5 -> FinancialQuestionnaireWizard(
                    subStep = questionnaireSubStep,
                    formData = formData,
                    onSubStepChange = onQuestionnaireSubStepChange,
                    onSetEmployment = onSetEmployment,
                    onSetCfdExp = onSetCfdExp,
                    onToggleQualification = onToggleQualification,
                    onSetIndustry = onSetIndustry,
                    onSetIncome = onSetIncome,
                    onToggleWealth = onToggleWealth,
                    onSetCurrency = onSetCurrency,
                    onComplete = onComplete
                )
            }
        }
    }
}

// ----------------------------------------------------
// STEP 1: PHONE ENTRY STEP WITH GREY NUMERIC KEYPAD
// ----------------------------------------------------
@Composable
fun PhoneEntryStep(
    formData: OnboardingFormData,
    onKeypadDigit: (String) -> Unit,
    onKeypadDelete: () -> Unit,
    onSetCountry: (String, String, String) -> Unit,
    onContinue: () -> Unit
) {
    var countryMenuOpen by remember { mutableStateOf(false) }

    val countries = listOf(
        Triple("Kenya", "+254", "🇰🇪"),
        Triple("United States", "+1", "🇺🇸"),
        Triple("United Kingdom", "+44", "🇬🇧"),
        Triple("United Arab Emirates", "+971", "🇦🇪"),
        Triple("Germany", "+49", "🇩🇪"),
        Triple("South Africa", "+27", "🇿🇦")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Enter your phone number",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "We'll send you an SMS verification code to secure your trading account.",
                color = TextSecondary,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Country Selector & Phone Input Container
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceDark)
                    .border(1.dp, BorderDark, RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Country dropdown button with flag
                Box {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { countryMenuOpen = true }
                            .padding(vertical = 6.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = formData.countryFlag, fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = formData.countryCode,
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Select country",
                            tint = TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = countryMenuOpen,
                        onDismissRequest = { countryMenuOpen = false },
                        modifier = Modifier.background(SurfaceDark)
                    ) {
                        countries.forEach { (name, code, flag) ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(flag, fontSize = 18.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("$name ($code)", color = TextPrimary)
                                    }
                                },
                                onClick = {
                                    onSetCountry(code, name, flag)
                                    countryMenuOpen = false
                                }
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .width(1.dp)
                        .height(28.dp)
                        .background(BorderDark)
                )

                // Formatted phone number display
                Text(
                    text = formData.phoneNumber.ifEmpty { "Phone number" },
                    color = if (formData.phoneNumber.isEmpty()) TextMuted else TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Grey Numeric Keypad + Continue Button
        Column(modifier = Modifier.padding(bottom = 12.dp)) {
            // Numeric Keypad Grid
            val keypadRows = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf("", "0", "DEL")
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                keypadRows.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        row.forEach { key ->
                            if (key.isEmpty()) {
                                Spacer(modifier = Modifier.weight(1f))
                            } else {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = KeypadGrey,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(52.dp)
                                        .clickable {
                                            if (key == "DEL") onKeypadDelete() else onKeypadDigit(key)
                                        }
                                        .testTag("keypad_key_$key")
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (key == "DEL") {
                                            Icon(
                                                imageVector = Icons.Default.Backspace,
                                                contentDescription = "Delete",
                                                tint = TextPrimary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        } else {
                                            Text(
                                                text = key,
                                                color = TextPrimary,
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("phone_continue_button"),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                )
            ) {
                Text(
                    text = "Continue",
                    color = Color.Black,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ----------------------------------------------------
// STEP 2: TERMS AND POLICIES STEP
// ----------------------------------------------------
@Composable
fun TermsAndPoliciesStep(
    formData: OnboardingFormData,
    onTogglePolicy: (String) -> Unit,
    onAcceptAll: (List<String>) -> Unit,
    onConfirm: () -> Unit
) {
    val policies = listOf(
        "Terms and Conditions BAH",
        "Risk Disclosure Notice",
        "Order Execution Policy",
        "Privacy Policy",
        "Client Categorisation Policy",
        "Conflicts of Interest Policy",
        "Complaints Handling Procedure",
        "Cookies Notice & Policy",
        "T&C Refer-A-Friend Programme"
    )

    val allAccepted = policies.all { formData.acceptedPolicies.contains(it) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Terms & Policies",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Please review and confirm acceptance of our regulated financial operational terms.",
                color = TextSecondary,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Select All Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceDark)
                    .clickable {
                        if (allAccepted) {
                            onAcceptAll(emptyList())
                        } else {
                            onAcceptAll(policies)
                        }
                    }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Accept All Documents",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Checkbox(
                    checked = allAccepted,
                    onCheckedChange = {
                        if (allAccepted) onAcceptAll(emptyList()) else onAcceptAll(policies)
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = GoldBeige,
                        uncheckedColor = TextSecondary,
                        checkmarkColor = Color.Black
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Policies Scrollable List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .height(300.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(policies) { policy ->
                    val isChecked = formData.acceptedPolicies.contains(policy)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onTogglePolicy(policy) },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = if (isChecked) GoldBeige else TextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = policy,
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { onTogglePolicy(policy) },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = GoldBeige,
                                    uncheckedColor = TextSecondary,
                                    checkmarkColor = Color.Black
                                )
                            )
                        }
                    }
                }
            }
        }

        // Big White Confirm Button
        Column(modifier = Modifier.padding(bottom = 16.dp)) {
            Button(
                onClick = onConfirm,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("confirm_policies_button"),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                )
            ) {
                Text(
                    text = "Confirm",
                    color = Color.Black,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ----------------------------------------------------
// STEP 3: COMPLETE ACCOUNT SET-UP PROMPT WITH GOLD/BEIGE BUTTON
// ----------------------------------------------------
@Composable
fun CompleteAccountSetupPromptStep(
    onVerifyIdentity: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 40.dp)
        ) {
            // Shield & Gold Badge Graphic
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(GoldBeigeBg)
                    .border(2.dp, GoldBeige, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "Security Shield",
                    tint = GoldBeige,
                    modifier = Modifier.size(50.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Complete account set-up",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Identity verification is required before trading real funds or accessing leveraged CFD derivatives. It takes less than 2 minutes to complete.",
                color = TextSecondary,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Checklist Preview
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TradingGreen, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Government ID Document", color = TextPrimary, fontSize = 13.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TradingGreen, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Biometric Facial Liveness", color = TextPrimary, fontSize = 13.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TradingGreen, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Financial Suitability Questionnaire", color = TextPrimary, fontSize = 13.sp)
                    }
                }
            }
        }

        // Distinct Beige/Gold "Verify identity" button (#d4af37)
        Column(modifier = Modifier.padding(bottom = 20.dp)) {
            Button(
                onClick = onVerifyIdentity,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("verify_identity_button"),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldBeige,
                    contentColor = Color.Black
                )
            ) {
                Text(
                    text = "Verify identity",
                    color = Color.Black,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ----------------------------------------------------
// STEP 4: DOCUMENT VERIFICATION ("VERIFY IT'S YOU")
// ----------------------------------------------------
@Composable
fun DocumentVerificationStep(
    formData: OnboardingFormData,
    onSetDocument: (Boolean, Boolean) -> Unit,
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Verify it's you",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Please submit photos of your official ID and a quick selfie.",
                color = TextSecondary,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Option 1: Upload ID
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSetDocument(true, !formData.idUploaded) }
                    .testTag("option_upload_id"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (formData.idUploaded) SurfaceElevated else SurfaceDark
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (formData.idUploaded) TradingGreen else BorderDark
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (formData.idUploaded) TradingGreen.copy(0.2f) else SurfaceElevated),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = "Upload ID",
                            tint = if (formData.idUploaded) TradingGreen else TextPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Upload ID",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (formData.idUploaded) "National ID / Passport uploaded ✓" else "Passport, Driver's License or National ID",
                            color = if (formData.idUploaded) TradingGreen else TextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    if (formData.idUploaded) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Done", tint = TradingGreen)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Option 2: Selfie
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSetDocument(false, !formData.selfieTaken) }
                    .testTag("option_selfie"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (formData.selfieTaken) SurfaceElevated else SurfaceDark
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (formData.selfieTaken) TradingGreen else BorderDark
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (formData.selfieTaken) TradingGreen.copy(0.2f) else SurfaceElevated),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Face,
                            contentDescription = "Selfie",
                            tint = if (formData.selfieTaken) TradingGreen else TextPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Selfie",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (formData.selfieTaken) "Liveness scan captured ✓" else "Take a quick photo to verify liveness",
                            color = if (formData.selfieTaken) TradingGreen else TextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    if (formData.selfieTaken) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Done", tint = TradingGreen)
                    }
                }
            }
        }

        // Continue Button
        Column(modifier = Modifier.padding(bottom = 16.dp)) {
            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("documents_continue_button"),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                )
            ) {
                Text(
                    text = "Continue",
                    color = Color.Black,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ----------------------------------------------------
// STEP 5: FINANCIAL QUESTIONNAIRE MULTI-STEP WIZARD
// ----------------------------------------------------
@Composable
fun FinancialQuestionnaireWizard(
    subStep: Int,
    formData: OnboardingFormData,
    onSubStepChange: (Int) -> Unit,
    onSetEmployment: (String) -> Unit,
    onSetCfdExp: (String) -> Unit,
    onToggleQualification: (String) -> Unit,
    onSetIndustry: (String) -> Unit,
    onSetIncome: (String) -> Unit,
    onToggleWealth: (String) -> Unit,
    onSetCurrency: (String, String) -> Unit,
    onComplete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Spacer(modifier = Modifier.height(10.dp))

            when (subStep) {
                // 1. Employment Status
                1 -> {
                    Text("Employment Status", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text("Select your primary source of occupational status", color = TextSecondary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(20.dp))

                    val options = listOf("Employed", "Self-employed", "Retired", "Unemployed", "Student")
                    options.forEach { opt ->
                        RadioSelectCard(
                            label = opt,
                            isSelected = formData.employmentStatus == opt,
                            onClick = { onSetEmployment(opt) }
                        )
                    }
                }

                // 2. CFD Experience
                2 -> {
                    Text("CFD & Trading Experience", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text("How many years have you traded leveraged instruments?", color = TextSecondary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(20.dp))

                    val options = listOf("Less than 2 years", "2-4 years", "More than 4 years", "None at all")
                    options.forEach { opt ->
                        RadioSelectCard(
                            label = opt,
                            isSelected = formData.cfdExperience == opt,
                            onClick = { onSetCfdExp(opt) }
                        )
                    }
                }

                // 3. Qualifications
                3 -> {
                    Text("Trading Qualifications", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text("Select all relevant financial background that apply", color = TextSecondary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(20.dp))

                    val options = listOf(
                        "I have a relevant qualification",
                        "I have relevant work experience",
                        "All of the above",
                        "None of the above"
                    )
                    options.forEach { opt ->
                        CheckboxSelectCard(
                            label = opt,
                            isSelected = formData.qualifications.contains(opt),
                            onClick = { onToggleQualification(opt) }
                        )
                    }
                }

                // 4. Industry
                4 -> {
                    Text("Industry Sector", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text("Which sector represents your current or past profession?", color = TextSecondary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(20.dp))

                    val options = listOf(
                        "Admin, IT & business support",
                        "Financial services",
                        "Healthcare & education",
                        "Energy & Utilities",
                        "Engineering & Manufacturing"
                    )
                    options.forEach { opt ->
                        RadioSelectCard(
                            label = opt,
                            isSelected = formData.industry == opt,
                            onClick = { onSetIndustry(opt) }
                        )
                    }
                }

                // 5. Gross Annual Income
                5 -> {
                    Text("Gross Annual Income", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text("Approximate annual earnings before taxes", color = TextSecondary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(20.dp))

                    val options = listOf(
                        "$200,000 or more",
                        "$100,000-$199,999",
                        "$50,000-$99,999",
                        "$20,000-$49,999",
                        "Less than $20,000"
                    )
                    options.forEach { opt ->
                        RadioSelectCard(
                            label = opt,
                            isSelected = formData.grossIncome == opt,
                            onClick = { onSetIncome(opt) }
                        )
                    }
                }

                // 6. Origin of Income & Wealth
                6 -> {
                    Text("Origin of Income & Wealth", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text("Select all sources contributing to your trading funds", color = TextSecondary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(20.dp))

                    val options = listOf("Employment", "Savings", "Investments", "Pension", "Inheritance")
                    options.forEach { opt ->
                        CheckboxSelectCard(
                            label = opt,
                            isSelected = formData.originOfWealth.contains(opt),
                            onClick = { onToggleWealth(opt) }
                        )
                    }
                }

                // 7. Currency Selection
                7 -> {
                    CurrencySelectionStep(
                        selectedCurrency = formData.accountCurrency,
                        onSelectCurrency = onSetCurrency
                    )
                }
            }
        }

        // Bottom Wizard Action Button
        Column(modifier = Modifier.padding(bottom = 16.dp)) {
            Button(
                onClick = {
                    if (subStep < 7) {
                        onSubStepChange(subStep + 1)
                    } else {
                        onComplete()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag(if (subStep == 7) "create_account_button" else "wizard_next_button"),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                )
            ) {
                Text(
                    text = if (subStep == 7) "Create account" else "Next (${subStep}/7)",
                    color = Color.Black,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ----------------------------------------------------
// CURRENCY SELECTION SUB-STEP (Tabs: Popular / All)
// ----------------------------------------------------
@Composable
fun CurrencySelectionStep(
    selectedCurrency: String,
    onSelectCurrency: (String, String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Popular, 1: All

    val popularCurrencies = listOf(
        Triple("US Dollar (USD)", "USD", "$"),
        Triple("Euro (EUR)", "EUR", "€"),
        Triple("British Pound (GBP)", "GBP", "£"),
        Triple("Swiss Franc (CHF)", "CHF", "Fr"),
        Triple("UAE Dirham (AED)", "AED", "د.إ")
    )

    val allCurrencies = popularCurrencies + listOf(
        Triple("Japanese Yen (JPY)", "JPY", "¥"),
        Triple("Australian Dollar (AUD)", "AUD", "A$"),
        Triple("Canadian Dollar (CAD)", "CAD", "C$"),
        Triple("Singapore Dollar (SGD)", "SGD", "S$")
    )

    Column {
        Text("Choose account currency", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text("Your base currency for balances, deposits and margin calculations.", color = TextSecondary, fontSize = 13.sp)

        Spacer(modifier = Modifier.height(16.dp))

        // Tabs: Popular and All
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceDark)
                .border(1.dp, BorderDark, RoundedCornerShape(12.dp))
                .padding(3.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (selectedTab == 0) SurfaceElevated else Color.Transparent)
                    .clickable { selectedTab = 0 },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Popular",
                    color = if (selectedTab == 0) TextPrimary else TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (selectedTab == 1) SurfaceElevated else Color.Transparent)
                    .clickable { selectedTab = 1 },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "All",
                    color = if (selectedTab == 1) TextPrimary else TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val list = if (selectedTab == 0) popularCurrencies else allCurrencies
        list.forEach { (name, code, symbol) ->
            val isSel = selectedCurrency == name
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onSelectCurrency(name, code) },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSel) SurfaceElevated else SurfaceDark
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isSel) GoldBeige else BorderDark
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = if (isSel) GoldBeigeBg else SurfaceElevated,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(symbol, color = if (isSel) GoldBeige else TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(name, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }

                    if (isSel) {
                        Icon(Icons.Default.Check, contentDescription = "Selected", tint = GoldBeige)
                    }
                }
            }
        }
    }
}

// Reusable Radio Card
@Composable
fun RadioSelectCard(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) SurfaceElevated else SurfaceDark
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) GoldBeige else BorderDark
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = GoldBeige,
                    unselectedColor = TextSecondary
                )
            )
        }
    }
}

// Reusable Checkbox Card
@Composable
fun CheckboxSelectCard(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) SurfaceElevated else SurfaceDark
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) GoldBeige else BorderDark
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onClick() },
                colors = CheckboxDefaults.colors(
                    checkedColor = GoldBeige,
                    uncheckedColor = TextSecondary,
                    checkmarkColor = Color.Black
                )
            )
        }
    }
}
