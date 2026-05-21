package com.ajay.seenu.expensetracker.data.data_source

import com.ajay.seenu.expensetracker.domain.model.ImportResult

interface ImportDataSource {
    suspend fun importFromJson(content: String): ImportResult
    suspend fun importFromCsv(content: String): ImportResult
}
