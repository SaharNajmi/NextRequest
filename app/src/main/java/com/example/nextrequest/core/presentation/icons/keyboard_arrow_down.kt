package com.example.nextrequest.core.presentation.icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Keyboard_arrow_down: ImageVector
    get() {
        if (_MaterialIconsKeyboard_arrow_down != null) return _MaterialIconsKeyboard_arrow_down!!
        
        _MaterialIconsKeyboard_arrow_down = ImageVector.Builder(
            name = "keyboard_arrow_down",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.Black)
            ) {
                moveTo(8.12f, 9.29f)
                lineTo(12f, 13.17f)
                lineToRelative(3.88f, -3.88f)
                curveToRelative(0.39f, -0.39f, 1.02f, -0.39f, 1.41f, 0f)
                curveToRelative(0.39f, 0.39f, 0.39f, 1.02f, 0f, 1.41f)
                lineToRelative(-4.59f, 4.59f)
                curveToRelative(-0.39f, 0.39f, -1.02f, 0.39f, -1.41f, 0f)
                lineTo(6.7f, 10.7f)
                curveToRelative(-0.39f, -0.39f, -0.39f, -1.02f, 0f, -1.41f)
                curveToRelative(0.39f, -0.38f, 1.03f, -0.39f, 1.42f, 0f)
                close()
            }
        }.build()
        
        return _MaterialIconsKeyboard_arrow_down!!
    }

private var _MaterialIconsKeyboard_arrow_down: ImageVector? = null

