package com.ajay.seenu.expensetracker

data class PaginationData<T> constructor(
    val data: T,
    val hasMoreData: Boolean
)
