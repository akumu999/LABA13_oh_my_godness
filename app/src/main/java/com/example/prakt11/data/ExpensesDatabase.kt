package com.example.prakt11.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.prakt11.data.models.Expenses
import com.example.prakt11.data.models.TypeExpenses

@Database(entities = [TypeExpenses::class, Expenses::class], version = 1)
abstract class ExpensesDatabase : RoomDatabase() {
    abstract fun expensesDAO(): ExpensesDAO
}