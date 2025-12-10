package com.example.nextrequest.home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nextrequest.core.extensions.formatJson
import com.example.nextrequest.core.presentation.icons.Arrow_downward_alt
import com.example.nextrequest.core.presentation.icons.Arrow_upward_alt
import com.example.nextrequest.core.presentation.icons.ChromeClose
import com.example.nextrequest.core.presentation.icons.Search
import com.example.nextrequest.core.presentation.theme.Silver
import com.example.nextrequest.core.presentation.theme.focusedBorderColor
import com.example.nextrequest.core.presentation.theme.unfocusedBorderColor
import com.example.nextrequest.home.domain.HighlightedTextLine
import com.example.nextrequest.home.domain.buildHighlightedTextLines

@Composable
fun SearchFromContentText(
    contentText: String,
    isSearchVisible: Boolean,
    onDismissSearch: () -> Unit,
) {
    val formattedJson = remember(contentText) { contentText.formatJson() }
    val lines = remember(formattedJson) { formattedJson.lines() }
    val listState = rememberLazyListState()
    var searchQuery by remember { mutableStateOf("") }
    var targetMatchIndex by remember { mutableStateOf(0) }

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
        if (foundIndex >= 0) {
            listState.scrollToItem(foundIndex)
        }
    }

    Column(modifier = Modifier.padding(8.dp)) {
        SearchBar(
            isVisible = isSearchVisible,
            searchQuery = searchQuery,
            totalMatches = totalMatches,
            targetMatchIndex = targetMatchIndex,
            onQueryChange = {
                targetMatchIndex = 0
                searchQuery = it
            },
            onPrev = {
                if (totalMatches > 0) {
                    targetMatchIndex =
                        if (targetMatchIndex > 0) targetMatchIndex - 1 else totalMatches - 1
                }
            },
            onNext = {
                if (totalMatches > 0) {
                    targetMatchIndex = (targetMatchIndex + 1) % totalMatches
                }
            },
            onClose = onDismissSearch
        )

        Spacer(modifier = Modifier.height(4.dp))

        HighlightedTextList(
            lines = highlightedTextLines,
            foundIndex = foundIndex,
            listState = listState
        )
    }
}

@Composable
fun SearchBar(
    isVisible: Boolean,
    searchQuery: String,
    totalMatches: Int,
    targetMatchIndex: Int,
    onQueryChange: (String) -> Unit,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onClose: () -> Unit,
) {
    if (isVisible)
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(if(isSystemInDarkTheme()) Color.Transparent else MaterialTheme.colorScheme.onPrimary)
                .padding(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onQueryChange,
                placeholder = { Text("Find", color = Silver) },
                modifier = Modifier
                    .weight(3f),
                leadingIcon = {
                    Icon(
                        imageVector = Search,
                        contentDescription = "Search Icon",
                        tint = Silver
                    )
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    focusedBorderColor = MaterialTheme.colorScheme.focusedBorderColor,
                    unfocusedBorderColor = MaterialTheme.colorScheme.unfocusedBorderColor,
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = if (totalMatches == 0) "No results" else "${targetMatchIndex + 1} of $totalMatches",
                fontSize = 12.sp,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Arrow_upward_alt,
                contentDescription = "Previous match",
                modifier = Modifier.clickable { onPrev() }
            )

            Icon(
                imageVector = Arrow_downward_alt,
                contentDescription = "Next match",
                modifier = Modifier.clickable { onNext() }
            )

            Icon(
                imageVector = ChromeClose,
                contentDescription = "close search box",
                modifier = Modifier.clickable { onClose() }
            )
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

            val cleanAnnotatedString = buildAnnotatedString {
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
                text = cleanAnnotatedString,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (index == foundIndex)  MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent)
                    .padding(4.dp)
            )
        }
    }}
