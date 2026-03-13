package com.example.nextrequest.core.data.db

import androidx.room.TypeConverter
import com.example.nextrequest.core.models.KeyValue
import com.example.nextrequest.history.data.model.HttpRequest
import com.example.nextrequest.history.data.model.WebSocketRequest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun keyValueListToJson(list: List<KeyValue>?): String? {
        return list?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun jsonToKeyValueList(json: String?): List<KeyValue>? {
        return json?.let {
            val type = object : TypeToken<List<KeyValue>>() {}.type
            gson.fromJson(json, type)
        }
    }

    @TypeConverter
    fun httpRequestToJson(request: HttpRequest): String = gson.toJson(request)

    @TypeConverter
    fun jsonToHttpRequest(json: String): HttpRequest {
        return gson.fromJson(json, HttpRequest::class.java)
    }

    @TypeConverter
    fun webSocketRequestToJson(request: WebSocketRequest): String = gson.toJson(request)

    @TypeConverter
    fun jsonToWebSocketRequest(json: String): WebSocketRequest {
        return gson.fromJson(json, WebSocketRequest::class.java)
    }
}