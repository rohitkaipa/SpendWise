package com.example.spendwise.di

import android.content.Context
import androidx.room.Room
import com.example.spendwise.data.local.ExchangeRateDao
import com.example.spendwise.data.local.SpendWiseDatabase
import com.example.spendwise.data.local.TransactionDao
import com.example.spendwise.data.repository.CurrencyRepository
import com.example.spendwise.data.repository.TransactionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): SpendWiseDatabase {
        return Room.databaseBuilder(
            context,
            SpendWiseDatabase::class.java,
            "spendwise_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideTransactionDao(
        database: SpendWiseDatabase
    ): TransactionDao {
        return database.transactionDao()
    }

    @Provides
    fun provideExchangeRateDao(
        database: SpendWiseDatabase
    ): ExchangeRateDao {
        return database.exchangeRateDao()
    }

    @Provides
    @Singleton
    fun provideTransactionRepository(
        dao: TransactionDao
    ): TransactionRepository {
        return TransactionRepository(dao)
    }
}
