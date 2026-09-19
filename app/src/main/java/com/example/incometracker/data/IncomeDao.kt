package com.example.incometracker.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface IncomeDao {

    // Sources
    @Query("SELECT * FROM income_sources ORDER BY name ASC")
    fun observeSources(): Flow<List<IncomeSourceEntity>>

    @Query("SELECT * FROM income_sources WHERE name = :name LIMIT 1")
    suspend fun getSourceByName(name: String): IncomeSourceEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSource(source: IncomeSourceEntity): Long

    // Rules
    @Query("SELECT * FROM income_rules WHERE active = 1")
    suspend fun getActiveRules(): List<IncomeRuleEntity>

    @Insert
    suspend fun insertRule(rule: IncomeRuleEntity): Long

    @Update
    suspend fun updateRule(rule: IncomeRuleEntity)

    // Entries
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEntry(entry: IncomeEntryEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEntries(entries: List<IncomeEntryEntity>): List<Long>

    @Query("SELECT COALESCE(SUM(amountCents), 0) FROM income_entries WHERE date BETWEEN :start AND :end")
    fun observeTotalBetween(start: LocalDate, end: LocalDate): Flow<Long>

    @Query("""
        SELECT date AS date, COALESCE(SUM(amountCents), 0) AS totalCents
        FROM income_entries
        WHERE date BETWEEN :start AND :end
        GROUP BY date
        ORDER BY date ASC
    """)
    fun observeDailyTotalsBetween(start: LocalDate, end: LocalDate): Flow<List<DailyTotal>>

    @Query("""
        SELECT
            e.id AS id,
            s.name AS sourceName,
            e.amountCents AS amountCents,
            e.date AS date,
            e.note AS note,
            e.createdAtEpochMillis AS createdAtEpochMillis
        FROM income_entries e
        JOIN income_sources s ON s.id = e.sourceId
        WHERE e.date = :date
        ORDER BY e.createdAtEpochMillis DESC
    """)
    fun observeEntriesWithSourceForDate(date: LocalDate): Flow<List<IncomeEntryWithSource>>

    @Query("""
        SELECT s.name AS sourceName, COALESCE(SUM(e.amountCents), 0) AS totalCents
        FROM income_entries e
        JOIN income_sources s ON s.id = e.sourceId
        WHERE e.date BETWEEN :start AND :end
        GROUP BY e.sourceId
        ORDER BY totalCents DESC
    """)
    fun observeTotalsBySourceBetween(start: LocalDate, end: LocalDate): Flow<List<SourceTotal>>

    @Query("SELECT * FROM income_entries WHERE id = :id LIMIT 1")
    suspend fun getEntryById(id: Long): IncomeEntryEntity?

    @Update
    suspend fun updateEntry(entry: IncomeEntryEntity)

    @Query("DELETE FROM income_entries WHERE id = :id")
    suspend fun deleteEntryById(id: Long)
}
