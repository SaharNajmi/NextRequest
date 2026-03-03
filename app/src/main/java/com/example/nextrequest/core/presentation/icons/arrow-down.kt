package com.example.nextrequest.core.presentation.icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val ArrowDown: ImageVector
    get() {
        if (_BootstrapArrowDownShort != null) return _BootstrapArrowDownShort!!
        _BootstrapArrowDownShort = ImageVector.Builder(
            name = "arrow-down-short",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 16f,
            viewportHeight = 16f
        ).apply {
            path(
                fill = SolidColor(Color.Black)
            ) {
                moveTo(8f, 4f)
                arcToRelative(0.5f, 0.5f, 0f, false, true, 0.5f, 0.5f)
                verticalLineToRelative(5.793f)
                lineToRelative(2.146f, -2.147f)
                arcToRelative(0.5f, 0.5f, 0f, false, true, 0.708f, 0.708f)
                lineToRelative(-3f, 3f)
                arcToRelative(0.5f, 0.5f, 0f, false, true, -0.708f, 0f)
                lineToRelative(-3f, -3f)
                arcToRelative(0.5f, 0.5f, 0f, true, true, 0.708f, -0.708f)
                lineTo(7.5f, 10.293f)
                verticalLineTo(4.5f)
                arcTo(0.5f, 0.5f, 0f, false, true, 8f, 4f)
            }
        }.build()
        
        return _BootstrapArrowDownShort!!
    }

private var _BootstrapArrowDownShort: ImageVector? = null

