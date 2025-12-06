package com.example.nextrequest.history.domain.model

import androidx.compose.ui.graphics.ImageBitmap
import com.example.nextrequest.core.KeyValueList
import com.example.nextrequest.core.models.HttpMethod
import java.time.LocalDate

data class History(
    val id: Int = 0,
    val requestUrl: String,
    val httpMethod: HttpMethod = HttpMethod.GET,
    val createdAt: LocalDate = LocalDate.now(),
    val response: String = "",
    val imageResponse: ImageBitmap? = null,
    val statusCode: Int? = null,
    val body: String? = null,
    val headers: KeyValueList? = null
)