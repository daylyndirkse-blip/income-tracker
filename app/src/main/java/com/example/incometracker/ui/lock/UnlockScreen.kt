package com.example.incometracker.ui.lock

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.incometracker.ui.theme.*

@Composable
fun UnlockScreen(vm: UnlockViewModel = viewModel()) {
    val st by vm.state.collectAsState()
    val useBio by vm.useBiometric.collectAsState()
    val activity = LocalContext.current as FragmentActivity
    val executor = remember { ContextCompat.getMainExecutor(activity) }

    var showPin by remember { mutableStateOf(false) }

    fun canAuthenticate(): Boolean {
        val mgr = BiometricManager.from(activity)
        return mgr.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
            BiometricManager.Authenticators.DEVICE_CREDENTIAL
        ) == BiometricManager.BIOMETRIC_SUCCESS
    }

    fun showBiometricPrompt() {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock Income Tracker")
            .setSubtitle("Use your biometric credential")
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
            .build()

        BiometricPrompt(activity, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                vm.unlockSuccess()
            }
        }).authenticate(promptInfo)
    }

    LaunchedEffect(useBio) {
        if (useBio && canAuthenticate()) showBiometricPrompt()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg),
        contentAlignment = Alignment.Center
    ) {
        // Background glow
        Box(
            modifier = Modifier
                .size(280.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Purple.copy(alpha = 0.12f), Color.Transparent)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(listOf(Purple, Color(0xFF4C1D95)))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("₿", fontSize = 36.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }

            Text("Welcome Back", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Text("Enter your PIN to continue", color = TextMed, fontSize = 14.sp)

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = st.pin,
                onValueChange = vm::setPin,
                label = { Text("PIN (4–8 digits)") },
                singleLine = true,
                visualTransformation = if (showPin) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Purple,
                    unfocusedBorderColor = Color(0xFF333333),
                    focusedContainerColor = CardBg,
                    unfocusedContainerColor = CardBg,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                trailingIcon = {
                    IconButton(onClick = { showPin = !showPin }) {
                        Icon(
                            if (showPin) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = null,
                            tint = TextMed
                        )
                    }
                }
            )

            AnimatedVisibility(visible = st.error != null) {
                Text(st.error ?: "", color = Danger, fontSize = 13.sp)
            }

            Button(
                onClick = vm::unlockWithPin,
                enabled = st.pin.length >= 4,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Purple),
                shape = RoundedCornerShape(16.dp)
            ) { Text("Unlock", fontWeight = FontWeight.Bold, fontSize = 16.sp) }

            if (useBio && canAuthenticate()) {
                OutlinedButton(
                    onClick = { showBiometricPrompt() },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Purple),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Fingerprint, contentDescription = null, tint = Purple, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Use Biometric", color = Purple, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
