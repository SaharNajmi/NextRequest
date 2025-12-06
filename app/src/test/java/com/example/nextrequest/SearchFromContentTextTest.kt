package com.example.nextrequest

import com.example.nextrequest.home.domain.buildHighlightedTextLines
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import org.junit.Test

class SearchFromContentTextTest() {
    @Test
    fun `returns original line if search query is empty`() {
        val lines = listOf("Hello World", "Another line")
        val result = buildHighlightedTextLines(lines, "")
        result[0].annotatedString.text shouldBe "Hello World"
        result[0].matchPositions shouldBe emptyList()
    }

    @Test
    fun `highlights single match in a line`() {
        val lines = listOf("Hello World")
        val result = buildHighlightedTextLines(lines, "World")
        result.size shouldBe 1
        result[0].matchPositions shouldBe listOf(6)
        result[0].annotatedString.text shouldBe "Hello World"
    }

    @Test
    fun `highlights multiple matches in a single line`() {
        val lines = listOf("Hello World World")
        val result = buildHighlightedTextLines(lines, "World")
        result.size shouldBe 1
        result[0].matchPositions shouldBe listOf(6,12)
        result[0].annotatedString.text shouldBe "Hello World World"
    }

    @Test
    fun `is case insensitive`() {
        val lines = listOf("Hello WORLD", "worldly matters")
        val result = buildHighlightedTextLines(lines, "woRld")
        result.size shouldBe 2
        result[0].matchPositions shouldBe listOf(6)
        result[1].matchPositions shouldBe listOf(0)
    }

    @Test
    fun `no matches returns empty positions`() {
        val lines = listOf("Nothing here")
        val result = buildHighlightedTextLines(lines, "xyz")
        result.size shouldBe 1
        result[0].matchPositions.shouldBeEmpty()
    }
}