package com.example.nextrequest.core.models

import androidx.compose.ui.graphics.Color

enum class HttpMethod(val color: Color) {
        GET(Color(0xFF1F7922)),
        POST(Color(0xFF853924)),
        PUT(Color(0xFF2F409D)),
        PATCH(Color(0xFF5A30A6)),
        DELETE(Color(0xFF730606)),
        HEAD(Color(0xFF1F7922)),
        OPTIONS(Color(0xFFA41142))
    }