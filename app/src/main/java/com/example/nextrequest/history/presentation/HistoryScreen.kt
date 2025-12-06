package com.example.nextrequest.history.presentation

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nextrequest.collection.presentation.model.CollectionEntry
import com.example.nextrequest.history.presentation.model.ExpandableHistoryItem
import com.example.nextrequest.history.domain.model.History
import com.example.nextrequest.history.presentation.model.HistoryEntry
import com.example.nextrequest.core.presentation.component.CustomSearchBar
import com.example.nextrequest.core.presentation.component.CustomToolbar
import com.example.nextrequest.core.presentation.component.NotFoundMessage
import com.example.nextrequest.history.presentation.component.SaveToCollectionDialog
import com.example.nextrequest.history.domain.searchHistories
import com.example.nextrequest.core.presentation.theme.Blue
import com.example.nextrequest.core.presentation.theme.LightGreen
import com.example.nextrequest.core.presentation.icons.Add
import com.example.nextrequest.core.presentation.icons.Delete
import com.example.nextrequest.core.presentation.icons.Delete_sweep
import com.example.nextrequest.core.presentation.icons.Keyboard_arrow_down
import com.example.nextrequest.core.presentation.icons.Keyboard_arrow_right

@Composable
fun HistoryScreen(
    navController: NavController,
    viewModel: HistoryViewModel,
    onHistoryItemClick: (Int) -> Unit,
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.getHistories()
        viewModel.getCollections()
    }
    val expandedState: State<List<ExpandableHistoryItem>> =
        viewModel.expandedStates.collectAsState()
    val histories by viewModel.historyEntry.collectAsState()
    val collectionNames by viewModel.collectionNames.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val filteredHistories: List<HistoryEntry> = searchHistories(
        histories, searchQuery
    )

    val callbacks = HistoryCallbacks(
        onHistoryItemClick,
        onAddHistoryToCollection = { history, collectionId ->
            viewModel.addHistoryRequestToCollection(history, collectionId)
            Toast.makeText(
                context,
                "successfully added to collections",
                Toast.LENGTH_SHORT
            ).show()
        },
        onAddHistoriesToCollection = { histories, collectionId ->
            viewModel.addHistoryRequestsToCollection(histories, collectionId)
            Toast.makeText(
                context,
                "successfully added to collections",
                Toast.LENGTH_SHORT
            ).show()
        },
        onHeaderClick = { date ->
            viewModel.toggleExpanded(date)
        },
        onDeleteHistoriesClick = { historyIds ->
            viewModel.deleteHistoriesRequest(historyIds)
        },
        onDeleteHistoryClick = { historyId ->
            viewModel.deleteHistoryRequest(historyId)
        }
    )
    Column(modifier = Modifier.padding(12.dp)) {
        Spacer(modifier = Modifier.height(24.dp))
        CustomToolbar("History", navController)
        Spacer(Modifier.height(8.dp))
        CustomSearchBar("Search by url", searchQuery) { searchQuery = it }
        if (filteredHistories.isEmpty()) {
            NotFoundMessage(searchQuery)
        } else {
            ExpandedHistoryItem(
                filteredHistories,
                collectionNames,
                expandedState,
                callbacks
            )
        }
    }
}

@Composable
private fun ExpandedHistoryItem(
    historyEntries: List<HistoryEntry>,
    collectionEntries: Set<CollectionEntry>,
    expandedState: State<List<ExpandableHistoryItem>>,
    callbacks: HistoryCallbacks,
) {
    historyEntries.forEach { historyEntry ->
        LazyColumn {
            item {
                HistoryHeader(
                    Modifier.padding(vertical = 8.dp),
                    historyEntry.dateCreated,
                    collectionEntries,
                    expandedState.value.firstOrNull { it.dateCreated == historyEntry.dateCreated }?.isExpanded
                        ?: false,
                    historyEntry.histories,
                    callbacks
                )
            }
            items(historyEntry.histories.size) { index ->
                AnimatedVisibility(
                    modifier = Modifier
                        .fillMaxWidth(),
                    visible = expandedState.value.firstOrNull { it.dateCreated == historyEntry.dateCreated }?.isExpanded
                        ?: false
                ) {
                    HistoryItem(
                        Modifier
                            .padding(top = 8.dp, bottom = 8.dp, start = 12.dp),
                        historyEntry.histories,
                        collectionEntries,
                        index,
                        callbacks
                    )
                }
            }
        }
    }
}

@Composable
fun HistoryHeader(
    modifier: Modifier,
    header: String,
    collectionEntries: Set<CollectionEntry>,
    isExpanded: Boolean,
    histories: List<History>,
    callbacks: HistoryCallbacks,
) {
    val expandedIcon =
        if (isExpanded) Keyboard_arrow_down else Keyboard_arrow_right
    var showDropdown by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .clickable { callbacks.onHeaderClick(header) }
            .background(if (isExpanded) LightGreen else Color.Transparent)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically) {

        Icon(
            imageVector = expandedIcon,
            contentDescription = "isExpandedIcon"
        )

        Text(
            text = header,
            modifier = Modifier.padding(start = 8.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = Add, contentDescription = "add requests to collections",
            modifier = Modifier
                .size(20.dp)
                .clickable {
                    showDropdown = true
                }, tint = Blue
        )
        Icon(
            Delete_sweep, contentDescription = "delete list by date",
            Modifier
                .padding(horizontal = 4.dp)
                .clickable {
                    callbacks.onDeleteHistoriesClick(histories.map { it.id })
                },
            tint = Blue
        )
        if (showDropdown) {
            SaveToCollectionDialog(
                collectionEntries,
                { showDropdown = false }) { collectionId ->
                callbacks.onAddHistoriesToCollection(histories, collectionId)
            }
        }
    }
}

@Composable
private fun HistoryItem(
    modifier: Modifier,
    items: List<History>,
    collectionNames: Set<CollectionEntry>,
    index: Int,
    callbacks: HistoryCallbacks,
) {
    var showDropdown by remember { mutableStateOf(false) }
    Row(
        modifier = modifier
            .clickable {
                callbacks.onHistoryItemClick(items[index].id)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = items[index].httpMethod.name,
            color = items[index].httpMethod.color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(end = 8.dp)
        )

        Text(
            text = items[index].requestUrl.toString(),
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f)
                .padding(end = 4.dp)
        )
        Icon(
            imageVector = Add, contentDescription = "add request to collections",
            modifier = Modifier
                .size(20.dp)
                .clickable {
                    showDropdown = true
                }
        )
        Icon(
            Delete, contentDescription = "delete",
            Modifier
                .padding(horizontal = 4.dp)
                .size(20.dp)
                .clickable {
                    callbacks.onDeleteHistoryClick(items[index].id)
                }
        )
        if (showDropdown) {
            SaveToCollectionDialog(
                collectionNames,
                { showDropdown = false }) { collectionId ->
                callbacks.onAddHistoryToCollection(items[index], collectionId)
            }
        }
    }
}



