package com.example.nextrequest

import com.example.nextrequest.socket.data.network.WebSocketRepositoryImp
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WebSocketRepositoryImpTest {
    lateinit var repository: WebSocketRepositoryImp
    lateinit var listenerSlot: CapturingSlot<WebSocketListener>
    lateinit var mockWebSocket: WebSocket
    val testDispatcher = StandardTestDispatcher()
    lateinit var client: OkHttpClient
    val testScope = TestScope(testDispatcher)

    @BeforeEach
    fun setup() {
        client = mockk()
        mockWebSocket = mockk(relaxed = true)
        listenerSlot = slot()
        every { client.newWebSocket(any(), capture(listenerSlot)) } returns mockWebSocket
        repository = WebSocketRepositoryImp(client, testScope)
    }

    @Test
    fun `onMessage emits incoming message`() = runTest {
        repository.connect("ws://test-url")
        listenerSlot.captured.onOpen(mockWebSocket, mockk())

        listenerSlot.captured.onMessage(mockWebSocket, "Hello")
        testScope.advanceUntilIdle()

        val message = repository.messages.first()
        message.text shouldBe "Hello"
        message.isSentByUser.shouldBeFalse()
    }

    @Test
    fun `connect should update isConnected and emit connected message`() = runTest {
        repository.connect("ws://test-url")
        listenerSlot.captured.onOpen(mockWebSocket, mockk())
        testScope.advanceUntilIdle()

        repository.isConnected.first().shouldBeTrue()
        repository.messages.first().text shouldBe "Connected to ws://test-url"
        repository.messages.first().isSentByUser shouldBe false
    }

    @Test
    fun `disconnect should close socket and update isConnected`() = runTest {
        repository.connect("ws://test-url")
        repository.disconnect()

        verify { mockWebSocket.close(1000, "User disconnected") }
        repository.isConnected.first().shouldBeFalse()
    }

    @Test
    fun `close should close socket and cancel scope`() = runTest {
        repository.connect("ws://test-url")
        repository.close()

        verify { mockWebSocket.close(1000, "connection closed") }
        repository.isConnected.first().shouldBeFalse()
    }


    @Test
    fun `onFailure should update isConnected to false`() = runTest {
        repository.connect("ws://test-url")

        listenerSlot.captured.onFailure(mockWebSocket, RuntimeException("fail"), null)
        repository.isConnected.first().shouldBeFalse()
    }

    @AfterEach
    fun tearDown() {
        repository.disconnect()
    }
}