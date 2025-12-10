package com.example.nextrequest.core.presentation.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.nextrequest.core.presentation.icons.Search
import com.example.nextrequest.core.presentation.theme.Silver
import com.example.nextrequest.core.presentation.theme.focusedBorderColor
import com.example.nextrequest.core.presentation.theme.unfocusedBorderColor

@Composable
fun CustomSearchBar(
    queryHint: String,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = { onSearchQueryChanged(it) },
        placeholder = { Text(queryHint, color = Silver) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
       shape = RoundedCornerShape(12.dp),
        singleLine = true,
        leadingIcon = {
            Icon(
                imageVector = Search,
                contentDescription = "Search Icon",
                tint = Silver
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.focusedBorderColor,
            unfocusedBorderColor = MaterialTheme.colorScheme.unfocusedBorderColor,
        ),
    )
}