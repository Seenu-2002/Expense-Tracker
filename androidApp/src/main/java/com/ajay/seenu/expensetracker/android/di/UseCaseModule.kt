package com.ajay.seenu.expensetracker.android.di

import com.ajay.seenu.expensetracker.UserConfigurationsManager
import com.ajay.seenu.expensetracker.data.repository.AttachmentRepository
import com.ajay.seenu.expensetracker.data.repository.CategoryRepository
import com.ajay.seenu.expensetracker.data.repository.TransactionRepository
import com.ajay.seenu.expensetracker.domain.usecase.DateRangeCalculatorUseCase
import com.ajay.seenu.expensetracker.domain.usecase.attachment.AddAttachmentUseCase
import com.ajay.seenu.expensetracker.domain.usecase.attachment.GetAttachmentsUseCase
import com.ajay.seenu.expensetracker.domain.usecase.category.AddCategoryUseCase
import com.ajay.seenu.expensetracker.domain.usecase.category.ChangeCategoriesUseCase
import com.ajay.seenu.expensetracker.domain.usecase.category.DeleteCategoryUseCase
import com.ajay.seenu.expensetracker.domain.usecase.category.GetAllCategoriesAsFlowUseCase
import com.ajay.seenu.expensetracker.domain.usecase.category.GetAllCategoriesUseCase
import com.ajay.seenu.expensetracker.domain.usecase.category.GetCategoryUseCase
import com.ajay.seenu.expensetracker.domain.usecase.category.UpdateCategoryUseCase
import com.ajay.seenu.expensetracker.domain.usecase.data_filter.GetExpenseByCategoryUseCase
import com.ajay.seenu.expensetracker.domain.usecase.data_filter.GetFilteredOverallDataUseCase
import com.ajay.seenu.expensetracker.domain.usecase.data_filter.GetFilteredTransactionsUseCase
import com.ajay.seenu.expensetracker.domain.usecase.data_filter.GetRecentTransactionsUseCase
import com.ajay.seenu.expensetracker.domain.usecase.data_filter.GetTotalTransactionPerDayByCategoryUseCase
import com.ajay.seenu.expensetracker.domain.usecase.transaction.AddTransactionUseCase
import com.ajay.seenu.expensetracker.domain.usecase.transaction.DeleteTransactionUseCase
import com.ajay.seenu.expensetracker.domain.usecase.transaction.GetTransactionCountByCategoryUseCase
import com.ajay.seenu.expensetracker.domain.usecase.transaction.GetTransactionUseCase
import com.ajay.seenu.expensetracker.domain.usecase.transaction.UpdateTransactionUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Singleton
    @Provides
    fun provideAddAttachmentUseCase(repository: AttachmentRepository): AddAttachmentUseCase {
        return AddAttachmentUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideGetAttachmentsUseCase(repository: AttachmentRepository): GetAttachmentsUseCase {
        return GetAttachmentsUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideAddCategoryUseCase(repository: CategoryRepository): AddCategoryUseCase {
        return AddCategoryUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideChangeCategoriesUseCase(repository: TransactionRepository): ChangeCategoriesUseCase {
        return ChangeCategoriesUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideDeleteCategoryUseCase(repository: CategoryRepository): DeleteCategoryUseCase {
        return DeleteCategoryUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideGetAllCategoriesAsFlowUseCase(repository: CategoryRepository): GetAllCategoriesAsFlowUseCase {
        return GetAllCategoriesAsFlowUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideGetAllCategoriesUseCase(repository: CategoryRepository): GetAllCategoriesUseCase {
        return GetAllCategoriesUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideGetCategoryUseCase(repository: CategoryRepository): GetCategoryUseCase {
        return GetCategoryUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideUpdateCategoryUseCase(repository: CategoryRepository): UpdateCategoryUseCase {
        return UpdateCategoryUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideAddTransactionUseCase(repository: TransactionRepository): AddTransactionUseCase {
        return AddTransactionUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideDeleteTransactionUseCase(repository: TransactionRepository): DeleteTransactionUseCase {
        return DeleteTransactionUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideGetTransactionUseCase(repository: TransactionRepository): GetTransactionUseCase {
        return GetTransactionUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideUpdateTransactionUseCase(repository: TransactionRepository): UpdateTransactionUseCase {
        return UpdateTransactionUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideGetTransactionCountByCategoryUseCase(repository: TransactionRepository): GetTransactionCountByCategoryUseCase {
        return GetTransactionCountByCategoryUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideGetExpenseByCategoryUseCase(
        categoryRepository: CategoryRepository,
        transactionRepository: TransactionRepository
    ): GetExpenseByCategoryUseCase {
        return GetExpenseByCategoryUseCase(categoryRepository, transactionRepository)
    }

    @Singleton
    @Provides
    fun provideGetFilteredOverallDataUseCase(repository: TransactionRepository): GetFilteredOverallDataUseCase {
        return GetFilteredOverallDataUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideGetFilteredTransactionsUseCase(repository: TransactionRepository): GetFilteredTransactionsUseCase {
        return GetFilteredTransactionsUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideGetRecentTransactionsUseCase(repository: TransactionRepository): GetRecentTransactionsUseCase {
        return GetRecentTransactionsUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideGetTotalTransactionPerDayByCategoryUseCase(repository: TransactionRepository): GetTotalTransactionPerDayByCategoryUseCase {
        return GetTotalTransactionPerDayByCategoryUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideDateRangeCalculatorUseCase(userConfigurationsManager: UserConfigurationsManager): DateRangeCalculatorUseCase {
        return DateRangeCalculatorUseCase(userConfigurationsManager)
    }

}