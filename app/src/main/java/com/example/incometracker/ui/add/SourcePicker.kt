package com.example.incometracker.ui.add

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.incometracker.data.IncomeSourceEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SourcePicker(
    sources: List<IncomeSourceEntity>,
    selectedId: Long?,
    onSelect: (Long) -> Unit,
    newSourceName: String,
    onNewSourceName: (String) -> Unit,
    onCreateSource: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedName = sources.firstOrNull { it.id == selectedId }?.name ?: "Select source"

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
            OutlinedTextField(
                value = selectedName,
                onValueChange = {},
                readOnly = true,
                label = { Text("Income source") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                sources.forEach { s ->
                    DropdownMenuItem(
                        text = { Text(s.name) },
                        onClick = { onSelect(s.id); expanded = false }
                    )
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = newSourceName,
                onValueChange = onNewSourceName,
                label = { Text("Add new source") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            Button(onClick = onCreateSource) { Text("Add") }
        }
    }
}
