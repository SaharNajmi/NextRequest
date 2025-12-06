package com.example.nextrequest.home.presentation.util

import com.example.nextrequest.core.KeyValueList

fun KeyValueList.getHeaderValue(key: String): String {
    return firstOrNull { it.first.equals(key, ignoreCase = true) }?.second ?: ""
}