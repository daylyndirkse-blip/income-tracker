package com.example.incometracker.ui.profile

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.incometracker.ui.components.InitialsAvatar
import com.example.incometracker.ui.components.SlideInContainer
import com.example.incometracker.ui.theme.*

@Composable
fun ProfileScreen(
    onOpenSettings: () -> Unit,
    onOpenPinSetup: () -> Unit,
    vm: ProfileViewModel = viewModel()
) {
    val name by vm.userName.collectAsState()
    var editName by remember { mutableStateOf("") }
    var editing by remember { mutableStateOf(false) }

    LaunchedEffect(name) {
        if (editName.isEmpty() && name.isNotEmpty()) editName = name
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
            Text(
                "Profile",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White
            )
        }

        // Avatar + name card
        SlideInContainer(1) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(CardBg)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    InitialsAvatar(name = name.ifEmpty { "?" }, size = 72.dp)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = name.ifEmpty { "Set your name" },
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                        Text(
                            text = "Income Tracker",
                            color = TextMed,
                            fontSize = 13.sp
                        )
                    }
                }

                AnimatedVisibility(
                    visible = editing,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = editName,
                            onValueChange = { editName = it },
                            label = { Text("Your name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Purple,
                                unfocusedBorderColor = Color(0xFF333333),
                                focusedContainerColor = CardBg2,
                                unfocusedContainerColor = CardBg2,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        Button(
                            onClick = {
                                vm.setName(editName.trim())
                                editing = false
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Purple),
                            shape = RoundedCornerShape(14.dp)
                        ) { Text("Save Name", fontWeight = FontWeight.SemiBold) }
                    }
                }

                if (!editing) {
                    OutlinedButton(
                        onClick = { editing = true },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Purple),
                        shape = RoundedCornerShape(14.dp)
                    ) { Text("Edit Name", color = Purple, fontWeight = FontWeight.SemiBold) }
                }
            }
        }

        // Security section
        SlideInContainer(2) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(CardBg)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    "SECURITY",
                    color = TextLow,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
                Spacer(Modifier.height(8.dp))
                ProfileMenuItem(
                    icon = Icons.Default.Lock,
                    title = "PIN & Biometric Lock",
                    subtitle = "Locks after 3 min in background",
                    onClick = onOpenPinSetup
                )
            }
        }

        // Preferences section
        SlideInContainer(3) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(CardBg)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    "PREFERENCES",
                    color = TextLow,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
                Spacer(Modifier.height(8.dp))
                ProfileMenuItem(
                    icon = Icons.Default.Settings,
                    title = "App Settings",
                    subtitle = "Currency, week start & more",
                    onClick = onOpenSettings
                )
            }
        }

        // App info
        SlideInContainer(4) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("Income Tracker", color = TextMed, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Text("Version 1.0.0", color = TextLow, fontSize = 12.sp)
            }
        }

        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardBg2)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Purple.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = Purple,
                modifier = Modifier.size(22.dp)
            )
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Text(subtitle, color = TextMed, fontSize = 12.sp)
        }
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = TextLow,
            modifier = Modifier.size(20.dp)
        )
    }
}
