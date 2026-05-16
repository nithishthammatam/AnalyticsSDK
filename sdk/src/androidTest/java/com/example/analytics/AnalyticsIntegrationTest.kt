package com.example.analytics

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.analytics.core.AnalyticsSDK
import com.example.analytics.core.SDKConfig
import com.example.analytics.storage.EventDatabase
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AnalyticsIntegrationTest {

    private lateinit var context: Context
    private lateinit var database: EventDatabase

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        database = EventDatabase.getDatabase(context)
        
        // Clear database before each test
        runBlocking {
            database.clearAllTables()
        }

        val config = SDKConfig.Builder("https://api.dummy.com/events")
            .setDebug(true)
            .setBatchSize(5)
            .build()
            
        AnalyticsSDK.init(context, config)
    }

    @Test
    fun testFullEventLifecycle_trackAndStore() = runBlocking {
        // 1. Track an event
        AnalyticsSDK.track("integration_test_event", mapOf("source" to "instrumentation_test"))
        
        // 2. Wait for Coroutine to process (since AnalyticsSDK uses Dispatchers.IO internally)
        delay(500) 
        
        // 3. Verify it was stored in the database
        val count = database.eventDao().getEventCount()
        assertEquals(1, count)
        
        val events = database.eventDao().getOldestEvents(1)
        assertEquals("integration_test_event", events[0].name)
    }

    @Test
    fun testEdgeCase_queuedEventsLimit() = runBlocking {
        // Track 505 events
        for (i in 1..505) {
            AnalyticsSDK.track("spam_event_$i")
        }
        
        // Wait for all coroutines to finish inserting
        delay(2000) 
        
        // EventRepository restricts to 500 max events
        val count = database.eventDao().getEventCount()
        assertTrue("Event count should be capped at 500, but was $count", count <= 500)
    }
}
