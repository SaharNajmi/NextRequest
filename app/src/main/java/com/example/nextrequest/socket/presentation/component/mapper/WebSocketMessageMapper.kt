package com.example.nextrequest.socket.presentation.component.mapper

import com.example.nextrequest.socket.domain.repository.WebSocketMessage
import com.example.nextrequest.socket.presentation.component.model.WebSocketMessageUiModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


fun WebSocketMessage.toUi(): WebSocketMessageUiModel {
    val time = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(
        Date(this.timestamp)
    )
    return WebSocketMessageUiModel(this.text, this.isSentByUser, time)
}
