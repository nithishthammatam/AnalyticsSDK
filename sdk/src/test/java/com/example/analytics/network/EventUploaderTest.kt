package com.example.analytics.network

import com.example.analytics.core.Event
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer

@RunWith(RobolectricTestRunner::class)
@Config(manifest=Config.NONE)
class EventUploaderTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var eventUploader: EventUploader

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        
        // EventUploader currently hardcodes the base URL to "https://dummy.com/".
        // In a real scenario, we would inject the Retrofit client or Base URL.
        // For this test, since it uses @Url, we can just pass the MockWebServer URL.
        eventUploader = EventUploader()
    }

    @Test
    fun `uploadBatch returns true on successful 200 response`() = runBlocking {
        mockWebServer.enqueue(MockResponse().setResponseCode(200))
        
        val events = listOf(
            Event(name = "test", timestamp = 123L, properties = null, userId = null, sessionId = null, anonymousId = null, deviceInfo = null)
        )
        
        val result = eventUploader.uploadBatch(mockWebServer.url("/events").toString(), events)
        assertTrue(result)
    }

    @Test
    fun `uploadBatch returns false on 500 error`() = runBlocking {
        mockWebServer.enqueue(MockResponse().setResponseCode(500))
        
        val events = listOf(
            Event(name = "test", timestamp = 123L, properties = null, userId = null, sessionId = null, anonymousId = null, deviceInfo = null)
        )
        
        val result = eventUploader.uploadBatch(mockWebServer.url("/events").toString(), events)
        assertFalse(result)
    }
}
