package com.example.prakt11.data.models

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.prakt11.data.EXPENSES_TABLE
import java.util.*

@Entity(tableName = EXPENSES_TABLE)
data class Expenses (
    @PrimaryKey(autoGenerate = false)
    @NonNull
    var uuid: UUID,
    var nameExpenses : String,
    var costExpenses : Int,
    @ColumnInfo(index = true)
    var typeExpenses : UUID
)