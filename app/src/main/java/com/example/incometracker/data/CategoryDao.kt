package com.example.incometracker.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories WHERE type = :type ORDER BY isDefault DESC, name ASC")
    fun getCategoriesByType(type: TransactionType): Flow<List<Category>>
    
    @Query("SELECT * FROM categories ORDER BY type, isDefault DESC, name ASC")
    fun getAllCategories(): Flow<List<Category>>
    
    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategoryById(id: Long): Category?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category): Long
    
    @Update
    suspend fun updateCategory(category: Category)
    
    @Delete
    suspend fun deleteCategory(category: Category)
    
    @Query("DELETE FROM categories WHERE id = :id AND isDefault = 0")
    suspend fun deleteCategoryById(id: Long)
}
