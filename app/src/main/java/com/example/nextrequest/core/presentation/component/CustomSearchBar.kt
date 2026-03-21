package com.example.nextrequest.core.presentation.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nextrequest.core.presentation.icons.Search
import com.example.nextrequest.core.presentation.theme.Silver
import com.example.nextrequest.core.presentation.theme.inputFieldColors
import com.example.nextrequest.core.presentation.theme.textMuted

@Composable
fun CustomSearchBar(
    queryHint: String,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
) {
    TextField(
        value = searchQuery,
        onValueChange = { onSearchQueryChanged(it) },
        placeholder = { Text(queryHint, color = Silver, fontSize = 12.sp) },
        singleLine = true,
        leadingIcon = {
            Icon(
                imageVector = Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.textMuted,
                modifier = Modifier.size(18.dp)
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .border(0.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(10.dp),
        colors = inputFieldColors()
    )
}