package com.ajay.seenu.expensetracker.domain.model

data class PaginationData<T> constructor(
    val data: T,
    val hasMoreData: Boolean
)