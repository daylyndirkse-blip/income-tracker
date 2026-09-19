package com.example.incometracker.ui.add

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.incometracker.data.DatabaseProvider
import com.example.incometracker.data.IncomeRepository
import com.example.incometracker.data.Recurrence
import com.example.incometracker.util.parseAmountToCents
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

data class AddIncomeState(
    val amount: String = "",
    val selectedSourceId: Long? = null,
    val newSourceName: String = "",
    val date: LocalDate = LocalDate.now(),
    val recurrence: Recurrence = Recurrence.NONE,
    val note: String = "",
    val editingEntryId: Long? = null,
    val error: String? = null,
    val saving: Boolean = false
)

class AddIncomeViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = DatabaseProvider.get(app).incomeDao()
    private val repo = IncomeRepository(dao)

    val sources = repo.observeSources()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _state = MutableStateFlow(AddIncomeState())
    val state = _state.asStateFlow()

    fun setAmount(v: String) = _state.update { it.copy(amount = v, error = null) }
    fun setSource(id: Long) = _state.update { it.copy(selectedSourceId = id, error = null) }
    fun setDate(d: LocalDate) = _state.update { it.copy(date = d) }
    fun setRecurrence(r: Recurrence) = _state.update { it.copy(recurrence = r) }
    fun setNote(v: String) = _state.update { it.copy(note = v) }
    fun setNewSourceName(v: String) = _state.update { it.copy(newSourceName = v, error = null) }

    fun createSourceAndSelect() {
        viewModelScope.launch {
            val name = state.value.newSourceName.trim()
            if (name.isEmpty()) {
                _state.update { it.copy(error = "Source name is empty") }
                return@launch
            }
            val id = repo.addSourceIfNeeded(name)
            _state.update { it.copy(selectedSourceId = id, newSourceName = "") }
        }
    }

    fun loadForEdit(entryId: Long) {
        viewModelScope.launch {
            val entry = dao.getEntryById(entryId)
            if (entry == null) {
                _state.update { it.copy(error = "Entry not found") }
                return@launch
            }
            _state.update {
                it.copy(
                    editingEntryId = entry.id,
                    amount = "%.2f".format(entry.amountCents / 100.0),
                    selectedSourceId = entry.sourceId,
                    date = entry.date,
                    note = entry.note.orEmpty(),
                    recurrence = Recurrence.NONE,
                    error = null
                )
            }
        }
    }

    fun save(onDone: () -> Unit) {
        viewModelScope.launch {
            val st = state.value
            val cents = parseAmountToCents(st.amount)
            if (cents == null || cents <= 0) {
                _state.update { it.copy(error = "Enter a valid amount") }
                return@launch
            }
            val sourceId = st.selectedSourceId
            if (sourceId == null) {
                _state.update { it.copy(error = "Select an income source") }
                return@launch
            }

            _state.update { it.copy(saving = true, error = null) }
            try {
                val note = st.note.trim().ifEmpty { null }
                val editingId = st.editingEntryId

                if (editingId == null) {
                    if (st.recurrence == Recurrence.NONE) {
                        repo.addOnceOffIncome(cents, sourceId, st.date, note)
                    } else {
                        repo.addRecurringIncome(cents, sourceId, st.date, st.recurrence, note)
                    }
                } else {
                    val existing = dao.getEntryById(editingId)
                    if (existing == null) {
                        _state.update { it.copy(error = "Entry not found") }
                        return@launch
                    }
                    dao.updateEntry(
                        existing.copy(
                            amountCents = cents,
                            sourceId = sourceId,
                            date = st.date,
                            note = note
                        )
                    )
                }

                onDone()
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message ?: "Failed to save") }
            } finally {
                _state.update { it.copy(saving = false) }
            }
        }
    }
}
