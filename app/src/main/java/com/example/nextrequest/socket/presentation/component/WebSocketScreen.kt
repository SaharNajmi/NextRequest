package com.example.nextrequest.socket.presentation.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.sahar.nextrequest.R
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nextrequest.core.presentation.UiState
import com.example.nextrequest.core.presentation.icons.ArrowDown
import com.example.nextrequest.core.presentation.icons.ArrowDropDown
import com.example.nextrequest.core.presentation.icons.ArrowDropUp
import com.example.nextrequest.core.presentation.icons.ArrowUp
import com.example.nextrequest.core.presentation.icons.Arrow_back
import com.example.nextrequest.core.presentation.icons.Send
import com.example.nextrequest.core.presentation.navigation.Screens.Companion.ROUTE_HOME_SCREEN
import com.example.nextrequest.core.presentation.theme.cardBackground
import com.example.nextrequest.core.presentation.theme.cardBorder
import com.example.nextrequest.core.presentation.theme.chipTintAlpha
import com.example.nextrequest.core.presentation.theme.inputFieldColors
import com.example.nextrequest.core.presentation.theme.textMuted
import com.example.nextrequest.socket.presentation.component.model.MessageUiModel
import com.example.nextrequest.socket.presentation.component.model.WebSocketUiModel

@Composable
fun WebSocketScreen(
    requestId: Int? = null,
    source: String?,
    navController: NavController,
    viewModel: WebSocketViewModel,
) {
    val uiState by viewModel.uiState.collectAsState()
    var messageText by remember { mutableStateOf("") }
    var requestUrl by remember { mutableStateOf("") }

    LaunchedEffect(requestId, source) {
        if (requestId != null && source != null) {
            viewModel.loadRequest(requestId, source)
        }
    }

    LaunchedEffect(uiState) {
        val url = (uiState as? UiState.Success<WebSocketUiModel>)?.data?.url
        if (!url.isNullOrEmpty()) requestUrl = url
    }

    val isConnected = (uiState as? UiState.Success<WebSocketUiModel>)?.data?.isConnected ?: false

    val callbacks = WebSocketCallbacks(
        onConnectClick = { viewModel.connect(requestUrl) },
        onDisconnectClick = { viewModel.disconnect() },
        onSendMessageClick = { viewModel.sendMessage(messageText) },
        onHideMessages = { viewModel.hideMessages() },
        onShowHiddenMessages = { viewModel.showHiddenMessages() }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        WebSocketTopBar(navController = navController)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RequestLine(
                    callbacks = callbacks,
                    isConnected = isConnected,
                    requestUrl = requestUrl,
                    onRequestUrlChanged = { requestUrl = it },
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(
                    modifier = Modifier
                        .weight(1f)
                        .border(0.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)),
                    value = messageText,
                    onValueChange = { messageText = it },
                    colors = inputFieldColors(),
                    placeholder = {
                        Text(stringResource(R.string.hint_type_message), color = MaterialTheme.colorScheme.textMuted, fontSize = 13.sp)
                    },
                    maxLines = 5,
                    shape = RoundedCornerShape(12.dp)
                )
                IconButton(
                    onClick = callbacks.onSendMessageClick,
                    enabled = isConnected && messageText.isNotBlank(),
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Send,
                        contentDescription = stringResource(R.string.cd_send_message),
                        tint = if (isConnected && messageText.isNotBlank())
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.textMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            when (val state = uiState) {
                is UiState.Error -> {
                    Text(
                        text = stringResource(R.string.msg_error, state.message),
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.error
                    )
                }

                UiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is UiState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .border(0.5.dp, MaterialTheme.colorScheme.cardBorder, RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.cardBackground)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.label_messages),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.textMuted,
                                modifier = Modifier.weight(1f)
                            )
                            TextButton(
                                onClick = callbacks.onHideMessages,
                                enabled = state.data.visibleMessages.isNotEmpty()
                            ) {
                                Text(stringResource(R.string.action_hide), fontSize = 12.sp)
                            }
                        }
                        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            items(state.data.visibleMessages) {
                                MessageItem(it)
                            }
                            if (state.data.hiddenMessages.isNotEmpty()) {
                                item {
                                    HiddenMessagesItem(
                                        count = state.data.hiddenMessages.size,
                                        onShowClick = callbacks.onShowHiddenMessages
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
private fun WebSocketTopBar(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 12.dp),
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
            text = stringResource(R.string.title_websocket),
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Black,
            fontSize = 20.sp,
            modifier = Modifier
                .weight(1f)
                .padding(start = 4.dp)
        )
    }
}

@Composable
fun MessageItem(message: MessageUiModel) {
    var expandedText by remember { mutableStateOf(false) }
    val maxLines = if (expandedText) Int.MAX_VALUE else 1
    var isOverflowing by remember { mutableStateOf(false) }
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
        ) {
            if (message.isSentByUser) {
                Icon(
                    imageVector = ArrowUp,
                    contentDescription = "sender",
                    modifier = Modifier
                        .size(16.dp)
                        .background(
                            MaterialTheme.colorScheme.primaryContainer,
                            RoundedCornerShape(4.dp)
                        ),
                    tint = MaterialTheme.colorScheme.primary
                )
            } else {
                Icon(
                    imageVector = ArrowDown,
                    contentDescription = "receiver",
                    modifier = Modifier
                        .size(16.dp)
                        .background(
                            MaterialTheme.colorScheme.secondaryContainer,
                            RoundedCornerShape(4.dp)
                        ),
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
            Text(
                message.text,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 6.dp, end = 4.dp),
                maxLines = maxLines,
                overflow = TextOverflow.Ellipsis,
                fontSize = 12.sp,
                onTextLayout = { textLayoutResult ->
                    if (!expandedText) {
                        isOverflowing = textLayoutResult.hasVisualOverflow
                    }
                }
            )
            Text(
                message.timestamp,
                fontSize = 12.sp,
                fontWeight = FontWeight.Light,
                color = MaterialTheme.colorScheme.textMuted
            )
            Icon(
                imageVector = if (expandedText) ArrowDropUp else ArrowDropDown,
                contentDescription = "expanded",
                modifier = Modifier
                    .padding(start = 2.dp)
                    .clickable(enabled = isOverflowing || expandedText) {
                        expandedText = !expandedText
                    }
                    .alpha(if (isOverflowing || expandedText) 1f else 0f)
            )
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)
    }
}

