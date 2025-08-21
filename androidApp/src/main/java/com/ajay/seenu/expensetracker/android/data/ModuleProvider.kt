package com.ajay.seenu.expensetracker.android.data

import android.content.Context
import com.ajay.seenu.expensetracker.AttachmentDataSourceImpl
import com.ajay.seenu.expensetracker.AttachmentDateSource
import com.ajay.seenu.expensetracker.BudgetRepository
import com.ajay.seenu.expensetracker.CategoryDataSource
import com.ajay.seenu.expensetracker.CategoryDataSourceImpl
import com.ajay.seenu.expensetracker.DriverFactory
import com.ajay.seenu.expensetracker.ExpenseDatabase
import com.ajay.seenu.expensetracker.ExportDataSource
import com.ajay.seenu.expensetracker.ExportDataSourceImpl
import com.ajay.seenu.expensetracker.TransactionDataSource
import com.ajay.seenu.expensetracker.TransactionDataSourceImpl
import com.ajay.seenu.expensetracker.UserConfigurationsManager
import com.ajay.seenu.expensetracker.createDatabase
import com.ajay.seenu.expensetracker.entity.AndroidFileManager
import com.ajay.seenu.expensetracker.entity.FileManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ModuleProvider {

    @Provides
    @Singleton
    fun provideDataSource(database: ExpenseDatabase): TransactionDataSource {
        return TransactionDataSourceImpl(database)
    }

    @Provides
    @Singleton
    fun provideAttachmentSource(database: ExpenseDatabase): AttachmentDateSource {
        return AttachmentDataSourceImpl(database)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ExpenseDatabase {
        return createDatabase(DriverFactory(context))
    }

    @Singleton
    @Provides
    fun provideUserConfigsManager(@ApplicationContext context: Context): UserConfigurationsManager {
        return UserConfigurationsManager(context)
    }

    @Singleton
    @Provides
    fun provideCategorySource(database: ExpenseDatabase): CategoryDataSource {
        return CategoryDataSourceImpl(database)
    }

    @Singleton
    @Provides
    fun provideExportSource(database: ExpenseDatabase): ExportDataSource {
        return ExportDataSourceImpl(database)
    }

    @Singleton
    @Provides
    fun provideFileManager(@ApplicationContext context: Context): FileManager {
        return AndroidFileManager(context)
    }

    @Provides
    @Singleton
    fun provideBudgetRepository(database: ExpenseDatabase): BudgetRepository {
        return BudgetRepository(database)
    }

}