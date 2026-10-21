package com.example.spendwise.data.repository

import com.example.spendwise.data.local.ExchangeRateDao
import com.example.spendwise.data.local.ExchangeRateEntity
import com.example.spendwise.data.remote.ExchangeRateApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrencyRepository @Inject constructor(
    private val apiService: ExchangeRateApiService,
    private val exchangeRateDao: ExchangeRateDao
) {

    suspend fun getExchangeRates(): Map<String, Double> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getLatestRates()
                val rates = response.rates
                if (!rates.isNullOrEmpty()) {
                    val entities = rates.map { (code, rate) ->
                        ExchangeRateEntity(
                            currencyCode = code,
                            rateAgainstUsd = rate,
                            timestamp = System.currentTimeMillis()
                        )
                    }
                    exchangeRateDao.insertRates(entities)
                    rates
                } else {
                    getCachedRates()
                }
            } catch (e: Exception) {
                getCachedRates()
            }
        }
    }

    private suspend fun getCachedRates(): Map<String, Double> {
        val cached = exchangeRateDao.getAllRates()
        if (cached.isNotEmpty()) {
            return cached.associate { it.currencyCode to it.rateAgainstUsd }
        }
        return mapOf(
            "USD" to 1.0,
            "INR" to 86.0,
            "EUR" to 0.95,
            "GBP" to 0.80,
            "JPY" to 150.0
        )
    }

    suspend fun convert(amount: Double, fromCurrency: String, toCurrency: String): Double {
        if (fromCurrency == toCurrency) return amount
        val rates = getExchangeRates()
        val fromRate = rates[fromCurrency] ?: 1.0
        val toRate = rates[toCurrency] ?: 1.0

        val amountInUsd = amount / fromRate
        return amountInUsd * toRate
    }
}
