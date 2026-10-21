package com.example.spendwise.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendwise.domain.model.Transaction
import com.example.spendwise.domain.model.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class SpendWiseViewModel : ViewModel() {

    private val _transactions =
        MutableStateFlow<List<Transaction>>(emptyList())

    val transactions: StateFlow<List<Transaction>> =
        _transactions.asStateFlow()

    val totalIncome: StateFlow<Double> =
        transactions
            .map { list ->
                list.filter { it.type == TransactionType.INCOME }
                    .sumOf { it.amount }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = 0.0
            )

    val totalExpenses: StateFlow<Double> =
        transactions
            .map { list ->
                list.filter { it.type == TransactionType.EXPENSE }
                    .sumOf { it.amount }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = 0.0
            )

    val balance: StateFlow<Double> =
        transactions
            .map { list ->
                list.sumOf {
                    when (it.type) {
                        TransactionType.INCOME -> it.amount
                        TransactionType.EXPENSE -> -it.amount
                    }
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = 0.0
            )

    fun addTransaction(
        title: String,
        category: String,
        amount: Double,
        currencyCode: String,
        type: TransactionType
    ) {
        val newTransaction = Transaction(
            id = System.currentTimeMillis(),
            title = title,
            category = category,
            amount = amount,
            currencyCode = currencyCode,
            type = type
        )

        _transactions.value = listOf(newTransaction) + _transactions.value
    }
}