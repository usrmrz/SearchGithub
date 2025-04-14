package dev.usrmrz.searchgithub.util

import android.os.SystemClock
import android.util.Log
import androidx.collection.ArrayMap
import java.util.concurrent.TimeUnit

/**
 * Utility class that decides whether we should fetch some data or not.
 */
//@Suppress("unused")
class RateLimiter<in KEY>(timeout: Int, timeUnit: TimeUnit) {
    private val timestamps = ArrayMap<KEY, Long>()
    private val timeout = timeUnit.toMillis(timeout.toLong())

    @Synchronized
    fun shouldFetch(key: KEY): Boolean {

        val lastFetched = timestamps[key]
        val now = now()
        if (lastFetched == null) {
            timestamps[key] = now
            Log.d("RtLm", "lastFetched == null;;lastFetched: $lastFetched, now: $now, timestamps[key]: ${timestamps[key]}")
            return true
        }
        if (now - lastFetched > timeout) {
            timestamps[key] = now
            Log.d("RtLm", "now - lastFetched > timeout;;lastFetched: $lastFetched, now: $now, timeout: $timeout")
            return true
        }

        Log.d("RtLm", "fun shouldFetch return false;;lastFetched: $lastFetched, now: $now, timestamps: $timestamps")

        return false
    }

    private fun now() = SystemClock.uptimeMillis()

    @Synchronized
    fun reset(key: KEY) {
        Log.d("RtLm", "fun reset timestamps.remove(key);;key: $key, timestamps: $timestamps")
        timestamps.remove(key)
    }
}