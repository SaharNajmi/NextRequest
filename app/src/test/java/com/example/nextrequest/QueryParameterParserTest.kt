package com.example.nextrequest

import com.example.nextrequest.core.extensions.buildUrlWithParams
import com.example.nextrequest.core.extensions.mapKeyValuePairsToQueryParameter
import com.example.nextrequest.core.extensions.mapStringToKeyValuePairs
import com.example.nextrequest.core.extensions.removeParameterFromUrl
import com.example.nextrequest.core.models.KeyValue
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test


class QueryParameterParserTest {
    @Test
    fun stringToKeyValue() {
        val input = "https://api.escuelajs.co/api/v1/products?offset=1&limit=2"
        val output = input.mapStringToKeyValuePairs()
        val expected = listOf(KeyValue("offset" , "1"), KeyValue("limit" , "2") )

        expected.forEachIndexed { i, keyValue ->
            withClue("expected $keyValue but got ${output[i]}") {
                output[i] shouldBe keyValue
            }
        }
    }

    @Test
    fun shouldReturnEmptyListGivenEmptyString() {
        val input = ""
        val output = input.mapStringToKeyValuePairs()
        assert(output.isEmpty())
        output.shouldBeEmpty()
    }

    @Test
    fun shouldReturnEmptyListGivenInvalidUrl() {
        val input = "s"
        val output = input.mapStringToKeyValuePairs()
        output.shouldBeEmpty()
    }

    @Test
    fun shouldReturnPairsAfterGivenQuestionMark() {
        val input = "url?n"
        val outPut = input.mapStringToKeyValuePairs()
        val expected = listOf(KeyValue("n" , ""))
        outPut shouldBe expected
    }

    @Test
    fun returnQueryParametersByGivenPairs() {
        val input = listOf(KeyValue("offset", "3"), KeyValue("limit", "4"))
        val outPut = input.mapKeyValuePairsToQueryParameter()
        val expected = "offset=3&limit=4"
        outPut shouldBe expected
    }

    @Test
    fun returnUrlIfThereIsNoQueryParameters() {
        val url = "url?"
        val params = ""
        val output = url.buildUrlWithParams(params)
        output shouldBe url
    }

    @Test
    fun `removeParameter from url`() {
        val url = "https://example.com?foo=1&bar=2&foo=3"
        val result = url.removeParameterFromUrl("foo", "1")
        result shouldBe "https://example.com?bar=2&foo=3"

        val result2 = url.removeParameterFromUrl("bar", "2")
        result2 shouldBe "https://example.com?foo=1&foo=3"

        val result3 = result2.removeParameterFromUrl("foo", "1").removeParameterFromUrl("foo", "3")
        result3 shouldBe "https://example.com"
    }
}