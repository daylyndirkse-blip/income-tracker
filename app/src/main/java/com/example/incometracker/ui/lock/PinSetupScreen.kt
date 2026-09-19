package com.example.incometracker.ui.lock

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun PinSetupScreen(onDone: () -> Unit, vm: PinSetupViewModel = viewModel()) {
    val st by vm.state.collectAsState()
    var showPin by remember { mutableStateOf(false) }

    Column(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("PIN Setup", style = MaterialTheme.typography.titleLarge)

        fun trailing() = @Composable {
            IconButton(onClick = { showPin = !showPin }) {
                Icon(
                    imageVector = if (showPin) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                    contentDescription = if (showPin) "Hide PIN" else "Show PIN"
                )
            }
        }

        if (st.hasExistingPin) {
            OutlinedTextField(
                value = st.currentPin,
                onValueChange = vm::setCurrentPin,
                label = { Text("Current PIN") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                visualTransformation = if (showPin) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = trailing(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        OutlinedTextField(
            value = st.newPin,
            onValueChange = vm::setNewPin,
            label = { Text("New PIN (4–8 digits)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            visualTransformation = if (showPin) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = trailing(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = st.confirmPin,
            onValueChange = vm::setConfirmPin,
            label = { Text("Confirm PIN") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            visualTransformation = if (showPin) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = trailing(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        if (st.error != null) Text(st.error!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { vm.save(onDone) }, modifier = Modifier.fillMaxWidth()) {
            Text("Save PIN")
        }

        OutlinedButton(
            onClick = { vm.clearPin(onDone) },
            modifier = Modifier.fillMaxWidth(),
            enabled = st.hasExistingPin
        ) { Text("Clear PIN") }
    }
}
