package com.ajay.seenu.expensetracker.android.di

import android.content.Context
import com.ajay.seenu.expensetracker.UserConfigurationsManager
import com.ajay.seenu.expensetracker.domain.AndroidFileManager
import com.ajay.seenu.expensetracker.domain.FileManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideUserConfigsManager(@ApplicationContext context: Context): UserConfigurationsManager {
        return UserConfigurationsManager(context)
    }

    @Singleton
    @Provides
    fun provideFileManager(@ApplicationContext context: Context): FileManager {
        return AndroidFileManager(context)
    }

}