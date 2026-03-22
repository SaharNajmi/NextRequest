package com.example.nextrequest.home.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nextrequest.core.presentation.icons.Add
import com.example.nextrequest.core.presentation.theme.inputFieldColors
import com.example.nextrequest.core.presentation.theme.textMuted

@Composable
fun KeyValueInput(
    item: (String, String) -> Unit,
) {
    var key by remember { mutableStateOf("") }
    var value by remember { mutableStateOf("") }
    val fieldColors = inputFieldColors()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(8.dp)
    ) {
        TextField(
            modifier = Modifier.weight(1f),
            value = key,
            singleLine = true,
            placeholder = { Text("Key", color = MaterialTheme.colorScheme.textMuted, fontSize = 12.sp) },
            shape = RoundedCornerShape(10.dp),
            colors = fieldColors,
            onValueChange = { key = it }
        )
        TextField(
            modifier = Modifier.weight(1f),
            value = value,
            singleLine = true,
            placeholder = { Text("Value", color = MaterialTheme.colorScheme.textMuted, fontSize = 12.sp) },
            shape = RoundedCornerShape(10.dp),
            colors = fieldColors,
            onValueChange = { value = it }
        )
        IconButton(
            onClick = {
                item(key.trim(), value.trim())
                key = ""
                value = ""
            },
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Add,
                contentDescription = "Add",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
