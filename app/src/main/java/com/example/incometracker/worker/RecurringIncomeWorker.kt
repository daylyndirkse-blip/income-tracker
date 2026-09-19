package com.example.incometracker.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.incometracker.data.DatabaseProvider
import com.example.incometracker.data.IncomeEntryEntity
import com.example.incometracker.data.Recurrence
import java.time.LocalDate

class RecurringIncomeWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val db = DatabaseProvider.get(applicationContext)
        val dao = db.incomeDao()
        val today = LocalDate.now()

        val rules = dao.getActiveRules()
        for (rule in rules) {
            val last = rule.lastGeneratedDate
            var next: LocalDate? = if (last == null) rule.anchorDate else when (rule.recurrence) {
                Recurrence.DAILY -> last.plusDays(1)
                Recurrence.WEEKLY -> last.plusWeeks(1)
                Recurrence.MONTHLY -> last.plusMonths(1)
                Recurrence.NONE -> null
            }

            if (next == null) continue

            val newEntries = mutableListOf<IncomeEntryEntity>()
            var newestGenerated: LocalDate? = null

            while (!next.isAfter(today)) {
                newEntries += IncomeEntryEntity(
                    sourceId = rule.sourceId,
                    amountCents = rule.amountCents,
                    date = next,
                    note = rule.note,
                    ruleId = rule.id
                )
                newestGenerated = next

                next = when (rule.recurrence) {
                    Recurrence.DAILY -> next.plusDays(1)
                    Recurrence.WEEKLY -> next.plusWeeks(1)
                    Recurrence.MONTHLY -> next.plusMonths(1)
                    Recurrence.NONE -> break
                }
            }

            if (newEntries.isNotEmpty() && newestGenerated != null) {
                dao.insertEntries(newEntries)
                dao.updateRule(rule.copy(lastGeneratedDate = newestGenerated))
            }
        }

        return Result.success()
    }
}
