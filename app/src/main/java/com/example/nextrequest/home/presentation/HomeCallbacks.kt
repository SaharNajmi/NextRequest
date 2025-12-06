package com.example.nextrequest.home.presentation

import com.example.nextrequest.core.models.HttpMethod

data class HomeCallbacks(
    val onSendRequestClick: () -> Unit,
    val onBodyChanged: (String) -> Unit,
    val onAddHeader: (String, String) -> Unit,
    val onRemoveHeader: (String, String) -> Unit,
    val onAddParameter: (String, String) -> Unit,
    val onRemoveParameter: (String, String) -> Unit,
    val onHttpMethodChanged: (HttpMethod) -> Unit,
    val onRequestUrlChanged: (String) -> Unit,
    val onClearDataClick: () -> Unit,
    val onNavigateToHistory: () -> Unit,
    val onNavigateToCollection: () -> Unit,
    val onCopyClick: () -> Unit,
)