@Composable
fun HiddenMessagesItem(count: Int, onShowClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.msg_messages_hidden, count),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.textMuted,
            modifier = Modifier.weight(1f)
        )
        TextButton(onClick = onShowClick) {
            Text(stringResource(R.string.action_show), fontSize = 12.sp)
        }
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)
}

@Composable
fun RequestLine(
    callbacks: WebSocketCallbacks,
    isConnected: Boolean,
    requestUrl: String,
    onRequestUrlChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        TextField(
            value = requestUrl,
            onValueChange = onRequestUrlChanged,
            maxLines = 4,
            placeholder = { Text(stringResource(R.string.hint_enter_url_ws), color = MaterialTheme.colorScheme.textMuted, fontSize = 13.sp) },
            colors = inputFieldColors(),
            modifier = Modifier
                .weight(1f)
                .border(0.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(10.dp),
            enabled = !isConnected
        )

        Surface(
            onClick = if (isConnected) callbacks.onDisconnectClick else callbacks.onConnectClick,
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(
                1.5.dp,
                if (isConnected) MaterialTheme.colorScheme.error.copy(alpha = 0.4f)
                else MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
            ),
            color = if (isConnected)
                MaterialTheme.colorScheme.error.copy(alpha = MaterialTheme.colorScheme.chipTintAlpha)
            else
                MaterialTheme.colorScheme.primary.copy(alpha = MaterialTheme.colorScheme.chipTintAlpha)
        ) {
            Text(
                text = if (isConnected) stringResource(R.string.action_disconnect) else stringResource(R.string.action_connect),
                color = if (isConnected) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
            )
        }
    }
}
