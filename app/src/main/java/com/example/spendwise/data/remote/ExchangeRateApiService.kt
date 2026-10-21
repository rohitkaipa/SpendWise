package com.example.spendwise.data.remote

import retrofit2.http.GET

interface ExchangeRateApiService {
    @GET("v6/latest/USD")
    suspend fun getLatestRates(): ExchangeRateResponse
}

data class ExchangeRateResponse(
    val result: String?,
    val base_code: String?,
    val rates: Map<String, Double>?
)
