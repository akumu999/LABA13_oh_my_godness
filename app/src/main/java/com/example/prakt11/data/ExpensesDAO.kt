package com.example.prakt11.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.prakt11.data.models.Expenses
import com.example.prakt11.data.models.TypeExpenses
import java.util.*

@Dao
interface ExpensesDAO {
    @Query("SELECT * FROM $TYPE_TABLE ")
    fun getAllTypesExpenses(): LiveData<MutableList<TypeExpenses>>
    @Query("SELECT typeExpenses FROM $TYPE_TABLE WHERE uuid =:id")
    fun getTypeExpensesName(id:UUID): String
    @Update
    fun updateTypeExpenses (type: TypeExpenses)
    @Insert
    fun addTypeExpenses (type: TypeExpenses)
    @Delete
    fun deleteType (type: TypeExpenses)

    @Query("SELECT * FROM $EXPENSES_TABLE")
    fun getAllExpenses(): LiveData<MutableList<Expenses>>
    @Query("SELECT * FROM $EXPENSES_TABLE WHERE uuid=:id")
    fun getExpenses(id: UUID): Expenses
    @Insert
    fun addExpenses(expenses: Expenses)
    @Update
    fun updateExpenses(expenses: Expenses)
    @Delete
    fun deleteExpenses(expenses: Expenses)
}