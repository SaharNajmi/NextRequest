package com.example.nextrequest.home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nextrequest.core.models.KeyValue
import com.example.nextrequest.core.presentation.theme.tagChipBackground
import com.example.nextrequest.core.presentation.theme.tagChipBorder
import com.example.nextrequest.core.presentation.theme.tagChipRemove
import com.example.nextrequest.core.presentation.theme.tagChipText

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RemovableTagList(items: List<KeyValue>?, onRemoveItem: (String, String) -> Unit) {
    val chipBg = MaterialTheme.colorScheme.tagChipBackground
    val chipBorder = MaterialTheme.colorScheme.tagChipBorder
    val chipTextColor = MaterialTheme.colorScheme.tagChipText
    val chipXColor = MaterialTheme.colorScheme.tagChipRemove

    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items?.forEach { (key, value) ->
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(chipBg)
                    .border(1.dp, chipBorder, RoundedCornerShape(20.dp))
                    .clickable { onRemoveItem(key, value) }
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "$key: $value",
                    color = chipTextColor,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Medium
                )
                Text("×", color = chipXColor, fontSize = 11.sp)
            }
        }
    }
}
