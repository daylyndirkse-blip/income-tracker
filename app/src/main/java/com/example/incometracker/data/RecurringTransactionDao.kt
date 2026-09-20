package com.example.incometracker.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RecurringTransactionDao {
    @Query("SELECT * FROM recurring_transactions WHERE isActive = 1 ORDER BY source ASC")
    fun getActiveRecurring(): Flow<List<RecurringTransaction>>
    
    @Query("SELECT * FROM recurring_transactions ORDER BY isActive DESC, source ASC")
    fun getAllRecurring(): Flow<List<RecurringTransaction>>
    
    @Query("SELECT * FROM recurring_transactions WHERE id = :id")
    suspend fun getById(id: Long): RecurringTransaction?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: RecurringTransaction): Long
    
    @Update
    suspend fun update(transaction: RecurringTransaction)
    
    @Delete
    suspend fun delete(transaction: RecurringTransaction)
    
    @Query("UPDATE recurring_transactions SET lastProcessed = :epochDay WHERE id = :id")
    suspend fun updateLastProcessed(id: Long, epochDay: Long)
}
