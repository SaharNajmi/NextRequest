package com.example.nextrequest.collection.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nextrequest.collection.domain.model.CollectionItem
import com.example.nextrequest.collection.presentation.model.CollectionUiState
import com.example.nextrequest.core.presentation.UiState
import com.example.nextrequest.core.presentation.color
import com.example.nextrequest.core.presentation.component.CustomSearchBar
import com.example.nextrequest.core.presentation.component.NotFoundMessage
import com.example.nextrequest.core.presentation.icons.Add
import com.example.nextrequest.core.presentation.icons.Arrow_back
import com.example.nextrequest.core.presentation.icons.Delete
import com.example.nextrequest.core.presentation.icons.Delete_sweep
import com.example.nextrequest.core.presentation.icons.Edit
import com.example.nextrequest.core.presentation.icons.Keyboard_arrow_down
import com.example.nextrequest.core.presentation.icons.Keyboard_arrow_right
import com.example.nextrequest.core.presentation.navigation.Screens.Companion.ROUTE_HOME_SCREEN
import com.example.nextrequest.core.presentation.theme.cardBackground
import com.example.nextrequest.core.presentation.theme.cardBorder
import com.example.nextrequest.core.presentation.theme.chipTintAlpha
import com.example.nextrequest.core.presentation.theme.focusedBorderColor
import com.example.nextrequest.core.presentation.theme.iconMuted
import com.example.nextrequest.core.presentation.theme.iconTint
import com.example.nextrequest.core.presentation.theme.textMuted
import com.example.nextrequest.history.domain.searchCollections
import com.sahar.nextrequest.R

