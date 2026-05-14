package com.ajay.seenu.expensetracker.android.di

import com.ajay.seenu.expensetracker.UserConfigurationsManager
import com.ajay.seenu.expensetracker.android.domain.usecases.GetPieChartDataUseCase
import com.ajay.seenu.expensetracker.data.repository.AccountRepository
import com.ajay.seenu.expensetracker.data.repository.AttachmentRepository
import com.ajay.seenu.expensetracker.data.repository.CategoryRepository
import com.ajay.seenu.expensetracker.data.repository.TransactionRepository
import com.ajay.seenu.expensetracker.domain.usecase.DateRangeCalculatorUseCase
import com.ajay.seenu.expensetracker.domain.usecase.account.CreateAccountUseCase
import com.ajay.seenu.expensetracker.domain.usecase.account.DeleteAccountUseCase
import com.ajay.seenu.expensetracker.domain.usecase.account.GetAccountUseCase
import com.ajay.seenu.expensetracker.domain.usecase.account.GetAccountsAsFlowUseCase
import com.ajay.seenu.expensetracker.domain.usecase.account.GetAccountsUseCase
import com.ajay.seenu.expensetracker.domain.usecase.account.GetAccountsWithBalanceAsFlowUseCase
import com.ajay.seenu.expensetracker.domain.usecase.account.InsertDefaultAccountsUseCase
import com.ajay.seenu.expensetracker.domain.usecase.account.UpdateAccountUseCase
import com.ajay.seenu.expensetracker.domain.usecase.attachment.AddAttachmentUseCase
import com.ajay.seenu.expensetracker.domain.usecase.attachment.GetAttachmentsUseCase
import com.ajay.seenu.expensetracker.domain.usecase.attachment.ReplaceAttachmentsUseCase
import com.ajay.seenu.expensetracker.domain.usecase.category.AddCategoryUseCase
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
import com.ajay.seenu.expensetracker.domain.usecase.transaction.ChangeAccountUseCase
import com.ajay.seenu.expensetracker.domain.usecase.transaction.ChangeCategoriesUseCase
import com.ajay.seenu.expensetracker.domain.usecase.transaction.DeleteAllTransactionsUseCase
import com.ajay.seenu.expensetracker.domain.usecase.transaction.DeleteTransactionUseCase
import com.ajay.seenu.expensetracker.domain.usecase.transaction.GetTrashTransactionsUseCase
import com.ajay.seenu.expensetracker.domain.usecase.transaction.GetTransactionCountByCategoryUseCase
import com.ajay.seenu.expensetracker.domain.usecase.transaction.GetTransactionCountByAccountUseCase
import com.ajay.seenu.expensetracker.domain.usecase.transaction.GetTransactionUseCase
import com.ajay.seenu.expensetracker.domain.usecase.transaction.PermanentlyDeleteTransactionUseCase
import com.ajay.seenu.expensetracker.domain.usecase.transaction.PurgeOldTrashUseCase
import com.ajay.seenu.expensetracker.domain.usecase.transaction.RestoreTransactionUseCase
import com.ajay.seenu.expensetracker.domain.usecase.transaction.SoftDeleteTransactionUseCase
import com.ajay.seenu.expensetracker.domain.usecase.transaction.UpdateTransactionUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideAddAttachmentUseCase(repository: AttachmentRepository): AddAttachmentUseCase {
        return AddAttachmentUseCase(repository)
    }

    @Provides
    fun provideGetAttachmentsUseCase(repository: AttachmentRepository): GetAttachmentsUseCase {
        return GetAttachmentsUseCase(repository)
    }

    @Provides
    fun provideReplaceAttachmentsUseCase(repository: AttachmentRepository): ReplaceAttachmentsUseCase {
        return ReplaceAttachmentsUseCase(repository)
    }

    @Provides
    fun provideAddCategoryUseCase(repository: CategoryRepository): AddCategoryUseCase {
        return AddCategoryUseCase(repository)
    }

    @Provides
    fun provideChangeCategoriesUseCase(repository: TransactionRepository): ChangeCategoriesUseCase {
        return ChangeCategoriesUseCase(repository)
    }

    @Provides
    fun provideDeleteCategoryUseCase(repository: CategoryRepository): DeleteCategoryUseCase {
        return DeleteCategoryUseCase(repository)
    }

    @Provides
    fun provideGetAllCategoriesAsFlowUseCase(repository: CategoryRepository): GetAllCategoriesAsFlowUseCase {
        return GetAllCategoriesAsFlowUseCase(repository)
    }

    @Provides
    fun provideGetAllCategoriesUseCase(repository: CategoryRepository): GetAllCategoriesUseCase {
        return GetAllCategoriesUseCase(repository)
    }

    @Provides
    fun provideGetCategoryUseCase(repository: CategoryRepository): GetCategoryUseCase {
        return GetCategoryUseCase(repository)
    }

    @Provides
    fun provideUpdateCategoryUseCase(repository: CategoryRepository): UpdateCategoryUseCase {
        return UpdateCategoryUseCase(repository)
    }

    @Provides
    fun provideAddTransactionUseCase(repository: TransactionRepository): AddTransactionUseCase {
        return AddTransactionUseCase(repository)
    }

    @Provides
    fun provideDeleteTransactionUseCase(repository: TransactionRepository): DeleteTransactionUseCase {
        return DeleteTransactionUseCase(repository)
    }

    @Provides
    fun provideSoftDeleteTransactionUseCase(repository: TransactionRepository): SoftDeleteTransactionUseCase {
        return SoftDeleteTransactionUseCase(repository)
    }

    @Provides
    fun provideRestoreTransactionUseCase(repository: TransactionRepository): RestoreTransactionUseCase {
        return RestoreTransactionUseCase(repository)
    }

    @Provides
    fun provideGetTrashTransactionsUseCase(repository: TransactionRepository): GetTrashTransactionsUseCase {
        return GetTrashTransactionsUseCase(repository)
    }

    @Provides
    fun providePermanentlyDeleteTransactionUseCase(repository: TransactionRepository): PermanentlyDeleteTransactionUseCase {
        return PermanentlyDeleteTransactionUseCase(repository)
    }

    @Provides
    fun providePurgeOldTrashUseCase(repository: TransactionRepository): PurgeOldTrashUseCase {
        return PurgeOldTrashUseCase(repository)
    }

    @Provides
    fun provideDeleteAllTransactionsUseCase(repository: TransactionRepository): DeleteAllTransactionsUseCase {
        return DeleteAllTransactionsUseCase(repository)
    }

    @Provides
    fun provideGetTransactionUseCase(repository: TransactionRepository): GetTransactionUseCase {
        return GetTransactionUseCase(repository)
    }

    @Provides
    fun provideUpdateTransactionUseCase(repository: TransactionRepository): UpdateTransactionUseCase {
        return UpdateTransactionUseCase(repository)
    }

    @Provides
    fun provideGetTransactionCountByCategoryUseCase(repository: TransactionRepository): GetTransactionCountByCategoryUseCase {
        return GetTransactionCountByCategoryUseCase(repository)
    }

    @Provides
    fun provideGetExpenseByCategoryUseCase(
        categoryRepository: CategoryRepository,
        transactionRepository: TransactionRepository
    ): GetExpenseByCategoryUseCase {
        return GetExpenseByCategoryUseCase(categoryRepository, transactionRepository)
    }

    @Provides
    fun provideGetFilteredOverallDataUseCase(repository: TransactionRepository): GetFilteredOverallDataUseCase {
        return GetFilteredOverallDataUseCase(repository)
    }

    @Provides
    fun provideGetFilteredTransactionsUseCase(repository: TransactionRepository): GetFilteredTransactionsUseCase {
        return GetFilteredTransactionsUseCase(repository)
    }

    @Provides
    fun provideGetRecentTransactionsUseCase(repository: TransactionRepository): GetRecentTransactionsUseCase {
        return GetRecentTransactionsUseCase(repository)
    }

    @Provides
    fun provideGetTotalTransactionPerDayByCategoryUseCase(repository: TransactionRepository): GetTotalTransactionPerDayByCategoryUseCase {
        return GetTotalTransactionPerDayByCategoryUseCase(repository)
    }

    @Provides
    fun provideDateRangeCalculatorUseCase(userConfigurationsManager: UserConfigurationsManager): DateRangeCalculatorUseCase {
        return DateRangeCalculatorUseCase(userConfigurationsManager)
    }

    @Provides
    fun provideInsertDefaultAccountsUseCase(repository: AccountRepository): InsertDefaultAccountsUseCase {
        return InsertDefaultAccountsUseCase(repository)
    }

    @Provides
    fun provideGetAccountsUseCase(repository: AccountRepository): GetAccountsUseCase {
        return GetAccountsUseCase(repository)
    }

    @Provides
    fun provideGetAccountsAsFlowUseCase(repository: AccountRepository): GetAccountsAsFlowUseCase {
        return GetAccountsAsFlowUseCase(repository)
    }

    @Provides
    fun provideGetAccountsWithBalanceAsFlowUseCase(repository: AccountRepository): GetAccountsWithBalanceAsFlowUseCase {
        return GetAccountsWithBalanceAsFlowUseCase(repository)
    }

    @Provides
    fun provideGetAccountUseCase(repository: AccountRepository): GetAccountUseCase {
        return GetAccountUseCase(repository)
    }

    @Provides
    fun provideCreateAccountUseCase(repository: AccountRepository): CreateAccountUseCase {
        return CreateAccountUseCase(repository)
    }

    @Provides
    fun provideUpdateAccountUseCase(repository: AccountRepository): UpdateAccountUseCase {
        return UpdateAccountUseCase(repository)
    }

    @Provides
    fun provideDeleteAccountUseCase(repository: AccountRepository): DeleteAccountUseCase {
        return DeleteAccountUseCase(repository)
    }

    @Provides
    fun provideGetTransactionCountByUseCase(repository: TransactionRepository): GetTransactionCountByAccountUseCase {
        return GetTransactionCountByAccountUseCase(repository)
    }

    @Provides
    fun provideChangeAccountUseCase(repository: TransactionRepository): ChangeAccountUseCase {
        return ChangeAccountUseCase(repository)
    }

    @Provides
    fun provideGetPieChartDataUseCase(
        transactionRepository: TransactionRepository,
        categoryRepository: CategoryRepository
    ): GetPieChartDataUseCase {
        return GetPieChartDataUseCase(transactionRepository, categoryRepository)
    }

}