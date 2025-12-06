package com.example.nextrequest.home.domain

import androidx.compose.ui.text.AnnotatedString

data class HighlightedTextLine(
    val annotatedString: AnnotatedString,
    val matchPositions: List<Int>
)