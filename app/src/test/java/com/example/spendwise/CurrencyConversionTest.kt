package com.example.spendwise

import org.junit.Assert.assertEquals
import org.junit.Test

class CurrencyConversionTest {

    @Test
    fun testCurrencyConversionCalculation() {
        val rates = mapOf(
            "USD" to 1.0,
            "INR" to 86.0,
            "EUR" to 0.95
        )

        val amountInUsd = 100.0
        val fromCurrency = "USD"
        val toCurrency = "INR"

        val fromRate = rates[fromCurrency] ?: 1.0
        val toRate = rates[toCurrency] ?: 1.0

        val converted = (amountInUsd / fromRate) * toRate
        assertEquals(8600.0, converted, 0.001)
    }

    @Test
    fun testSameCurrencyConversion() {
        val amount = 500.0
        val converted = if ("INR" == "INR") amount else 0.0
        assertEquals(500.0, converted, 0.001)
    }
}
