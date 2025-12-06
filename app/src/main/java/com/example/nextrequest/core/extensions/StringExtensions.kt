package com.example.nextrequest.core.extensions

import com.example.nextrequest.core.KeyValueList
import com.example.nextrequest.core.models.HttpMethod
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser

fun String.formatJson(): String {
    return try {
        val jsonElement = JsonParser.parseString(this)
        GsonBuilder().setPrettyPrinting().create().toJson(jsonElement)
    } catch (e: Exception) {
        this
    }
}

fun String.mapStringToKeyValuePairs(): KeyValueList {
    if (this.isEmpty())
        return emptyList()

    val query = this.substringAfter("?", "")

    if (query.isEmpty())
        return emptyList()

    return query
        .split("&")
        .filter { it.isNotBlank() }
        .map {
            val parts = it.split("=")
            val key = parts.getOrNull(0) ?: ""
            val value = parts.getOrNull(1) ?: ""
            key to value
        }
}

fun KeyValueList.mapKeyValuePairsToQueryParameter(): String {
    return this.joinToString("&") { (prefix, postfix) ->
        "$prefix=$postfix"
    }
}

fun String.buildUrlWithParams(queryParams: String): String {
    val baseUrl = this.substringBefore("?")
    return if (queryParams.isEmpty())
        this
    else
        "$baseUrl?$queryParams"
}

fun String.parseHttpMethodFromString(): HttpMethod {
    val methodPart: String = this.substringBefore(" ")
    return HttpMethod.valueOf(methodPart)
}

fun String.removeParameterFromUrl(key: String, value: String): String {
    val query = this.substringAfter("?", "")
    if (query.isEmpty()) return this

    val newQuery = query.split("&")
        .filter { it != "$key=$value" }
        .joinToString("&")

    return this.substringBefore("?") + if (newQuery.isNotEmpty()) "?$newQuery" else ""
}