package com.ajay.seenu.expensetracker.android.di

import android.content.Context
import com.ajay.seenu.expensetracker.DriverFactory
import com.ajay.seenu.expensetracker.ExpenseDatabase
import com.ajay.seenu.expensetracker.createDatabase
import com.ajay.seenu.expensetracker.data.data_source.AccountDataSource
import com.ajay.seenu.expensetracker.data.data_source.AttachmentDateSource
import com.ajay.seenu.expensetracker.data.data_source.CategoryDataSource
import com.ajay.seenu.expensetracker.data.data_source.ExportDataSource
import com.ajay.seenu.expensetracker.data.data_source.TransactionDataSource
import com.ajay.seenu.expensetracker.data.data_source.local.AccountLocalDataSource
import com.ajay.seenu.expensetracker.data.data_source.local.AttachmentLocalDataSource
import com.ajay.seenu.expensetracker.data.data_source.local.CategoryLocalDataSource
import com.ajay.seenu.expensetracker.data.data_source.local.ExportLocalDataSource
import com.ajay.seenu.expensetracker.data.data_source.local.TransactionLocalDataSource
import com.ajay.seenu.expensetracker.data.repository.AccountRepository
import com.ajay.seenu.expensetracker.data.repository.AttachmentRepository
import com.ajay.seenu.expensetracker.data.repository.CategoryRepository
import com.ajay.seenu.expensetracker.data.repository.TransactionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideTransactionLocalDataSource(database: ExpenseDatabase): TransactionDataSource {
        return TransactionLocalDataSource(database)
    }

    @Singleton
    @Provides
    fun provideTransactionRepository(
        dataSource: TransactionDataSource,
        categoryRepository: CategoryRepository,
        accountRepository: AccountRepository
    ): TransactionRepository {
        return TransactionRepository(dataSource, categoryRepository, accountRepository)
    }

    @Provides
    @Singleton
    fun provideAttachmentLocalDataSource(database: ExpenseDatabase): AttachmentDateSource {
        return AttachmentLocalDataSource(database)
    }

    @Singleton
    @Provides
    fun provideAttachmentRepository(
        dataSource: AttachmentDateSource
    ): AttachmentRepository {
        return AttachmentRepository(dataSource)
    }

    @Singleton
    @Provides
    fun provideCategoryLocalDataSource(database: ExpenseDatabase): CategoryDataSource {
        return CategoryLocalDataSource(database)
    }

    @Singleton
    @Provides
    fun provideCategoryRepository(dataSource: CategoryDataSource): CategoryRepository {
        return CategoryRepository(dataSource)
    }

    @Singleton
    @Provides
    fun provideAccountLocalDataSource(database: ExpenseDatabase): AccountDataSource {
        return AccountLocalDataSource(database)
    }

    @Singleton
    @Provides
    fun provideExportLocalDataSource(database: ExpenseDatabase, transactionRepository: TransactionRepository): ExportDataSource {
        return ExportLocalDataSource(transactionRepository, database)
    }

    @Singleton
    @Provides
    fun provideAccountRepository(dataSource: AccountDataSource): AccountRepository {
        return AccountRepository(dataSource)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ExpenseDatabase {
        return createDatabase(DriverFactory(context))
    }

}