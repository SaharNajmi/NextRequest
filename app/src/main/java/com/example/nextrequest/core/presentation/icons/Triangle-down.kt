package com.example.nextrequest.core.presentation.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val TriangleDown: ImageVector
    get() {
        if (_triangle_down != null) return _triangle_down!!
        
        _triangle_down = ImageVector.Builder(
            name = "Arrow_drop_down",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000))
            ) {
                moveTo(480f, 600f)
                lineTo(280f, 400f)
                horizontalLineToRelative(400f)
                close()
            }
        }.build()
        
        return _triangle_down!!
    }

private var _triangle_down: ImageVector? = null

