package com.example.nextrequest.core.presentation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.nextrequest.core.models.HttpMethod


val HttpMethod.color
    @Composable
    get() = when (this) {
        HttpMethod.GET -> if (isSystemInDarkTheme()) Color(0xFF1F7922) else Color(0xFF429A45)
        HttpMethod.POST -> if (isSystemInDarkTheme()) Color(0xFF853924) else Color(0xFFFF5722)
        HttpMethod.PUT -> if (isSystemInDarkTheme()) Color(0xFF2F409D) else Color(0xFF2196F3)
        HttpMethod.PATCH -> if (isSystemInDarkTheme()) Color(0xFF5A30A6) else Color(0xFF9C27B0)
        HttpMethod.DELETE -> if (isSystemInDarkTheme()) Color(0xFF730606) else Color(0xFFBA1A1A)
        HttpMethod.HEAD -> if (isSystemInDarkTheme()) Color(0xFF1F7922) else Color(0xFF3E9B41)
        HttpMethod.OPTIONS -> if (isSystemInDarkTheme()) Color(0xFFA41142) else Color(0xFFE91E63)
    }