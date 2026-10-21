package com.example.spendwise.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ExchangeRateDao {

    @Query("SELECT * FROM exchange_rates")
    suspend fun getAllRates(): List<ExchangeRateEntity>

    @Query("SELECT * FROM exchange_rates WHERE currencyCode = :code")
    suspend fun getRate(code: String): ExchangeRateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRates(rates: List<ExchangeRateEntity>)
}
