package com.example.spendwise.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.spendwise.domain.model.TransactionType

@Entity(tableName = "transactions")
data class TransactionEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val title: String,

    val category: String,

    val amount: Double,

    val currencyCode: String,

    val type: TransactionType,

    val timestamp: Long
)