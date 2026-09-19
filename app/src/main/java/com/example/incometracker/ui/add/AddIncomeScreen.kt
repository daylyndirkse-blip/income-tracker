package com.example.incometracker.ui.add

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.incometracker.data.Recurrence
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
                        val d = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
                        vm.setDate(d)
                    }
                    showDate = false
                }) { Text("OK") }
            }
        ) { DatePicker(state = pickerState) }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(if (isEditing) "Edit Income" else "Add Income", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = state.amount,
            onValueChange = vm::setAmount,
            label = { Text("Amount") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        SourcePicker(
            sources = sources,
            selectedId = state.selectedSourceId,
            onSelect = vm::setSource,
            newSourceName = state.newSourceName,
            onNewSourceName = vm::setNewSourceName,
            onCreateSource = vm::createSourceAndSelect
        )

        OutlinedButton(onClick = { showDate = true }) { Text("Date: ${state.date}") }

        if (!isEditing) {
            Text("Income type", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = state.recurrence == Recurrence.NONE, onClick = { vm.setRecurrence(Recurrence.NONE) }, label = { Text("Once-off") })
                FilterChip(selected = state.recurrence == Recurrence.DAILY, onClick = { vm.setRecurrence(Recurrence.DAILY) }, label = { Text("Daily") })
                FilterChip(selected = state.recurrence == Recurrence.WEEKLY, onClick = { vm.setRecurrence(Recurrence.WEEKLY) }, label = { Text("Weekly") })
                FilterChip(selected = state.recurrence == Recurrence.MONTHLY, onClick = { vm.setRecurrence(Recurrence.MONTHLY) }, label = { Text("Monthly") })
            }
        }

        OutlinedTextField(
            value = state.note,
            onValueChange = vm::setNote,
            label = { Text("Note (optional)") },
            modifier = Modifier.fillMaxWidth()
        )

        if (state.error != null) Text(state.error!!, color = MaterialTheme.colorScheme.error)

        Button(
            onClick = { vm.save(onDone) },
            enabled = !state.saving,
            modifier = Modifier.fillMaxWidth()
        ) { Text(if (isEditing) "Update" else "Save") }
    }
}
