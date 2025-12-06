package com.example.nextrequest.core.data.extensions

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

fun LocalDate.toLong(): Long {
    return this.atStartOfDay(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()
}

fun Long.toLocalDate(): LocalDate {
    return Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
}