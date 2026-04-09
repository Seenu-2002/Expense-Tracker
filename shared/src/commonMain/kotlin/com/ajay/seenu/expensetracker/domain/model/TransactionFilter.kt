package com.ajay.seenu.expensetracker.domain.model

data class TransactionFilter(
    val dateFilter: DateFilter = DateFilter.ThisMonth,
    val type: TransactionType? = null,
    val categoryIds: Set<Long> = emptySet(),
    val accountIds: Set<Long> = emptySet()
) {
    val hasActiveFilters: Boolean
        get() = type != null || categoryIds.isNotEmpty() || accountIds.isNotEmpty()

    val activeFilterCount: Int
        get() = (if (type != null) 1 else 0) +
                (if (categoryIds.isNotEmpty()) 1 else 0) +
                (if (accountIds.isNotEmpty()) 1 else 0)
}
