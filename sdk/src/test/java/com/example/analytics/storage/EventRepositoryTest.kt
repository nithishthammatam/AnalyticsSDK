package com.example.analytics.storage

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.analytics.core.Event
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest=Config.NONE)
class EventRepositoryTest {

    private lateinit var database: EventDatabase
    private lateinit var eventDao: EventDao
    private lateinit var repository: EventRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // Use an in-memory database for testing
        database = Room.inMemoryDatabaseBuilder(context, EventDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        eventDao = database.eventDao()
        repository = EventRepository(eventDao)
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun `saveEvent inserts event correctly`() = runBlocking {
        val event = Event(name = "test_event", timestamp = 12345L, properties = null, userId = null, sessionId = null, anonymousId = null, deviceInfo = null)
        repository.saveEvent(event)

        val batch = repository.getBatch(10)
        assertEquals(1, batch.size)
        assertEquals("test_event", batch[0].name)
    }

    @Test
    fun `deleteBatch removes specified events`() = runBlocking {
        val event1 = Event(name = "event_1", timestamp = 1000L, properties = null, userId = null, sessionId = null, anonymousId = null, deviceInfo = null)
        val event2 = Event(name = "event_2", timestamp = 2000L, properties = null, userId = null, sessionId = null, anonymousId = null, deviceInfo = null)
        
        repository.saveEvent(event1)
        repository.saveEvent(event2)

        val batch = repository.getBatch(10)
        assertEquals(2, batch.size)

        repository.deleteBatch(listOf(batch[0].id, batch[1].id))
        
        val newBatch = repository.getBatch(10)
        assertEquals(0, newBatch.size)
    }

    @Test
    fun `saveEvent respects MAX_EVENTS limit`() = runBlocking {
        // Repository has MAX_EVENTS = 500
        for (i in 1..505) {
            repository.saveEvent(Event(name = "event_$i", timestamp = i.toLong(), properties = null, userId = null, sessionId = null, anonymousId = null, deviceInfo = null))
        }

        // We should only have 500 events
        val count = eventDao.getEventCount()
        assertEquals(500, count)

        // The oldest 5 events (1 to 5) should have been deleted
        val batch = repository.getBatch(1)
        assertEquals("event_6", batch[0].name)
    }
}
