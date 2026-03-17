package com.example.nextrequest.socket.presentation.component

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nextrequest.core.presentation.UiState
import com.example.nextrequest.core.presentation.component.CustomToolbar
import com.example.nextrequest.core.presentation.icons.ArrowDown
import com.example.nextrequest.core.presentation.icons.ArrowDropDown
import com.example.nextrequest.core.presentation.icons.ArrowDropUp
import com.example.nextrequest.core.presentation.icons.ArrowUp
import com.example.nextrequest.core.presentation.theme.Silver
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

    val callbacks = WebSocketCallbacks(
        onConnectClick = {
            viewModel.connect(requestUrl)
        },
        onDisconnectClick = {
            viewModel.disconnect()
        },
        onSendMessageClick = {
            viewModel.sendMessage(messageText)
        })

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Column(modifier = Modifier.padding(horizontal = 12.dp)) {
                Spacer(modifier = Modifier.height(36.dp))
                CustomToolbar("WebSocket", navController)
            }
        }
    ) { padding ->
        val isConnected =
            (uiState as? UiState.Success<WebSocketUiModel>)?.data?.isConnected ?: false

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(vertical = 8.dp, horizontal = 12.dp)
        ) {
            RequestLine(
                callbacks = callbacks,
                isConnected = isConnected,
                requestUrl = requestUrl,
                onRequestUrlChanged = {
                    requestUrl = it
                }
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                TextField(
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .weight(1f),
                    value = messageText,
                    onValueChange = { messageText = it },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    placeholder = { Text("Type a message", color = Silver) },
                    maxLines = 5
                )
                Button(
                    shape = RoundedCornerShape(4.dp),
                    onClick = {
                        viewModel.sendMessage(messageText)
                    },
                    enabled = isConnected && messageText.isNotBlank()
                ) {
                    Text("Send")
                }
            }
            when (val state = uiState) {
                is UiState.Error -> {
                    Text(
                        text = "Error: ${state.message}",
                        modifier = Modifier.padding(16.dp), color = Color.Red
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            shape = RoundedCornerShape(4.dp),
                            onClick = { viewModel.hideMessages() },
                            enabled = state.data.visibleMessages.isNotEmpty()
                        ) {
                            Text("Hide Messages")
                        }
                    }
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        items(state.data.visibleMessages) {
                            MessageItem(it)
                        }
                        if (state.data.hiddenMessages.isNotEmpty()) {
                            item {
                                HiddenMessagesItem(
                                    count = state.data.hiddenMessages.size,
                                    onRestore = { viewModel.restoreHiddenMessages() }
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
fun MessageItem(message: MessageUiModel) {
    var expandedText by remember { mutableStateOf(false) }
    val maxLines = if (expandedText) Int.MAX_VALUE else 1
    var isOverflowing by remember { mutableStateOf(false) }
    Column() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
        ) {
            if (message.isSentByUser) {
                Icon(
                    imageVector = ArrowUp, contentDescription = "sender",
                    Modifier.background(
                        MaterialTheme.colorScheme.primaryContainer
                    ), tint = MaterialTheme.colorScheme.primary
                )
            } else {
                Icon(
                    imageVector = ArrowDown, contentDescription = "receiver",
                    Modifier
                        .background(
                            MaterialTheme.colorScheme.secondaryContainer
                        ), tint = MaterialTheme.colorScheme.secondary
                )
            }
            Text(
                message.text,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp, end = 4.dp),
                maxLines = maxLines,
                overflow = TextOverflow.Ellipsis,
                fontSize = 12.sp, onTextLayout = { textLayoutResult ->
                    if (!expandedText) {
                        isOverflowing = textLayoutResult.hasVisualOverflow
                    }
                }
            )
            Text(message.timestamp, fontSize = 12.sp, fontWeight = FontWeight.Light)
            Icon(
                imageVector = if (expandedText) ArrowDropUp else ArrowDropDown,
                contentDescription = "expanded",
                modifier = Modifier
                    .padding(start = 2.dp)
                    .clickable(enabled = isOverflowing || expandedText) {
                        expandedText = !expandedText
                    }
                    .alpha(if (isOverflowing || expandedText) 1f else 0f))
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outline, thickness = 0.5.dp)
    }
}

@Composable
fun HiddenMessagesItem(count: Int, onRestore: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "$count messages hidden",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        TextButton(onClick = onRestore) {
            Text("Restore", fontSize = 12.sp)
        }
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outline, thickness = 0.5.dp)
}

@Composable
fun RequestLine(
    callbacks: WebSocketCallbacks,
    isConnected: Boolean,
    requestUrl: String,
    onRequestUrlChanged: (String) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        TextField(
            value = requestUrl,
            onValueChange = {
                onRequestUrlChanged(it)
            },
            maxLines = 4,
            placeholder = { Text("Enter URL", color = Silver) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            modifier = Modifier
                .padding(end = 4.dp)
                .weight(1f)
                .border(
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(0.5.dp, color = MaterialTheme.colorScheme.primary)
                ),
            enabled = !isConnected
        )
        Button(
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isConnected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
            ),
            onClick = {
                if (isConnected) callbacks.onDisconnectClick() else callbacks.onConnectClick()
            }) {
            if (isConnected) {
                Text(
                    text = "Disconnect",
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Text(
                    text = "connect",
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}
