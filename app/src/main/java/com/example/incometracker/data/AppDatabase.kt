package com.example.incometracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(
    entities = [IncomeRecord::class, Category::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun incomeDao(): IncomeDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "income_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch {
                        populateDefaultCategories(database.categoryDao())
                    }
                }
            }
        }

        private suspend fun populateDefaultCategories(categoryDao: CategoryDao) {
            // Default Income Categories
            val incomeCategories = listOf(
                Category(name = "Salary", icon = "Work", color = "#4CAF50", type = TransactionType.INCOME, isDefault = true),
                Category(name = "Freelance", icon = "Computer", color = "#2196F3", type = TransactionType.INCOME, isDefault = true),
                Category(name = "Investment", icon = "TrendingUp", color = "#9C27B0", type = TransactionType.INCOME, isDefault = true),
                Category(name = "Business", icon = "Business", color = "#FF9800", type = TransactionType.INCOME, isDefault = true),
                Category(name = "Gift", icon = "CardGiftcard", color = "#E91E63", type = TransactionType.INCOME, isDefault = true),
                Category(name = "Other Income", icon = "Add", color = "#607D8B", type = TransactionType.INCOME, isDefault = true)
            )

            // Default Expense Categories
            val expenseCategories = listOf(
                Category(name = "Food", icon = "Restaurant", color = "#FF5722", type = TransactionType.EXPENSE, isDefault = true),
                Category(name = "Transport", icon = "DirectionsCar", color = "#3F51B5", type = TransactionType.EXPENSE, isDefault = true),
                Category(name = "Shopping", icon = "ShoppingCart", color = "#E91E63", type = TransactionType.EXPENSE, isDefault = true),
                Category(name = "Bills", icon = "Receipt", color = "#F44336", type = TransactionType.EXPENSE, isDefault = true),
                Category(name = "Entertainment", icon = "Movie", color = "#9C27B0", type = TransactionType.EXPENSE, isDefault = true),
                Category(name = "Health", icon = "LocalHospital", color = "#009688", type = TransactionType.EXPENSE, isDefault = true),
                Category(name = "Education", icon = "School", color = "#FF9800", type = TransactionType.EXPENSE, isDefault = true),
                Category(name = "Other Expense", icon = "Remove", color = "#607D8B", type = TransactionType.EXPENSE, isDefault = true)
            )

            incomeCategories.forEach { categoryDao.insertCategory(it) }
            expenseCategories.forEach { categoryDao.insertCategory(it) }
        }
    }
}
