package com.ajay.seenu.expensetracker.domain

object DateFormats {

    val FORMATS = listOf(
        "dd/MM/yyyy",         // 19/02/2002
        "MM/dd/yyyy",         // 02/19/2002
        "yyyy/MM/dd",         // 2002/02/19
        "dd MMM yyyy",        // 19 Feb 2002
        "dd MMMM yyyy",       // 19 February 2002
        "MMM dd, yyyy",       // Feb 19, 2002
        "MMMM dd, yyyy",      // February 19, 2002
        "EEE, dd MMM yyyy",   // Tue, 19 Feb 2002
        "EEEE, dd MMMM yyyy", // Tuesday, 19 February 2002
        "yyyy-MM-dd",         // 2002-02-19
        "dd-MM-yyyy",         // 19-02-2002
        "MM-dd-yyyy",         // 02-19-2002
        "dd.MM.yyyy",         // 19.02.2002
        "MM.dd.yyyy",         // 02.19.2002
        "yyyy.MM.dd",         // 2002.02.19
    )

}