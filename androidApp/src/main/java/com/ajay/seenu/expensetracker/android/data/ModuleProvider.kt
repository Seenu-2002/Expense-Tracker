package com.ajay.seenu.expensetracker.android.data

import android.content.Context
import com.ajay.seenu.expensetracker.AttachmentDataSourceImpl
import com.ajay.seenu.expensetracker.AttachmentDateSource
import com.ajay.seenu.expensetracker.DriverFactory
import com.ajay.seenu.expensetracker.ExpenseDatabase
import com.ajay.seenu.expensetracker.TransactionDataSource
import com.ajay.seenu.expensetracker.TransactionDataSourceImpl
import com.ajay.seenu.expensetracker.UserConfigurationsManager
import com.ajay.seenu.expensetracker.createDatabase
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

}