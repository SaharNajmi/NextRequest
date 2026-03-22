package com.example.nextrequest.history.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.sahar.nextrequest.R
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.nextrequest.collection.presentation.model.CollectionEntry
import com.example.nextrequest.core.presentation.icons.Add
import com.example.nextrequest.core.presentation.icons.Check
import com.example.nextrequest.core.presentation.icons.Close
import com.example.nextrequest.core.presentation.theme.cardBackground
import com.example.nextrequest.core.presentation.theme.cardBorder
import com.example.nextrequest.core.presentation.theme.iconMuted
import com.example.nextrequest.core.presentation.theme.iconTint
import com.example.nextrequest.core.presentation.theme.inputBackground
import com.example.nextrequest.core.presentation.theme.textMuted

@Composable
fun SaveToCollectionDialog(
    items: Set<CollectionEntry>,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
    onAddNewCollection: (String) -> Unit,
) {
    var selectedItem by remember(items) { mutableStateOf(items.firstOrNull()) }
    var showNewCollectionInput by remember { mutableStateOf(false) }
    var newCollectionName by remember { mutableStateOf("") }
    var pendingSelectionName by remember { mutableStateOf<String?>(null) }
    var scrollToItem by remember { mutableStateOf<CollectionEntry?>(null) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(items) {
        pendingSelectionName?.let { name ->
            items.find { it.name == name }?.let {
                selectedItem = it
                scrollToItem = it
            }
            pendingSelectionName = null
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(0.5.dp, MaterialTheme.colorScheme.cardBorder, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.cardBackground)
        ) {
            DialogHeader(selectedItem)

            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.cardBorder)

            CollectionList(
                items = items,
                selectedItem = selectedItem,
                scrollToItem = scrollToItem,
                onSelect = { selectedItem = it }
            )

            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.cardBorder)

            if (showNewCollectionInput) {
                NewCollectionInput(
                    value = newCollectionName,
                    onValueChange = { newCollectionName = it },
                    focusRequester = focusRequester,
                    onCancel = {
                        newCollectionName = ""
                        showNewCollectionInput = false
                    },
                    onConfirm = {
                        val name = newCollectionName.trim()
                        pendingSelectionName = name
                        onAddNewCollection(name)
                        newCollectionName = ""
                        showNewCollectionInput = false
                    },
                )
            } else {
                AddNewCollectionRow(onClick = { showNewCollectionInput = true })
            }

            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.cardBorder)

            DialogActions(
                saveEnabled = items.isNotEmpty(),
                onDismiss = onDismiss,
                onSave = {
                    selectedItem?.let { onSave(it.id) }
                    onDismiss()
                }
            )
        }
    }
}

@Composable
private fun DialogHeader(selectedItem: CollectionEntry?) {
    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 12.dp)) {
        Text(
            text = selectedItem?.let { stringResource(R.string.msg_save_to_collection, it.name) } ?: stringResource(R.string.msg_no_collections_yet),
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = stringResource(R.string.msg_select_collection),
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.textMuted,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@Composable
private fun CollectionList(
    items: Set<CollectionEntry>,
    selectedItem: CollectionEntry?,
    scrollToItem: CollectionEntry?,
    onSelect: (CollectionEntry) -> Unit,
) {
    val listState = rememberLazyListState()
    val showFade by remember { derivedStateOf { listState.canScrollForward } }

    LaunchedEffect(scrollToItem) {
        scrollToItem?.let { target ->
            val index = items.toList().indexOfFirst { it.id == target.id }
            if (index >= 0) listState.animateScrollToItem(index)
        }
    }

    Box(modifier = Modifier.heightIn(max = 240.dp)) {
        LazyColumn(
            state = listState,
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(items.toList()) { entry ->
                CollectionListItem(
                    entry = entry,
                    isSelected = selectedItem?.id == entry.id,
                    onClick = { onSelect(entry) }
                )
            }
        }
        if (showFade) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, MaterialTheme.colorScheme.cardBackground)
                        )
                    )
            )
        }
    }
}

@Composable
private fun CollectionListItem(
    entry: CollectionEntry,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isSelected) MaterialTheme.colorScheme.secondaryContainer
                else Color.Transparent
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) MaterialTheme.colorScheme.secondary
                    else MaterialTheme.colorScheme.cardBorder
                )
        )
        Text(
            text = entry.name,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
            color = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer
            else MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp)
        )
        if (isSelected) {
            Icon(
                imageVector = Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun NewCollectionInput(
    value: String,
    onValueChange: (String) -> Unit,
    focusRequester: FocusRequester,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
) {
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .border(1.5.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(42.dp)
                .background(MaterialTheme.colorScheme.primary)
        )
        Icon(
            imageVector = Add,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .size(16.dp)
        )
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onBackground
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { if (value.isNotBlank()) onConfirm() }),
            decorationBox = { inner ->
                Box(modifier = Modifier.fillMaxWidth()) {
                    if (value.isEmpty()) {
                        Text(
                            text = stringResource(R.string.hint_collection_name),
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.textMuted
                        )
                    }
                    inner()
                }
            },
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 12.dp)
                .focusRequester(focusRequester)
        )
        InputDivider()
        Box(
            modifier = Modifier
                .size(42.dp)
                .clickable(onClick = onCancel),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Close,
                contentDescription = stringResource(R.string.cd_cancel),
                tint = MaterialTheme.colorScheme.iconMuted,
                modifier = Modifier.size(16.dp)
            )
        }
        InputDivider()
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(
                    if (value.isNotBlank()) MaterialTheme.colorScheme.primary
                    else Color.Transparent
                )
                .clickable(enabled = value.isNotBlank(), onClick = onConfirm),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Check,
                contentDescription = stringResource(R.string.cd_create),
                tint = if (value.isNotBlank()) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.textMuted,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun InputDivider() {
    Box(
        modifier = Modifier
            .width(0.5.dp)
            .height(42.dp)
            .background(MaterialTheme.colorScheme.cardBorder)
    )
}

@Composable
private fun AddNewCollectionRow(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Add,
            contentDescription = stringResource(R.string.action_add_new_collection),
            tint = MaterialTheme.colorScheme.iconTint,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(R.string.action_add_new_collection),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.iconTint
        )
    }
}

@Composable
private fun DialogActions(
    saveEnabled: Boolean,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = onDismiss) {
            Text(
                text = stringResource(R.string.action_cancel),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.textMuted
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Button(
            enabled = saveEnabled,
            onClick = onSave,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = stringResource(R.string.action_save), fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}