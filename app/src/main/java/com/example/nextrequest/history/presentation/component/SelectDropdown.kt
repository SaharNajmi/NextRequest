package com.example.nextrequest.history.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.nextrequest.collection.presentation.model.CollectionEntry

@Composable
fun SaveToCollectionDialog(
    items: Set<CollectionEntry>,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
) {
    var selectedItem by remember { mutableStateOf(items.firstOrNull()) }
    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = selectedItem?.let { "Save to ${it.name}" } ?: "No collections exist",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .heightIn(max = 300.dp)
                        .padding(vertical = 2.dp),
                ) {

                    items(items.toList()) { entry ->
                        val backgroundColor: Color =
                            if (selectedItem?.id == entry.id) MaterialTheme.colorScheme.secondaryContainer
                            else Color.Transparent
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(backgroundColor, RoundedCornerShape(4.dp))
                               // .clickable { selectedItem = entry }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedItem?.id == entry.id,
                                onClick = { selectedItem = entry }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = entry.name)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { onDismiss() }) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = {
                        selectedItem?.let { onSave(it.id) }
                        onDismiss()
                    }) {
                        Text("Save")
                    }
                }
            }
        }
    }
}