package com.example.spendwise.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [TransactionEntity::class, ExchangeRateEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(TransactionTypeConverter::class)
abstract class SpendWiseDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
    abstract fun exchangeRateDao(): ExchangeRateDao
}
