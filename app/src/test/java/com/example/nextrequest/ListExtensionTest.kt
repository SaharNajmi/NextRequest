package com.example.nextrequest

import com.example.nextrequest.core.models.KeyValue
import com.example.nextrequest.home.presentation.util.getHeaderValue
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ListExtensionTest : StringSpec({

    "give a pairs of strings and returns correct value for existing key" {
        val headers: List<KeyValue> = listOf(
            KeyValue("Authorization", "bearer jksdfs"),
            KeyValue("Authorization", "cgwbkscd"),
            KeyValue("Token", "gusdh"),
            KeyValue("Content-Type", "application/json"),
        )
        headers.getHeaderValue("Authorization") shouldBe "bearer jksdfs"
        headers.getHeaderValue("Content-Type") shouldBe "application/json"
    }

    "return empty string when the key is not found" {
        val headers = listOf(
            KeyValue("A", "aaa"),
            KeyValue("B", "bbb"),
            KeyValue("C", "ccc")
        )
        headers.getHeaderValue("D") shouldBe ""
    }

    "is case-insensitive" {
        val headers = listOf(
            KeyValue("Content-Type" , "application/json")
        )

        headers.getHeaderValue("CONTENT-type") shouldBe "application/json"
    }
})