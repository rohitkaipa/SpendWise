package com.example.spendwise.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendwise.data.local.TransactionEntity
import com.example.spendwise.data.repository.CurrencyRepository
import com.example.spendwise.data.repository.TransactionRepository
import com.example.spendwise.domain.model.Transaction
import com.example.spendwise.domain.model.TransactionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TransactionDisplayItem(
    val id: Long,
    val title: String,
    val category: String,
    val originalAmount: Double,
    val originalCurrency: String,
    val convertedAmount: Double,
    val displayCurrency: String,
    val type: TransactionType,
    val timestamp: Long
)

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val repository: TransactionRepository,
    private val currencyRepository: CurrencyRepository
) : ViewModel() {

    private val _displayCurrency = MutableStateFlow("INR")
    val displayCurrency: StateFlow<String> = _displayCurrency.asStateFlow()

    val transactions: StateFlow<List<Transaction>> = repository.getAllTransactions()
        .map { entities ->
            entities.map { entity ->
                Transaction(
                    id = entity.id,
                    title = entity.title,
                    category = entity.category,
                    amount = entity.amount,
                    currencyCode = entity.currencyCode,
                    type = entity.type,
                    timestamp = entity.timestamp
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    private val _convertedTransactions: StateFlow<List<Transaction>> = combine(
        repository.getAllTransactions().map { entities ->
            entities.map { entity ->
                Transaction(
                    id = entity.id,
                    title = entity.title,
                    category = entity.category,
                    amount = entity.amount,
                    currencyCode = entity.currencyCode,
                    type = entity.type,
                    timestamp = entity.timestamp
                )
            }
        },
        _displayCurrency
    ) { list, targetCurrency ->
        list.map { tx ->
            val convertedAmount = try {
                currencyRepository.convert(tx.amount, tx.currencyCode, targetCurrency)
            } catch (e: Exception) {
                tx.amount
            }
            tx.copy(amount = convertedAmount, currencyCode = targetCurrency)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val transactionDisplayItems: StateFlow<List<TransactionDisplayItem>> = combine(
        repository.getAllTransactions().map { entities ->
            entities.map { entity ->
                Transaction(
                    id = entity.id,
                    title = entity.title,
                    category = entity.category,
                    amount = entity.amount,
                    currencyCode = entity.currencyCode,
                    type = entity.type,
                    timestamp = entity.timestamp
                )
            }
        },
        _displayCurrency
    ) { list, targetCurrency ->
        list.map { tx ->
            val convertedAmount = try {
                currencyRepository.convert(tx.amount, tx.currencyCode, targetCurrency)
            } catch (e: Exception) {
                tx.amount
            }
            TransactionDisplayItem(
                id = tx.id,
                title = tx.title,
                category = tx.category,
                originalAmount = tx.amount,
                originalCurrency = tx.currencyCode,
                convertedAmount = convertedAmount,
                displayCurrency = targetCurrency,
                type = tx.type,
                timestamp = tx.timestamp
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val totalIncome: StateFlow<Double> = _convertedTransactions
        .map { list ->
            list.filter { it.type == TransactionType.INCOME }
                .sumOf { it.amount }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = 0.0
        )

    val totalExpenses: StateFlow<Double> = _convertedTransactions
        .map { list ->
            list.filter { it.type == TransactionType.EXPENSE }
                .sumOf { it.amount }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = 0.0
        )

    val balance: StateFlow<Double> = _convertedTransactions
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

    fun setDisplayCurrency(currencyCode: String) {
        _displayCurrency.value = currencyCode
    }

    fun addTransaction(
        title: String,
        category: String,
        amount: Double,
        currencyCode: String,
        type: TransactionType
    ) {
        viewModelScope.launch {
            val entity = TransactionEntity(
                title = title,
                category = category,
                amount = amount,
                currencyCode = currencyCode,
                type = type,
                timestamp = System.currentTimeMillis()
            )
            repository.insertTransaction(entity)
        }
    }

    fun deleteTransaction(transactionId: Long) {
        viewModelScope.launch {
            repository.deleteTransaction(transactionId)
        }
    }
}
