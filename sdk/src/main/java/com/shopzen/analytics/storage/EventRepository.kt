package com.shopzen.analytics.storage

import com.shopzen.analytics.core.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class EventRepository(private val eventDao: EventDao) {
    private val MAX_EVENTS = 500
    private val mutex = Mutex()

    suspend fun saveEvent(event: Event) = withContext(Dispatchers.IO) {
        mutex.withLock {
            val currentCount = eventDao.getEventCount()
            
            // If we hit our max queue size, delete the oldest event before inserting
            if (currentCount >= MAX_EVENTS) {
                val amountToRemove = (currentCount - MAX_EVENTS) + 1
                eventDao.deleteOldestEvents(amountToRemove)
            }
            
            eventDao.insertEvent(event)
        }
    }

    suspend fun getBatch(limit: Int): List<Event> = withContext(Dispatchers.IO) {
        return@withContext eventDao.getOldestEvents(limit)
    }

    suspend fun deleteBatch(eventIds: List<Long>) = withContext(Dispatchers.IO) {
        eventDao.deleteEvents(eventIds)
    }
}
