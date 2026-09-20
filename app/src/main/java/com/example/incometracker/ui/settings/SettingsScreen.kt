package com.example.incometracker.ui.settings

import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.incometracker.data.AppDatabase
import com.example.incometracker.data.ThemePreferences
import com.example.incometracker.ui.components.*
import com.example.incometracker.util.CsvExporter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val themePreferences = remember { ThemePreferences(context) }
    val themeMode by themePreferences.themeMode.collectAsState(initial = ThemePreferences.ThemeMode.SYSTEM)
    val scope = rememberCoroutineScope()
    
    var showThemeDialog by remember { mutableStateOf(false) }
    var isExporting by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SlideInContainer(delay = 0) {
                SettingItem(
                    icon = Icons.Default.DarkMode,
                    title = "Theme",
                    subtitle = when(themeMode) {
                        ThemePreferences.ThemeMode.LIGHT -> "Light"
                        ThemePreferences.ThemeMode.DARK -> "Dark"
                        ThemePreferences.ThemeMode.SYSTEM -> "System Default"
                    },
                    onClick = { showThemeDialog = true }
                )
            }

            SlideInContainer(delay = 100) {
                SettingItem(
                    icon = Icons.Default.FileDownload,
                    title = "Export to CSV",
                    subtitle = "Download your income data",
                    onClick = {
                        isExporting = true
                        scope.launch {
                            try {
                                val app = context.applicationContext as Application
                                val db = AppDatabase.getDatabase(app, scope)
                                val records = db.incomeDao().getAllRecords().first()
                                
                                val file = CsvExporter.exportToCsv(context, records)
                                CsvExporter.shareFile(context, file)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            } finally {
                                isExporting = false
                            }
                        }
                    }
                )
            }

            SlideInContainer(delay = 200) {
                SettingItem(
                    icon = Icons.Default.Lock,
                    title = "Security",
                    subtitle = "PIN & biometric settings",
                    onClick = { }
                )
            }

            SlideInContainer(delay = 300) {
                SettingItem(
                    icon = Icons.Default.Backup,
                    title = "Backup & Restore",
                    subtitle = "Cloud backup options",
                    onClick = { }
                )
            }

            SlideInContainer(delay = 400) {
                SettingItem(
                    icon = Icons.Default.Info,
                    title = "About",
                    subtitle = "Trackr v1.0.0",
                    onClick = { }
                )
            }
        }
        
        if (isExporting) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }

    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("Choose Theme") },
            text = {
                Column {
                    ThemePreferences.ThemeMode.values().forEach { mode ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    scope.launch {
                                        themePreferences.setThemeMode(mode)
                                        showThemeDialog = false
                                    }
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = themeMode == mode,
                                onClick = null
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = when(mode) {
                                    ThemePreferences.ThemeMode.LIGHT -> "Light"
                                    ThemePreferences.ThemeMode.DARK -> "Dark"
                                    ThemePreferences.ThemeMode.SYSTEM -> "System Default"
                                }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SettingItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Open",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
