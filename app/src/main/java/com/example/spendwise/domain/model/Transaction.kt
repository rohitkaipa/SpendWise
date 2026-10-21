package com.example.spendwise.domain.model

enum class TransactionType {
    INCOME,
    EXPENSE
}

data class Transaction(
    val id: Long,
    val title: String,
    val category: String,
    val amount: Double,
    val currencyCode: String,
    val type: TransactionType,
    val timestamp: Long = System.currentTimeMillis()
)