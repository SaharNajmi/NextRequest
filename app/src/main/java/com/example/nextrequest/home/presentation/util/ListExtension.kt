package com.example.nextrequest.home.presentation.util

import com.example.nextrequest.core.models.KeyValue

fun List<KeyValue>.getHeaderValue(key: String): String {
    return firstOrNull { it.key.equals(key, ignoreCase = true) }?.value ?: ""
}