package com.ajay.seenu.expensetracker.android.domain.usecases

import com.ajay.seenu.expensetracker.GetTotalAmountByCategoryAndTypeBetween
import com.ajay.seenu.expensetracker.android.presentation.common.ChartDefaults
import com.ajay.seenu.expensetracker.android.presentation.screeens.AnalyticsLegendRowData
import com.ajay.seenu.expensetracker.android.presentation.screeens.AnalyticsScreenData
import com.ajay.seenu.expensetracker.android.presentation.screeens.charts.Entry
import com.ajay.seenu.expensetracker.android.presentation.screeens.charts.PieChartData
import com.ajay.seenu.expensetracker.data.repository.CategoryRepository
import com.ajay.seenu.expensetracker.data.repository.TransactionRepository
import com.ajay.seenu.expensetracker.domain.model.Category
import com.ajay.seenu.expensetracker.domain.model.DateRange
import com.ajay.seenu.expensetracker.domain.model.TransactionType
import javax.inject.Inject

class GetPieChartDataUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) {

    suspend operator fun invoke(type: TransactionType, dateRange: DateRange): AnalyticsScreenData {
        val data = transactionRepository.getTotalAmountByCategory(
            type = type,
            dateRange = dateRange
        )
        val categories = categoryRepository.getCategories(type)
        val chartData = getChartData(categories, data)
        val legendData = getLegendData(chartData)
        return AnalyticsScreenData(chartData, legendData)
    }

    private fun getChartData(
        categories: List<Category>,
        data: List<GetTotalAmountByCategoryAndTypeBetween>
    ): PieChartData {
        val entries = arrayListOf<Entry>()
        for ((index, item) in data.withIndex()) {
            if (item.total == null || item.total == 0.0) {
                continue
            }

            val category = categories.find { it.id == item.categoryId }
                ?: continue
            val entry = Entry(
                category.label,
                item.total ?: 0.0,
                ChartDefaults.getDynamicColor(index),
                extras = category
            )
            entries.add(entry)
        }

        return PieChartData(entries)
    }

    private fun getLegendData(data: PieChartData): List<AnalyticsLegendRowData> {
        val sum = data.sum
        return data.entries.map {
            AnalyticsLegendRowData(
                it.x,
                it.y,
                it.color,
                (it.y / sum * 100).toFloat(),
                it.extras as Category
            )
        }
    }


}