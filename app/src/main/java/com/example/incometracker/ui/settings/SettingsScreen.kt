package com.example.incometracker.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.incometracker.ui.components.SlideInContainer
import com.example.incometracker.ui.theme.*
import com.example.incometracker.util.WeekStart

private val SupportedCurrencies = listOf(
    "USD" to "🇺🇸 USD - US Dollar",
    "EUR" to "🇪🇺 EUR - Euro",
    "GBP" to "🇬🇧 GBP - British Pound",
    "ZAR" to "🇿🇦 ZAR - South African Rand",
    "KES" to "🇰🇪 KES - Kenyan Shilling",
    "NGN" to "🇳🇬 NGN - Nigerian Naira",
    "GHS" to "🇬🇭 GHS - Ghanaian Cedi",
    "AUD" to "🇦🇺 AUD - Australian Dollar",
    "CAD" to "🇨🇦 CAD - Canadian Dollar",
    "INR" to "🇮🇳 INR - Indian Rupee"
)

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
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(Modifier.height(8.dp))

        SlideInContainer(0) {
            Text("Settings", style = MaterialTheme.typography.headlineLarge, color = Color.White)
        }

        // Currency
        SlideInContainer(1) {
            SettingSection(title = "Currency") {
                var expanded by remember { mutableStateOf(false) }
                val selectedLabel = SupportedCurrencies.firstOrNull { it.first == currency }?.second ?: currency

                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                    OutlinedTextField(
                        value = selectedLabel,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Purple,
                            unfocusedBorderColor = Color(0xFF333333),
                            focusedContainerColor = CardBg2,
                            unfocusedContainerColor = CardBg2,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(CardBg)
                    ) {
                        SupportedCurrencies.forEach { (code, label) ->
                            DropdownMenuItem(
                                text = { Text(label, color = if (code == currency) Purple else Color.White) },
                                onClick = { vm.setCurrency(code); expanded = false }
                            )
                        }
                    }
                }
            }
        }

        // Week start
        SlideInContainer(2) {
            SettingSection(title = "Week Starts On") {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    listOf(WeekStart.MONDAY to "Monday", WeekStart.SUNDAY to "Sunday").forEach { (ws, label) ->
                        val selected = weekStart == ws
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (selected) Purple else CardBg2)
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                label,
                                color = if (selected) Color.White else TextMed,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        // Security
        SlideInContainer(3) {
            SettingSection(title = "Security") {
                SettingRow(
                    title = "Enable App Lock",
                    subtitle = "Locks after 3 minutes in background"
                ) {
                    Switch(
                        checked = lockEnabled,
                        onCheckedChange = vm::setLockEnabled,
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Purple)
                    )
                }

                Spacer(Modifier.height(10.dp))

                SettingRow(
                    title = "Biometric Unlock",
                    subtitle = "Use fingerprint or face unlock"
                ) {
                    Switch(
                        checked = useBiometric,
                        onCheckedChange = vm::setUseBiometric,
                        enabled = lockEnabled,
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Purple)
                    )
                }

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = onOpenPinSetup,
                    enabled = lockEnabled,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Purple),
                    shape = RoundedCornerShape(14.dp)
                ) { Text(if (pinSet) "Change PIN" else "Set PIN", fontWeight = FontWeight.SemiBold) }
            }
        }

        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun SettingSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CardBg)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(title, color = TextMed, fontSize = 12.sp, fontWeight = FontWeight.Medium, letterSpacing = 0.5.sp)
        content()
    }
}

@Composable
private fun SettingRow(title: String, subtitle: String, control: @Composable () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color.White, fontWeight = FontWeight.Medium)
            Text(subtitle, color = TextMed, fontSize = 12.sp)
        }
        control()
    }
}
