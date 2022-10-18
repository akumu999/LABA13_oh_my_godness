package com.example.prakt11.data.models

import android.provider.BaseColumns
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.prakt11.data.EXPENSES_TABLE
import com.example.prakt11.data.TYPE_TABLE
import java.util.*

@Entity(tableName = TYPE_TABLE)
data class TypeExpenses(
    @PrimaryKey(autoGenerate = false)
    var uuid: UUID,
    var typeExpenses : String
)