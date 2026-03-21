package com.example.nextrequest.history.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nextrequest.collection.presentation.model.CollectionEntry
import com.example.nextrequest.core.presentation.UiState
import com.example.nextrequest.core.presentation.color
import com.example.nextrequest.core.presentation.component.CustomSearchBar
import com.example.nextrequest.core.presentation.component.NotFoundMessage
import com.example.nextrequest.core.presentation.icons.Add
import com.example.nextrequest.core.presentation.icons.Arrow_back
import com.example.nextrequest.core.presentation.icons.Delete
import com.example.nextrequest.core.presentation.icons.Delete_sweep
import com.example.nextrequest.core.presentation.icons.Hourglass_empty
import com.example.nextrequest.core.presentation.icons.Keyboard_arrow_down
import com.example.nextrequest.core.presentation.icons.Keyboard_arrow_right
import com.example.nextrequest.core.presentation.navigation.Screens.Companion.ROUTE_HOME_SCREEN
import com.example.nextrequest.core.presentation.theme.cardBackground
import com.example.nextrequest.core.presentation.theme.cardBorder
import com.example.nextrequest.core.presentation.theme.chipTintAlpha
import com.example.nextrequest.core.presentation.theme.iconMuted
import com.example.nextrequest.core.presentation.theme.iconTint
import com.example.nextrequest.core.presentation.theme.textMuted
import com.example.nextrequest.history.domain.model.HistoryItem
import com.example.nextrequest.history.domain.searchHistories
import com.example.nextrequest.history.presentation.component.SaveToCollectionDialog
import com.example.nextrequest.history.presentation.model.ExpandableHistoryItem
import com.example.nextrequest.history.presentation.model.HistoryEntry
import com.sahar.nextrequest.R
import kotlinx.coroutines.launch

@Composable
fun HistoryScreen(
    navController: NavController,
    viewModel: HistoryViewModel,
    onHistoryItemClick: (HistoryItem) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) { viewModel.getHistories() }

    val callbacks = HistoryCallbacks(
        onHistoryItemClick = onHistoryItemClick,
        onAddHistoryToCollection = { history, collectionId ->
            viewModel.addHistoryToCollection(history, collectionId)
            scope.launch { snackbarHostState.showSnackbar("Added to collection") }
        },
        onAddHistoriesToCollection = { histories, collectionId ->
            viewModel.addHistoriesToCollection(histories, collectionId)
            scope.launch { snackbarHostState.showSnackbar("Added to collection") }
        },
        onHeaderClick = viewModel::toggleExpanded,
        onDeleteHistoriesClick = viewModel::deleteHistories,
        onDeleteHistoryClick = viewModel::deleteHistory,
        onCreateNewCollectionClick = viewModel::createNewCollection,
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding(),
        contentAlignment = Alignment.BottomCenter
    ) {
        SnackbarHost(hostState = snackbarHostState) { data ->
            HistorySnackbar(message = data.visuals.message)
        }

        Column(modifier = Modifier.fillMaxSize()) {
            HistoryTopBar(navController)

            when (uiState) {
                UiState.Loading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

                is UiState.Error -> Text(
                    text = "Error: ${(uiState as UiState.Error).message}",
                    modifier = Modifier.padding(16.dp),
                    color = Color.Red
                )

                is UiState.Success -> HistoryScreenContent(
                    historyUiModel = (uiState as UiState.Success<HistoryUiModel>).data,
                    callbacks = callbacks
                )
            }
        }
    }
}

@Composable
private fun HistorySnackbar(message: String) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.cardBackground)
            .border(0.5.dp, MaterialTheme.colorScheme.cardBorder, RoundedCornerShape(10.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(40.dp)
                .background(MaterialTheme.colorScheme.primary)
        )
        Text(
            text = message,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun HistoryTopBar(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
                navController.navigate(ROUTE_HOME_SCREEN) {
                    popUpTo(ROUTE_HOME_SCREEN) { inclusive = false }
                    launchSingleTop = true
                }
            },
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Arrow_back,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.textMuted,
                modifier = Modifier.size(20.dp)
            )
        }
        Text(
            text = "History",
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium,
            fontSize = 18.sp,
            modifier = Modifier.fillMaxWidth().padding(start = 4.dp)
        )
    }
}

@Composable
fun HistoryScreenContent(
    historyUiModel: HistoryUiModel,
    callbacks: HistoryCallbacks,
) {
    val expandedState = historyUiModel.expandedStates
    val histories = historyUiModel.historyEntries
    val collectionNames = historyUiModel.collectionNames
    var searchQuery by remember { mutableStateOf("") }
    val filteredHistories = searchHistories(histories, searchQuery)

    Column(modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 12.dp)) {
        CustomSearchBar("Search requests...", searchQuery) { searchQuery = it }

        when {
            filteredHistories.isEmpty() && searchQuery.isEmpty() -> EmptyHistoryMessage()
            filteredHistories.isEmpty() -> NotFoundMessage(searchQuery)
            else -> ExpandedHistoryList(filteredHistories, collectionNames, expandedState, callbacks)
        }
    }
}

@Composable
private fun EmptyHistoryMessage() {
    Row(
        modifier = Modifier
            .padding(top = 24.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            Hourglass_empty,
            contentDescription = "empty list",
            tint = MaterialTheme.colorScheme.textMuted,
            modifier = Modifier.padding(end = 4.dp)
        )
        Text(text = "History is empty", color = MaterialTheme.colorScheme.textMuted)
    }
}

