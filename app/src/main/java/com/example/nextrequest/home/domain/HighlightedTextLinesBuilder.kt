package com.example.nextrequest.home.domain

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

fun buildHighlightedTextLines(
    lines: List<String>,
    searchQuery: String,
): List<HighlightedTextLine> {
    return lines.map { line ->
        val lowerLine = line.lowercase()
        val lowerTerm = searchQuery.lowercase()
        var currentIndex = 0
        val matchPositions = mutableListOf<Int>()

        val annotated = buildAnnotatedString {
            if (lowerTerm.isEmpty()) {
                append(line)
            } else {
                while (currentIndex < line.length) {
                    val matchIndex = lowerLine.indexOf(lowerTerm, currentIndex)
                    if (matchIndex == -1) {
                        append(line.substring(currentIndex))
                        break
                    }

                    append(line.substring(currentIndex, matchIndex))

                    withStyle(
                        style = SpanStyle(
                            background = Color.Yellow,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append(line.substring(matchIndex, matchIndex + searchQuery.length))
                    }

                    matchPositions.add(matchIndex)
                    currentIndex = matchIndex + searchQuery.length
                }
            }
        }

        HighlightedTextLine(annotated, matchPositions)
    }
}