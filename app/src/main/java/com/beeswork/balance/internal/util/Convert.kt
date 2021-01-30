package com.beeswork.balance.internal.util

import java.util.*


object Convert {

    fun birthYearToAge(birthYear: Int): Int {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        return currentYear - birthYear + 1
    }
}