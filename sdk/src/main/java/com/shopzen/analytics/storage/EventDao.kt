package com.shopzen.analytics.storage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.shopzen.analytics.core.Event

@Dao
interface EventDao {
    @Insert
    suspend fun insertEvent(event: Event)

    @Query("SELECT * FROM events ORDER BY timestamp ASC LIMIT :limit")
    suspend fun getOldestEvents(limit: Int): List<Event>

    @Query("DELETE FROM events WHERE id IN (:ids)")
    suspend fun deleteEvents(ids: List<Long>)

    @Query("SELECT COUNT(*) FROM events")
    suspend fun getEventCount(): Int

    @Query("DELETE FROM events WHERE id IN (SELECT id FROM events ORDER BY timestamp ASC LIMIT :amount)")
    suspend fun deleteOldestEvents(amount: Int)
}
