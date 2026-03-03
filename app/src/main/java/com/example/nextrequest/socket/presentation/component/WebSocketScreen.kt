package com.example.nextrequest.socket.presentation.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nextrequest.core.presentation.component.CustomToolbar
import com.example.nextrequest.core.presentation.icons.ArrowDown
import com.example.nextrequest.core.presentation.icons.ArrowUp
import com.example.nextrequest.core.presentation.theme.Silver
import com.example.nextrequest.socket.presentation.component.model.MessageUiModel

@Composable
fun WebSocketScreen(navController: NavController, viewModel: WebSocketViewModel) {
    val messages by viewModel.messages.collectAsState()
    val isConnected by viewModel.isConnected.collectAsState(false)
    var messageText by remember { mutableStateOf("") }
    var requestUrl by remember { mutableStateOf("") }
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
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
                    .padding(vertical = 8.dp, horizontal = 24.dp)
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 24.dp)
            ) {
                items(messages) {
                    MessageItem(it)
                }
            }
        }
    }
}

@Composable
fun MessageItem(message: MessageUiModel) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp, horizontal = 8.dp),
        verticalAlignment =Alignment.CenterVertically ) {
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
                Modifier.background(
                    MaterialTheme.colorScheme.secondaryContainer
                ), tint = MaterialTheme.colorScheme.secondary
            )
        }
        Spacer(Modifier.width(8.dp))
        Text(message.text)
        Spacer(modifier = Modifier.weight(1f))
        Text(message.timestamp, fontSize = 12.sp, fontWeight = FontWeight.Light)

    }
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
            .padding(vertical = 8.dp, horizontal = 24.dp)
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
