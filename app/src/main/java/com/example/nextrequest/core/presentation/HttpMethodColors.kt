package com.example.nextrequest.core.presentation

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.nextrequest.core.models.HttpMethod
import com.example.nextrequest.core.presentation.theme.isDark

val HttpMethod.color: Color
    @Composable
    get() {
        val dark = MaterialTheme.colorScheme.isDark
        return when (this) {
            //              Light (800-level, readable on white)   Dark (300-level, vibrant on dark)
            HttpMethod.GET     -> if (dark) Color(0xFF81C784) else Color(0xFF2E7D32)
            HttpMethod.POST    -> if (dark) Color(0xFFFF8A65) else Color(0xFFBF360C)
            HttpMethod.PUT     -> if (dark) Color(0xFF64B5F6) else Color(0xFF1565C0)
            HttpMethod.PATCH   -> if (dark) Color(0xFFCE93D8) else Color(0xFF6A1B9A)
            HttpMethod.DELETE  -> if (dark) Color(0xFFEF9A9A) else Color(0xFFB71C1C)
            HttpMethod.HEAD    -> if (dark) Color(0xFF80CBC4) else Color(0xFF00695C)
            HttpMethod.OPTIONS -> if (dark) Color(0xFFF48FB1) else Color(0xFF880E4F)
        }
    }
