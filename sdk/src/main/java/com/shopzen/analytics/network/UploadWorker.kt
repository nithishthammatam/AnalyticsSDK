package com.shopzen.analytics.network

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.shopzen.analytics.storage.EventDatabase
import com.shopzen.analytics.storage.EventRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UploadWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val endpoint = inputData.getString("endpoint") ?: return@withContext Result.failure()
        val batchSize = inputData.getInt("batch_size", 20)

        val database = EventDatabase.getDatabase(applicationContext)
        val repository = EventRepository(database.eventDao())
        val uploader = EventUploader()

        val events = repository.getBatch(batchSize)
        if (events.isEmpty()) {
            return@withContext Result.success()
        }

        val success = uploader.uploadBatch(endpoint, events)
        if (!success) { android.util.Log.e("AnalyticsSDK", "Failed to upload to: $endpoint") }
        return@withContext if (success) {
            val eventIds = events.map { it.id }
            repository.deleteBatch(eventIds)
            Result.success()
        } else {
            if (runAttemptCount >= 3) {
                Result.failure()
            } else {
                Result.retry()
            }
        }
    }
}
