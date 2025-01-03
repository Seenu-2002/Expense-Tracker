package com.ajay.seenu.expensetracker.android.domain.usecases

import com.ajay.seenu.expensetracker.GetTotalAmountByCategoryAndTypeBetween
import com.ajay.seenu.expensetracker.android.data.TransactionRepository
import com.ajay.seenu.expensetracker.android.domain.data.Filter
import com.ajay.seenu.expensetracker.android.domain.data.Transaction
import com.ajay.seenu.expensetracker.android.domain.data.getDateRange
import com.ajay.seenu.expensetracker.android.domain.mapper.CategoryMapper
import com.ajay.seenu.expensetracker.android.domain.mapper.CategoryMapper.map
import com.ajay.seenu.expensetracker.android.presentation.common.ChartDefaults
import com.ajay.seenu.expensetracker.android.presentation.screeens.AnalyticsLegendRowData
import com.ajay.seenu.expensetracker.android.presentation.screeens.AnalyticsScreenData
import com.ajay.seenu.expensetracker.android.presentation.screeens.charts.Entry
import com.ajay.seenu.expensetracker.android.presentation.screeens.charts.PieChartData
import javax.inject.Inject

class GetPieChartDataUseCase @Inject constructor(
    private val repository: TransactionRepository
) {

    suspend operator fun invoke(type: Transaction.Type, filter: Filter): AnalyticsScreenData {
        val data = repository.getTotalAmountByCategory(
            type.map(),
            filter.getDateRange(0) // Fixme: User configured
        )
        val categories = CategoryMapper.mapCategories(repository.getCategories(type))
        val chartData = getChartData(categories, data)
        val legendData = getLegendData(chartData)
        return AnalyticsScreenData(chartData, legendData)
    }

    private fun getChartData(
        categories: List<Transaction.Category>,
        data: List<GetTotalAmountByCategoryAndTypeBetween>
    ): PieChartData {
        val entries = arrayListOf<Entry>()
        for ((index, item) in data.withIndex()) {
            if (item.total == null || item.total == 0.0) {
                continue
            }

            val category = categories.find { it.id == item.category }
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
                it.extras as Transaction.Category
            )
        }
    }


}