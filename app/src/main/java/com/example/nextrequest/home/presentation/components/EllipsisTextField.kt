package com.example.nextrequest.home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.style.TextOverflow
import com.example.nextrequest.core.presentation.theme.inputBackground

@Composable
fun EllipsisTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    var isFocused by remember { mutableStateOf(false) }

    AppTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        modifier = modifier.onFocusChanged { isFocused = it.isFocused },
        decorationOverlay = if (!isFocused && value.isNotEmpty()) {
            {
                Text(
                    text = value,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = appTextFieldStyle.copy(color = MaterialTheme.colorScheme.onSurface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.inputBackground)
                )
            }
        } else null
    )
}
