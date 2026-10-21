package com.example.spendwise.data.local

import androidx.room.TypeConverter
import com.example.spendwise.domain.model.TransactionType

class TransactionTypeConverter {

    @TypeConverter
    fun fromTransactionType(type: TransactionType): String {
        return type.name
    }

    @TypeConverter
    fun toTransactionType(value: String): TransactionType {
        return TransactionType.valueOf(value)
    }
}