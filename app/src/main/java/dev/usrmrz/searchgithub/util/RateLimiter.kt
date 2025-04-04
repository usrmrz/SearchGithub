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
            return true
        }
        if (now - lastFetched > timeout) {
            timestamps[key] = now
            return true
        }

        Log.d("RtLmtr", "lastFetched: $lastFetched, now: $now, timestamps: $timestamps")

        return false
    }

    private fun now() = SystemClock.uptimeMillis()

    @Synchronized
    fun reset(key: KEY) {
        timestamps.remove(key)
    }
}