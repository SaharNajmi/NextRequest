package com.example.nextrequest.socket.presentation.component.mapper

import com.example.nextrequest.socket.domain.repository.WebSocketMessage
import com.example.nextrequest.socket.presentation.component.model.MessageUiModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


fun WebSocketMessage.toUi(): MessageUiModel {
    val time = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(
        Date(this.timestamp)
    )
    return MessageUiModel(this.text, this.isSentByUser, time)
}

fun MessageUiModel.toWebSocket(): WebSocketMessage {
    val sdf = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())
    val timestampLong = try {
        sdf.parse(this.timestamp)?.time ?: System.currentTimeMillis()
    } catch (e: Exception) {
        System.currentTimeMillis()
    }
    return WebSocketMessage(this.text, this.isSentByUser, timestampLong)
}
