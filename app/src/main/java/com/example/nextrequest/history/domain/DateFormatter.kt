package com.example.nextrequest.history.domain

import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

fun formatDate(date: LocalDate): String {
    val today = LocalDate.now()
    val yesterday = today.minusDays(1)

    return when (date) {
        today -> "Today"
        yesterday -> "Yesterday"
        else -> {
            val day = date.dayOfMonth
            val month = date.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())
            "$day $month"
        }
    }
}