package com.example.incometracker.data

import java.time.LocalDate

class IncomeRepository(private val dao: IncomeDao) {

    fun observeSources() = dao.observeSources()

    suspend fun addSourceIfNeeded(nameRaw: String): Long {
        val name = nameRaw.trim()
        require(name.isNotEmpty())
        val existing = dao.getSourceByName(name)
        if (existing != null) return existing.id

        val id = dao.insertSource(IncomeSourceEntity(name = name))
        return if (id != -1L) id else dao.getSourceByName(name)!!.id
    }

    suspend fun addOnceOffIncome(amountCents: Long, sourceId: Long, date: LocalDate, note: String?) {
        dao.insertEntry(
            IncomeEntryEntity(
                sourceId = sourceId,
                amountCents = amountCents,
                date = date,
                note = note,
                ruleId = null
            )
        )
    }

    suspend fun addRecurringIncome(
        amountCents: Long,
        sourceId: Long,
        anchorDate: LocalDate,
        recurrence: Recurrence,
        note: String?
    ) {
        require(recurrence != Recurrence.NONE)

        val ruleId = dao.insertRule(
            IncomeRuleEntity(
                sourceId = sourceId,
                amountCents = amountCents,
                recurrence = recurrence,
                anchorDate = anchorDate,
                note = note,
                active = true,
                lastGeneratedDate = null
            )
        )

        val today = LocalDate.now()
        val dates = generateDates(anchorDate, today, recurrence)
        val entries = dates.map { d ->
            IncomeEntryEntity(
                sourceId = sourceId,
                amountCents = amountCents,
                date = d,
                note = note,
                ruleId = ruleId
            )
        }
        dao.insertEntries(entries)

        val last = dates.lastOrNull()
        if (last != null) {
            dao.updateRule(
                IncomeRuleEntity(
                    id = ruleId,
                    sourceId = sourceId,
                    amountCents = amountCents,
                    recurrence = recurrence,
                    anchorDate = anchorDate,
                    note = note,
                    active = true,
                    lastGeneratedDate = last
                )
            )
        }
    }

    private fun generateDates(start: LocalDate, endInclusive: LocalDate, recurrence: Recurrence): List<LocalDate> {
        if (start.isAfter(endInclusive)) return emptyList()
        val out = mutableListOf<LocalDate>()
        var d = start
        while (!d.isAfter(endInclusive)) {
            out += d
            d = when (recurrence) {
                Recurrence.DAILY -> d.plusDays(1)
                Recurrence.WEEKLY -> d.plusWeeks(1)
                Recurrence.MONTHLY -> d.plusMonths(1)
                Recurrence.NONE -> break
            }
        }
        return out
    }
}
