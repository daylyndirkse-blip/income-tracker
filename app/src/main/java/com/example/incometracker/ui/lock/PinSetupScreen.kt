package com.example.incometracker.ui.lock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.incometracker.ui.theme.*

@Composable
fun PinSetupScreen(onDone: () -> Unit, vm: PinSetupViewModel = viewModel()) {
    val st by vm.state.collectAsState()
    var showPin by remember { mutableStateOf(false) }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Purple,
        unfocusedBorderColor = Color(0xFF333333),
        focusedContainerColor = CardBg,
        unfocusedContainerColor = CardBg,
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White
    )

    fun trailing() = @Composable {
        IconButton(onClick = { showPin = !showPin }) {
            Icon(
                if (showPin) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                contentDescription = null,
                tint = TextMed
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(Modifier.height(8.dp))

        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            IconButton(onClick = onDone) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
            }
            Text("PIN Setup", style = MaterialTheme.typography.headlineMedium, color = Color.White)
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
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = fieldColors
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
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = fieldColors
        )

        OutlinedTextField(
            value = st.confirmPin,
            onValueChange = vm::setConfirmPin,
            label = { Text("Confirm PIN") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            visualTransformation = if (showPin) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = trailing(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = fieldColors
        )

        if (st.error != null) {
            Text(st.error!!, color = Danger, fontSize = 13.sp)
        }

        Button(
            onClick = { vm.save(onDone) },
            modifier = Modifier.fillMaxWidth().height(54.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Purple),
            shape = RoundedCornerShape(16.dp)
        ) { Text("Save PIN", fontWeight = FontWeight.Bold, fontSize = 16.sp) }

        if (st.hasExistingPin) {
            OutlinedButton(
                onClick = { vm.clearPin(onDone) },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Danger),
                shape = RoundedCornerShape(16.dp)
            ) { Text("Remove PIN", color = Danger, fontWeight = FontWeight.SemiBold) }
        }
    }
}
