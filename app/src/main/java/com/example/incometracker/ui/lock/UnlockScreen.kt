package com.example.incometracker.ui.lock

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun UnlockScreen(vm: UnlockViewModel = viewModel()) {
    val st by vm.state.collectAsState()
    val useBio by vm.useBiometric.collectAsState()

    val activity = (LocalContext.current as FragmentActivity)
    val executor = remember { ContextCompat.getMainExecutor(activity) }

    fun canAuthenticate(): Boolean {
        val mgr = BiometricManager.from(activity)
        return mgr.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
        ) == BiometricManager.BIOMETRIC_SUCCESS
    }

    fun showBiometricPrompt() {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock")
            .setSubtitle("Use biometric or device credential")
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
            .build()

        val prompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    vm.unlockSuccess()
                }
            }
        )
        prompt.authenticate(promptInfo)
    }

    LaunchedEffect(useBio) {
        if (useBio && canAuthenticate()) showBiometricPrompt()
    }

    var showPin by remember { mutableStateOf(false) }

    Surface(color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text("App Locked", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = st.pin,
                onValueChange = vm::setPin,
                label = { Text("PIN (4–8 digits)") },
                singleLine = true,
                visualTransformation = if (showPin) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                trailingIcon = {
                    IconButton(onClick = { showPin = !showPin }) {
                        Icon(
                            imageVector = if (showPin) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (showPin) "Hide PIN" else "Show PIN"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            if (st.error != null) {
                Spacer(Modifier.height(8.dp))
                Text(st.error!!, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = vm::unlockWithPin,
                modifier = Modifier.fillMaxWidth(),
                enabled = st.pin.length >= 4
            ) { Text("Unlock") }

            if (useBio && canAuthenticate()) {
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = { showBiometricPrompt() }, modifier = Modifier.fillMaxWidth()) {
                    Text("Use biometric/device credential")
                }
            }
        }
    }
}