@Composable
fun CollectionScreen(
    navController: NavController,
    viewModel: CollectionViewModel,
    onCollectionItemClick: (CollectionItem, String) -> Unit,
) {
    LaunchedEffect(Unit) {
        viewModel.getCollections()
    }
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    val callbacks = CollectionCallbacks(
        onCollectionItemClick = onCollectionItemClick,
        onRenameRequestClick = { id, newName -> viewModel.changeRequestName(id, newName) },
        onRenameCollectionClick = { collection -> viewModel.updateCollection(collection) },
        onCreateEmptyRequestClick = { collectionId ->
            viewModel.createAnEmptyRequest(collectionId)
        },
        onCreateNewCollectionClick = { viewModel.createNewCollection() },
        onHeaderClick = { collectionId -> viewModel.toggleExpanded(collectionId) },
        onDeleteCollectionClick = { collectionId -> viewModel.deleteCollection(collectionId) },
        onDeleteRequestClick = { requestId -> viewModel.deleteRequestItem(requestId) }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    awaitEachGesture {
                        awaitFirstDown().consume()
                        waitForUpOrCancellation()?.let { focusManager.clearFocus() }
                    }
                }
        ) {
            CollectionTopBar(navController, callbacks)

            Column(modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 12.dp)) {
                CustomSearchBar(stringResource(R.string.hint_search_collections), searchQuery) { searchQuery = it }

                when (uiState) {
                    is UiState.Loading -> Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }

                    is UiState.Error -> Text(
                        text = stringResource(R.string.msg_error, (uiState as UiState.Error).message),
                        modifier = Modifier.padding(16.dp),
                        color = Color.Red
                    )

                    is UiState.Success -> {
                        val collections = (uiState as UiState.Success<List<CollectionUiState>>).data
                        val filteredItems = searchCollections(collections, searchQuery)
                        when {
                            collections.isEmpty() -> EmptyCollectionMessage(callbacks)
                            filteredItems.isEmpty() -> NotFoundMessage(searchQuery)
                            else -> ExpandedCollectionItems(filteredItems, callbacks)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CollectionTopBar(navController: NavController, callbacks: CollectionCallbacks) {
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
                contentDescription = stringResource(R.string.cd_back),
                tint = MaterialTheme.colorScheme.textMuted,
                modifier = Modifier.size(20.dp)
            )
        }
        Text(
            text = stringResource(R.string.title_collections),
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium,
            fontSize = 18.sp,
            modifier = Modifier
                .weight(1f)
                .padding(start = 4.dp)
        )
        IconButton(
            onClick = { callbacks.onCreateNewCollectionClick() },
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Add,
                contentDescription = stringResource(R.string.cd_create_new_collection),
                tint = MaterialTheme.colorScheme.iconTint,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun EmptyCollectionMessage(callbacks: CollectionCallbacks) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.msg_create_collection_title),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = stringResource(R.string.msg_create_collection_subtitle),
            fontSize = 12.sp,
            fontWeight = FontWeight.Light,
            color = MaterialTheme.colorScheme.textMuted,
            modifier = Modifier.padding(top = 8.dp)
        )
        TextButton(
            modifier = Modifier
                .padding(top = 12.dp)
                .border(0.5.dp, MaterialTheme.colorScheme.cardBorder, RoundedCornerShape(8.dp)),
            onClick = { callbacks.onCreateNewCollectionClick() }
        ) {
            Text(
                stringResource(R.string.action_create_collection),
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun ExpandedCollectionItems(
    collections: List<CollectionUiState>,
    callbacks: CollectionCallbacks,
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        collections.forEach { collection ->
            val allRequests = collection.requestCollection.items
            item(key = collection.requestCollection.collectionId) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(0.5.dp, MaterialTheme.colorScheme.cardBorder, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.cardBackground)
                ) {
                    CollectionHeader(
                        modifier = Modifier.fillMaxWidth(),
                        header = collection.requestCollection.collectionName,
                        isExpanded = collection.isExpanded,
                        collection = collection,
                        callbacks = callbacks
                    )

                    if (allRequests.isNullOrEmpty()) {
                        AnimatedVisibility(
                            modifier = Modifier.fillMaxWidth(),
                            visible = collection.isExpanded
                        ) {
                            Column {
                                HorizontalDivider(
                                    thickness = 0.5.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant
                                )
                                AddARequestButton(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    collectionId = collection.requestCollection.collectionId,
                                    callbacks = callbacks
                                )
                            }
                        }
                    } else {
                        allRequests.forEach { item ->
                            AnimatedVisibility(
                                modifier = Modifier.fillMaxWidth(),
                                visible = collection.isExpanded
                            ) {
                                Column {
                                    HorizontalDivider(
                                        thickness = 0.5.dp,
                                        color = MaterialTheme.colorScheme.outlineVariant
                                    )
                                    CollectionItemView(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        collectionItem = item,
                                        collectionId = collection.requestCollection.collectionId,
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
}

@Composable
fun CollectionHeader(
    modifier: Modifier,
    header: String,
    isExpanded: Boolean,
    collection: CollectionUiState,
    callbacks: CollectionCallbacks,
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    var text by remember {
        mutableStateOf(TextFieldValue(header, TextRange(header.length)))
    }
    var isEditable by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .background(
                if (isExpanded) MaterialTheme.colorScheme.secondaryContainer
                else Color.Transparent
            )
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isExpanded) Keyboard_arrow_down else Keyboard_arrow_right,
            contentDescription = stringResource(R.string.cd_expand_collapse),
            tint = MaterialTheme.colorScheme.textMuted,
            modifier = Modifier
                .size(18.dp)
                .clickable {
                    focusManager.clearFocus()
                    callbacks.onHeaderClick(collection.requestCollection.collectionId)
                }
        )

        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            readOnly = !isEditable,
            maxLines = 1,
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .padding(start = 3.dp)

                .focusRequester(focusRequester)
                .onFocusChanged { focusState ->
                    if (!focusState.isFocused && isEditable) {
                        isEditable = false
                        val newName = text.text
                        if (collection.requestCollection.collectionName != newName) {
                            callbacks.onRenameCollectionClick(
                                collection.requestCollection.copy(collectionName = newName)
                            )
                        }
                    }
                },
            textStyle = TextStyle(
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            ),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (isEditable) MaterialTheme.colorScheme.focusedBorderColor else Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
            ),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                isEditable = false
                focusManager.clearFocus()
            })
        )

        Icon(
            imageVector = Add,
            contentDescription = stringResource(R.string.cd_add_new_request),
            modifier = Modifier
                .padding(start = 8.dp)
                .size(18.dp)
                .clickable {
                    focusManager.clearFocus()
                    callbacks.onCreateEmptyRequestClick(collection.requestCollection.collectionId)
                },
            tint = MaterialTheme.colorScheme.iconTint
        )

        Icon(
            imageVector = Edit,
            contentDescription = stringResource(R.string.cd_rename_collection),
            modifier = Modifier
                .padding(start = 8.dp)
                .size(18.dp)
                .clickable {
                    if (isEditable) {
                        isEditable = false
                        focusManager.clearFocus()
                    } else {
                        isEditable = true
                        focusRequester.requestFocus()
                    }
                },
            tint = MaterialTheme.colorScheme.iconTint
        )

        Icon(
            imageVector = Delete_sweep,
            contentDescription = stringResource(R.string.cd_delete_collection),
            modifier = Modifier
                .padding(start = 8.dp)
                .size(18.dp)
                .clickable {
                    focusManager.clearFocus()
                    callbacks.onDeleteCollectionClick(collection.requestCollection.collectionId)
                },
            tint = MaterialTheme.colorScheme.iconTint
        )
    }
}

@Composable
fun AddARequestButton(modifier: Modifier, collectionId: String, callbacks: CollectionCallbacks) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(
            stringResource(R.string.msg_collection_empty),
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.textMuted
        )
        TextButton(onClick = { callbacks.onCreateEmptyRequestClick(collectionId) }) {
            Text(
                stringResource(R.string.action_add_request),
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun CollectionItemView(
    modifier: Modifier,
    collectionItem: CollectionItem,
    collectionId: String,
    callbacks: CollectionCallbacks,
) {
    val strippedName = when (collectionItem) {
        is CollectionItem.Http -> {
            val prefix = "${collectionItem.request.httpMethod.name} "
            if (collectionItem.requestName.startsWith(prefix))
                collectionItem.requestName.removePrefix(prefix)
            else
                collectionItem.requestName
        }
        is CollectionItem.WebSocket ->
            if (collectionItem.requestName.startsWith("WebSocket "))
                collectionItem.requestName.removePrefix("WebSocket ")
            else
                collectionItem.requestName
    }
    val displayText =
        if (strippedName.length > 35) strippedName.take(35) + "..." else strippedName

    var text by remember {
        mutableStateOf(TextFieldValue(displayText, TextRange(displayText.length)))
    }
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    var isEditable by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    if (interactionSource.collectIsPressedAsState().value) {
        callbacks.onCollectionItemClick(collectionItem, collectionId)
    }

    Row(
        modifier = modifier.clickable {
            focusManager.clearFocus()
            callbacks.onCollectionItemClick(collectionItem, collectionId)
        },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        CollectionItemBadge(collectionItem)

        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            readOnly = !isEditable,
            maxLines = 1,
            interactionSource = interactionSource,
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .focusRequester(focusRequester)
                .combinedClickable(onClick = {
                    callbacks.onCollectionItemClick(collectionItem, collectionId)
                })
                .onFocusChanged { focusState ->
                    if (!focusState.isFocused && isEditable) {
                        isEditable = false
                        callbacks.onRenameRequestClick(collectionItem.requestId, text.text)
                    }
                },
            textStyle = TextStyle(
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground
            ),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (isEditable) MaterialTheme.colorScheme.focusedBorderColor else Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
            ),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                isEditable = false
                focusManager.clearFocus()
            })
        )

        Icon(
            imageVector = Edit,
            contentDescription = stringResource(R.string.cd_rename),
            modifier = Modifier
                .size(18.dp)
                .clickable {
                    if (isEditable) {
                        isEditable = false
                        focusManager.clearFocus()
                    } else {
                        isEditable = true
                        focusRequester.requestFocus()
                    }
                },
            tint = MaterialTheme.colorScheme.iconMuted
        )

        Icon(
            imageVector = Delete,
            contentDescription = stringResource(R.string.cd_delete),
            modifier = Modifier
                .size(18.dp)
                .clickable {
                    focusManager.clearFocus()
                    callbacks.onDeleteRequestClick(collectionItem.requestId)
                },
            tint = MaterialTheme.colorScheme.iconMuted
        )
    }
}

@Composable
private fun CollectionItemBadge(collectionItem: CollectionItem) {
    when (collectionItem) {
        is CollectionItem.Http -> Text(
            text = collectionItem.request.httpMethod.name,
            color = collectionItem.request.httpMethod.color,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(top = 1.dp)
                .background(
                    color = collectionItem.request.httpMethod.color.copy(alpha = MaterialTheme.colorScheme.chipTintAlpha),
                    shape = RoundedCornerShape(6.dp)
                )
                .padding(horizontal = 6.dp, vertical = 3.dp)
        )

        is CollectionItem.WebSocket -> Box(
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