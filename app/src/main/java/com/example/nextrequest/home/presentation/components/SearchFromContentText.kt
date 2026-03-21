package com.example.nextrequest.home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nextrequest.core.extensions.formatJson
import com.example.nextrequest.core.presentation.icons.Close
import com.example.nextrequest.core.presentation.icons.Keyboard_arrow_down
import com.example.nextrequest.core.presentation.icons.Keyboard_arrow_up
import com.example.nextrequest.core.presentation.icons.Search
import com.example.nextrequest.core.presentation.theme.Silver
import com.example.nextrequest.core.presentation.theme.cardBorder
import com.example.nextrequest.core.presentation.theme.inputBackground
import com.example.nextrequest.core.presentation.theme.textMuted
import com.example.nextrequest.home.domain.HighlightedTextLine
import com.example.nextrequest.home.domain.buildHighlightedTextLines

@Composable
fun SearchFromContentText(
    contentText: String,
    isSearchCardVisible: Boolean,
    onDismissSearch: () -> Unit,
) {
    val formattedJson = remember(contentText) { contentText.formatJson() }
    val lines = remember(formattedJson) { formattedJson.lines() }
    val listState = rememberLazyListState()
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var targetMatchIndex by rememberSaveable { mutableStateOf(0) }

    val highlightedTextLines = remember(lines, searchQuery) {
        buildHighlightedTextLines(lines, searchQuery)
    }

    val allMatches = remember(highlightedTextLines) {
        highlightedTextLines.flatMapIndexed { index, line ->
            line.matchPositions.map { index }
        }
    }

    val totalMatches = allMatches.size
    val foundIndex = allMatches.getOrNull(targetMatchIndex) ?: -1

    LaunchedEffect(foundIndex) {
        if (foundIndex >= 0) listState.scrollToItem(foundIndex)
    }

    Column(modifier = Modifier.padding(8.dp)) {
        if (isSearchCardVisible) {
            SearchBarCard(
                searchQuery = searchQuery,
                totalMatches = totalMatches,
                targetMatchIndex = targetMatchIndex,
                onQueryChange = {
                    targetMatchIndex = 0
                    searchQuery = it
                },
                onPrev = {
                    if (totalMatches > 0)
                        targetMatchIndex =
                            if (targetMatchIndex > 0) targetMatchIndex - 1 else totalMatches - 1
                },
                onNext = {
                    if (totalMatches > 0)
                        targetMatchIndex = (targetMatchIndex + 1) % totalMatches
                },
                onClose = onDismissSearch
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        HighlightedTextList(
            lines = highlightedTextLines,
            foundIndex = foundIndex,
            listState = listState
        )
    }
}

@Composable
private fun SearchBarCard(
    searchQuery: String,
    totalMatches: Int,
    targetMatchIndex: Int,
    onQueryChange: (String) -> Unit,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onClose: () -> Unit,
) {
    val containerBg = MaterialTheme.colorScheme.inputBackground
    val containerBorder = MaterialTheme.colorScheme.cardBorder
    val iconTint = MaterialTheme.colorScheme.textMuted

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(containerBg, RoundedCornerShape(12.dp))
            .border(1.dp, containerBorder, RoundedCornerShape(12.dp))
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = searchQuery,
            onValueChange = onQueryChange,
            placeholder = { Text("Find...", color = Silver, fontSize = 13.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Search,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(16.dp)
                )
            },
            singleLine = true,
            modifier = Modifier.weight(1f),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            )
        )

        Box(
            modifier = Modifier
                .border(1.dp, iconTint.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = if (totalMatches == 0) "No results" else "${targetMatchIndex + 1} of $totalMatches",
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = iconTint
            )
        }

        IconButton(onClick = onPrev, modifier = Modifier.size(32.dp)) {
            Icon(
                imageVector = Keyboard_arrow_up,
                contentDescription = "Previous match",
                tint = iconTint,
            )
        }
        IconButton(onClick = onNext, modifier = Modifier.size(32.dp)) {
            Icon(
                imageVector = Keyboard_arrow_down,
                contentDescription = "Next match",
                tint = iconTint,
            )
        }
        IconButton(onClick = onClose, modifier = Modifier.size(32.dp)) {
            Icon(
                imageVector = Close,
                contentDescription = "Close search",
                tint = iconTint,
            )
        }
    }
}

@Composable
fun HighlightedTextList(
    lines: List<HighlightedTextLine>,
    foundIndex: Int,
    listState: LazyListState,
) {
    LazyColumn(state = listState) {
        itemsIndexed(lines) { index, item ->
            val annotated = buildAnnotatedString {
                append(item.annotatedString.text)
                item.annotatedString.spanStyles.forEach { span ->
                    addStyle(
                        style = SpanStyle(
                            background = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                        ),
                        start = span.start,
                        end = span.end
                    )
                }
            }
            Text(
                text = annotated,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (index == foundIndex)
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        else Color.Transparent
                    )
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            )
        }
    }
}
