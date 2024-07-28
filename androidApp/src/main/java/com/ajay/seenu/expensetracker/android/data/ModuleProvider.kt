package com.ajay.seenu.expensetracker.android.data

import android.content.Context
import com.ajay.seenu.expensetracker.DriverFactory
import com.ajay.seenu.expensetracker.TransactionDataSource
import com.ajay.seenu.expensetracker.TransactionDataSourceImpl
import com.ajay.seenu.expensetracker.UserConfigurationsManager
import com.ajay.seenu.expensetracker.createDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ModuleProvider {

    @Provides
    @Singleton
    fun provideDataSource(@ApplicationContext context: Context): TransactionDataSource {
        return TransactionDataSourceImpl(createDatabase(DriverFactory(context)))
    }

    @Provides
    @Singleton
    fun provideDateFormatter(@ApplicationContext context: Context, locale: Locale): SimpleDateFormat {
        val configs = UserConfigurationsManager(context).getConfigs()
        return SimpleDateFormat(configs.dateFormat, locale)
    }

    @Provides
    @Singleton
    fun provideLocale(): Locale {
        return Locale.ENGLISH
    }

}