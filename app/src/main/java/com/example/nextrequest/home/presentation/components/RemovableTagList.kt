package com.example.nextrequest.home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.nextrequest.core.KeyValueList

@Composable
fun RemovableTagList(items: KeyValueList?, onRemoveItem: (String, String) -> Unit) {
    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .height(48.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .horizontalScroll(scrollState),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items?.forEach { (key, value) ->
            Text(
                text = "$key: $value", color = Color.Black,
                modifier = Modifier
                    .clickable {
                        onRemoveItem(key, value)
                    }
                    .padding(horizontal = 8.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, shape = MaterialTheme.shapes.small)
                    .padding(horizontal = 4.dp),
             //   color = MaterialTheme.colorScheme.background
            )
        }
    }
}
