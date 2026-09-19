package com.example.incometracker.ui.add

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.incometracker.data.IncomeSourceEntity
import com.example.incometracker.data.Recurrence
import com.example.incometracker.ui.theme.*
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddIncomeScreen(
    onDone: () -> Unit,
    initialDate: LocalDate? = null,
    entryId: Long? = null,
    vm: AddIncomeViewModel = viewModel()
) {
    val state by vm.state.collectAsState()
    val sources by vm.sources.collectAsState()
    val isEditing = entryId != null

    LaunchedEffect(entryId) { if (entryId != null) vm.loadForEdit(entryId) }
    LaunchedEffect(initialDate, entryId) { if (!isEditing && initialDate != null) vm.setDate(initialDate) }

    var showDate by remember { mutableStateOf(false) }

    if (showDate) {
        val initMillis = state.date.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
        val pickerState = rememberDatePickerState(initialSelectedDateMillis = initMillis)
        DatePickerDialog(
            onDismissRequest = { showDate = false },
            confirmButton = {
                TextButton(onClick = {
                    val millis = pickerState.selectedDateMillis
                    if (millis != null) {
                        vm.setDate(Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate())
                    }
                    showDate = false
                }) { Text("OK", color = Purple) }
            },
            colors = DatePickerDefaults.colors(containerColor = CardBg)
        ) { DatePicker(state = pickerState) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(onClick = onDone) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text(
                if (isEditing) "Edit Income" else "Add Income",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )
        }

        // Amount field
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Amount", color = TextMed, fontSize = 13.sp)
            OutlinedTextField(
                value = state.amount,
                onValueChange = vm::setAmount,
                placeholder = { Text("0.00", color = TextLow) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
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
                leadingIcon = {
                    Text(" $", color = TextMed, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            )
        }

        // Source picker
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Income Source", color = TextMed, fontSize = 13.sp)
            RedesignedSourcePicker(
                sources = sources,
                selectedId = state.selectedSourceId,
                onSelect = vm::setSource,
                newSourceName = state.newSourceName,
                onNewSourceName = vm::setNewSourceName,
                onCreateSource = vm::createSourceAndSelect
            )
        }

        // Date picker
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Date", color = TextMed, fontSize = 13.sp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardBg)
                    .border(1.dp, Color(0xFF333333), RoundedCornerShape(16.dp))
                    .clickable { showDate = true }
                    .padding(16.dp)
            ) {
                Text(
                    state.date.format(java.time.format.DateTimeFormatter.ofPattern("MMM d, yyyy")),
                    color = Color.White
                )
            }
        }

        // Income type (hide when editing)
        if (!isEditing) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Income Type", color = TextMed, fontSize = 13.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        Recurrence.NONE to "Once-off",
                        Recurrence.DAILY to "Daily",
                        Recurrence.WEEKLY to "Weekly",
                        Recurrence.MONTHLY to "Monthly"
                    ).forEach { (rec, label) ->
                        val selected = state.recurrence == rec
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (selected) Purple else CardBg)
                                .border(
                                    1.dp,
                                    if (selected) Purple else Color(0xFF333333),
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { vm.setRecurrence(rec) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                label,
                                color = if (selected) Color.White else TextMed,
                                fontSize = 11.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        // Note
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Note (optional)", color = TextMed, fontSize = 13.sp)
            OutlinedTextField(
                value = state.note,
                onValueChange = vm::setNote,
                placeholder = { Text("Add a note...", color = TextLow) },
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
                minLines = 2
            )
        }

        // Error
        if (state.error != null) {
            Text(state.error!!, color = Danger, fontSize = 13.sp)
        }

        // Save button
        Button(
            onClick = { vm.save(onDone) },
            enabled = !state.saving,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Purple),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                if (state.saving) "Saving..." else if (isEditing) "Update Income" else "Save Income",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        Spacer(Modifier.height(20.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RedesignedSourcePicker(
    sources: List<IncomeSourceEntity>,
    selectedId: Long?,
    onSelect: (Long) -> Unit,
    newSourceName: String,
    onNewSourceName: (String) -> Unit,
    onCreateSource: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedName = sources.firstOrNull { it.id == selectedId }?.name ?: "Select a source"

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selectedName,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Purple,
                unfocusedBorderColor = Color(0xFF333333),
                focusedContainerColor = CardBg,
                unfocusedContainerColor = CardBg,
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
            sources.forEach { s ->
                DropdownMenuItem(
                    text = { Text(s.name, color = Color.White) },
                    onClick = { onSelect(s.id); expanded = false }
                )
            }
        }
    }

    Spacer(Modifier.height(8.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = newSourceName,
            onValueChange = onNewSourceName,
            placeholder = { Text("New source name", color = TextLow) },
            singleLine = true,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Purple,
                unfocusedBorderColor = Color(0xFF333333),
                focusedContainerColor = CardBg,
                unfocusedContainerColor = CardBg,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )
        Button(
            onClick = onCreateSource,
            colors = ButtonDefaults.buttonColors(containerColor = Purple),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.height(56.dp)
        ) { Text("Add") }
    }
}
