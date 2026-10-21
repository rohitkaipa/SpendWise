package com.example.spendwise.data.repository

import com.example.spendwise.data.local.TransactionDao
import com.example.spendwise.data.local.TransactionEntity
import kotlinx.coroutines.flow.Flow

class TransactionRepository(
    private val transactionDao: TransactionDao
) {

    fun getAllTransactions(): Flow<List<TransactionEntity>> {
        return transactionDao.getAllTransactions()
    }

    suspend fun insertTransaction(transaction: TransactionEntity) {
        transactionDao.insertTransaction(transaction)
    }

    suspend fun deleteTransaction(transactionId: Long) {
        transactionDao.deleteTransaction(transactionId)
    }
}