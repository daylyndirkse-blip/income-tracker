package com.example.incometracker.ui.profile

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
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
import com.example.incometracker.ui.components.DarkCard
import com.example.incometracker.ui.components.InitialsAvatar
import com.example.incometracker.ui.components.SlideInContainer
import com.example.incometracker.ui.theme.CardBg
import com.example.incometracker.ui.theme.Purple
import com.example.incometracker.ui.theme.TextMed

@Composable
fun ProfileScreen(
    onOpenSettings: () -> Unit,
    onOpenPinSetup: () -> Unit,
    vm: ProfileViewModel = viewModel()
) {
    val name by vm.userName.collectAsState()
    var editName by remember { mutableStateOf("") }
    var editing by remember { mutableStateOf(false) }

    LaunchedEffect(name) { if (editName.isEmpty()) editName = name }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(Modifier.height(8.dp))

        SlideInContainer(0) {
            Text("Profile", style = MaterialTheme.typography.headlineLarge, color = Color.White)
        }

        SlideInContainer(1) {
            DarkCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    InitialsAvatar(name = name.ifEmpty { "?" }, size = 64.dp)
                    Column {
                        Text(
                            text = name.ifEmpty { "Set your name" },
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(
                            text = "Income Tracker",
                            color = TextMed,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                if (editing) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Your name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Purple,
                            unfocusedBorderColor = Color(0xFF333333)
                        )
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = {
                            vm.setName(editName)
                            editing = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Purple)
                    ) { Text("Save") }
                } else {
                    OutlinedButton(
                        onClick = { editing = true },
                        modifier = Modifier.fillMaxWidth(),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Purple)
                    ) { Text("Edit Name", color = Purple) }
                }
            }
        }

        SlideInContainer(2) {
            DarkCard(modifier = Modifier.fillMaxWidth()) {
                Text("Security", color = TextMed, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(12.dp))

                ProfileMenuItem(
                    icon = Icons.Default.Lock,
                    title = "PIN & Biometric Lock",
                    subtitle = "Set up app lock",
                    onClick = onOpenPinSetup
                )
            }
        }

        SlideInContainer(3) {
            DarkCard(modifier = Modifier.fillMaxWidth()) {
                Text("Preferences", color = TextMed, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(12.dp))

                ProfileMenuItem(
                    icon = Icons.Default.Person,
                    title = "App Settings",
                    subtitle = "Currency, week start",
                    onClick = onOpenSettings
                )
            }
        }
    }
}

@Composable
private fun ProfileMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    androidx.compose.foundation.clickable(onClick = onClick)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF232320))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Purple.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Purple, modifier = Modifier.size(20.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color.White, fontWeight = FontWeight.Medium)
            Text(subtitle, color = TextMed, fontSize = 12.sp)
        }
        Icon(
            androidx.compose.material.icons.Icons.Default.ChevronRight,
            contentDescription = null,
            tint = TextMed,
            modifier = Modifier.size(20.dp)
        )
    }
}
