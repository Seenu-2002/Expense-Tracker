package com.ajay.seenu.expensetracker.android.domain

import com.ajay.seenu.expensetracker.android.domain.data.Transaction

class CategoryRelationMapper constructor(private val categories: List<Transaction.Category>) {

    private val parentVsChildren: HashMap<Transaction.Category, ArrayList<Transaction.Category>> = hashMapOf()

    init {
        indexChildren()
    }

    private fun indexChildren() {
        categories.forEach { category ->
            val parent = category.parent
            if (parent != null) {
                parentVsChildren[parent]?.add(category) ?: run {
                    parentVsChildren[parent] = arrayListOf(category)
                }
            } else {
                parentVsChildren[category] = arrayListOf()
            }
        }
    }

    fun getChildren(category: Transaction.Category): List<Transaction.Category> {
        return parentVsChildren[category]?.toList() ?: emptyList()
    }

}