@Composable
private fun ExpandedHistoryList(
    historyEntries: List<HistoryEntry>,
    collectionEntries: Set<CollectionEntry>,
    expandedState: List<ExpandableHistoryItem>,
    callbacks: HistoryCallbacks,
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        historyEntries.forEach { historyEntry ->
            val isExpanded = expandedState
                .firstOrNull { it.dateCreated == historyEntry.dateCreated }
                ?.isExpanded ?: false

            item(key = historyEntry.dateCreated) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(0.5.dp, MaterialTheme.colorScheme.cardBorder, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.cardBackground)
                ) {
                    HistoryHeader(
                        modifier = Modifier.fillMaxWidth(),
                        header = historyEntry.dateCreated,
                        collectionEntries = collectionEntries,
                        isExpanded = isExpanded,
                        histories = historyEntry.histories,
                        callbacks = callbacks
                    )
                    historyEntry.histories.forEach { item ->
                        AnimatedVisibility(
                            modifier = Modifier.fillMaxWidth(),
                            visible = isExpanded
                        ) {
                            Column {
                                HorizontalDivider(
                                    thickness = 0.5.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant
                                )
                                HistoryItemView(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    item = item,
                                    collectionNames = collectionEntries,
                                    callbacks = callbacks
                                )
                            }
                        }
                    }
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
    histories: List<HistoryItem>,
    callbacks: HistoryCallbacks,
) {
    Row(
        modifier = modifier
            .background(
                if (isExpanded) MaterialTheme.colorScheme.secondaryContainer
                else Color.Transparent
            )
            .clickable { callbacks.onHeaderClick(header) }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isExpanded) Keyboard_arrow_down else Keyboard_arrow_right,
            contentDescription = "expand/collapse",
            tint = MaterialTheme.colorScheme.textMuted,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = header,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp),
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp
        )
        HeaderActions(
            collectionEntries = collectionEntries,
            histories = histories,
            callbacks = callbacks
        )
    }
}

@Composable
private fun HeaderActions(
    collectionEntries: Set<CollectionEntry>,
    histories: List<HistoryItem>,
    callbacks: HistoryCallbacks,
) {
    var showCollectionDialog by remember { mutableStateOf(false) }

    Icon(
        imageVector = Add,
        contentDescription = "add requests to collections",
        modifier = Modifier
            .size(18.dp)
            .clickable { showCollectionDialog = true },
        tint = MaterialTheme.colorScheme.iconTint
    )
    Icon(
        imageVector = Delete_sweep,
        contentDescription = "delete list by date",
        modifier = Modifier
            .padding(start = 8.dp)
            .size(18.dp)
            .clickable { callbacks.onDeleteHistoriesClick(histories) },
        tint = MaterialTheme.colorScheme.iconTint
    )

    if (showCollectionDialog) {
        SaveToCollectionDialog(
            items = collectionEntries,
            onDismiss = { showCollectionDialog = false },
            onSave = { callbacks.onAddHistoriesToCollection(histories, it) },
            onAddNewCollection = callbacks.onCreateNewCollectionClick
        )
    }
}

@Composable
private fun HistoryItemView(
    modifier: Modifier,
    item: HistoryItem,
    collectionNames: Set<CollectionEntry>,
    callbacks: HistoryCallbacks,
) {
    Row(
        modifier = modifier.clickable { callbacks.onHistoryItemClick(item) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        HistoryItemBadge(item)

        Text(
            text = when (item) {
                is HistoryItem.Http -> item.request.requestUrl
                is HistoryItem.WebSocket -> item.request.url
            },
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f)
        )

        HistoryItemActions(
            item = item,
            collectionNames = collectionNames,
            callbacks = callbacks
        )
    }
}

@Composable
private fun HistoryItemActions(
    item: HistoryItem,
    collectionNames: Set<CollectionEntry>,
    callbacks: HistoryCallbacks,
) {
    var showCollectionDialog by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = Add,
            contentDescription = "add to collection",
            modifier = Modifier
                .size(18.dp)
                .clickable { showCollectionDialog = true },
            tint = MaterialTheme.colorScheme.iconMuted
        )
        Icon(
            imageVector = Delete,
            contentDescription = "delete",
            modifier = Modifier
                .size(18.dp)
                .clickable { callbacks.onDeleteHistoryClick(item) },
            tint = MaterialTheme.colorScheme.iconMuted
        )
    }

    if (showCollectionDialog) {
        SaveToCollectionDialog(
            items = collectionNames,
            onDismiss = { showCollectionDialog = false },
            onSave = { callbacks.onAddHistoryToCollection(item, it) },
            onAddNewCollection = callbacks.onCreateNewCollectionClick
        )
    }
}

@Composable
private fun HistoryItemBadge(item: HistoryItem) {
    when (item) {
        is HistoryItem.Http -> Text(
            text = item.request.httpMethod.name,
            color = item.request.httpMethod.color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(top = 1.dp)
                .background(
                    color = item.request.httpMethod.color.copy(alpha = MaterialTheme.colorScheme.chipTintAlpha),
                    shape = RoundedCornerShape(6.dp)
                )
                .padding(horizontal = 6.dp, vertical = 3.dp)
        )
        is HistoryItem.WebSocket -> Box(
            modifier = Modifier
                .padding(top = 1.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = MaterialTheme.colorScheme.chipTintAlpha),
                    shape = RoundedCornerShape(6.dp)
                )
                .padding(horizontal = 6.dp, vertical = 4.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.websocket),
                contentDescription = "websocket",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(11.dp)
            )
        }
    }
}
