package com.example.nextrequest

import com.example.nextrequest.home.presentation.util.getHeaderValue
import com.example.nextrequest.core.KeyValueList
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ListExtensionTest : StringSpec({

    "give a pairs of strings and returns correct value for existing key" {
        val headers: KeyValueList = listOf(
            "Authorization" to "bearer jksdfs",
            "Authorization" to "cgwbkscd",
            "Token" to "gusdh",
            "Content-Type" to "application/json",
        )
        headers.getHeaderValue("Authorization") shouldBe "bearer jksdfs"
        headers.getHeaderValue("Content-Type") shouldBe "application/json"
    }

    "return empty string when the key is not found" {
        val headers = listOf(
            Pair("A", "aaa"),
            Pair("B", "bbb"),
            Pair("C", "ccc")
        )
        headers.getHeaderValue("D") shouldBe ""
    }

    "is case-insensitive" {
        val headers = listOf(
            "Content-Type" to "application/json"
        )

        headers.getHeaderValue("CONTENT-type") shouldBe "application/json"
    }
})