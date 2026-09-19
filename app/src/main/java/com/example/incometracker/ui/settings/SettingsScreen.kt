package com.example.incometracker.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.incometracker.util.WeekStart

private val SupportedCurrencies = listOf("USD", "EUR", "GBP", "ZAR", "KES", "NGN", "GHS", "AUD", "CAD", "INR")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onOpenPinSetup: () -> Unit,
    vm: SettingsViewModel = viewModel()
) {
    val currency by vm.currency.collectAsState()
    val weekStart by vm.weekStart.collectAsState()

    val lockEnabled by vm.lockEnabled.collectAsState()
    val useBiometric by vm.useBiometric.collectAsState()
    val pinSet by vm.pinSet.collectAsState()

    LaunchedEffect(lockEnabled, pinSet) {
        if (lockEnabled && !pinSet) onOpenPinSetup()
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Settings", style = MaterialTheme.typography.titleLarge)

        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("General", style = MaterialTheme.typography.titleMedium)

                DropdownSetting(
                    label = "Currency",
                    value = currency,
                    options = SupportedCurrencies,
                    onSelect = vm::setCurrency
                )

                DropdownSetting(
                    label = "Week starts on",
                    value = weekStart.name.lowercase().replaceFirstChar { it.uppercase() },
                    options = listOf("Monday", "Sunday"),
                    onSelect = { v -> vm.setWeekStart(if (v == "Sunday") WeekStart.SUNDAY else WeekStart.MONDAY) }
                )
            }
        }

        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("App Lock (optional)", style = MaterialTheme.typography.titleMedium)

                SettingSwitch("Enable app lock", lockEnabled, vm::setLockEnabled)
                SettingSwitch("Use biometric/device credential", useBiometric, vm::setUseBiometric, enabled = lockEnabled)

                Button(
                    onClick = onOpenPinSetup,
                    enabled = lockEnabled,
                    modifier = Modifier.fillMaxWidth()
                ) { Text(if (pinSet) "Change PIN" else "Set PIN") }

                Text(
                    "Lock triggers if app was in background for 3 minutes.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownSetting(
    label: String,
    value: String,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            readOnly = true,
            value = value,
            onValueChange = {},
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { opt ->
                DropdownMenuItem(
                    text = { Text(opt) },
                    onClick = { onSelect(opt); expanded = false }
                )
            }
        }
    }
}

@Composable
private fun SettingSwitch(title: String, checked: Boolean, onChecked: (Boolean) -> Unit, enabled: Boolean = true) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(title, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onChecked, enabled = enabled)
    }
}
