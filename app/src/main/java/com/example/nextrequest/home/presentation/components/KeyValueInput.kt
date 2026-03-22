package com.example.nextrequest.home.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.nextrequest.core.presentation.icons.Add
import com.sahar.nextrequest.R

@Composable
fun KeyValueInput(
    item: (String, String) -> Unit,
) {
    var key by remember { mutableStateOf("") }
    var value by remember { mutableStateOf("") }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(8.dp)
    ) {
        AppTextField(
            value = key,
            onValueChange = { key = it },
            placeholder = stringResource(R.string.hint_key),
            modifier = Modifier.weight(1f)
        )
        AppTextField(
            value = value,
            onValueChange = { value = it },
            placeholder = stringResource(R.string.hint_value),
            modifier = Modifier.weight(1f)
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
                contentDescription = stringResource(R.string.cd_add),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
