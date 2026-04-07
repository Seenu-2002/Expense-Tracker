package com.ajay.seenu.expensetracker.android


object QueryMaker {

    private val incomeCategory = (1 .. 6).toList()
    private val expenseCategory = (7 .. 16).toList() //+ (32 .. 37).toList()
    private val date = (0 .. 10449980000)
    private val paymentTypes = listOf("UPI", "CARD", "CASH")
    private val amount = (1 .. 10)
    private val type = (0 .. 100)

    fun generateTransactions(count: Int): String {
        var query = "INSERT INTO TransactionDetail(type,amount,category,paymentType,date) VALUES "
        repeat(count) {
            query += "${generateTransaction()},"
        }
        query = query.removeSuffix(",") + ";"
        return query
    }

    private fun generateTransaction(): String? {
        val type = type.random().let {
            if (it > 70) {
                "INCOME"
            } else {
                "EXPENSE"
            }
        }

        val date = date.random() + 1748629800000//1704047400000
        when(type) {
            "INCOME" -> {
                val category = incomeCategory.random()
                val amount = amount.random() * 500
                val paymentType = "CASH"
                return """
                    ("$type",$amount,$category,"$paymentType",$date)
                """.trimIndent()
            }
            "EXPENSE" -> {
                val category = expenseCategory.random()
                val amount = amount.random() * 100
                val paymentType = paymentTypes.random()
                return """
                    ("$type",$amount,$category,"$paymentType",$date)
                """.trimIndent()
            }
        }
        return null
    }

}

fun main() {
    val query = QueryMaker.generateTransactions(1000)
    println(query)
}