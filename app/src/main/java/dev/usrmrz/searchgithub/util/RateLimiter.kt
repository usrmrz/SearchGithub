package dev.usrmrz.searchgithub.util


import android.os.SystemClock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.collections.mutableMapOf

/**
 * Utility class that decides whether we should fetch some data or not.
 */
//@Suppress("unused")
class RateLimiter(val timeout: Long) {
    private val lastFetchTimes = mutableMapOf<String, MutableStateFlow<Long>>()

    @Synchronized
    fun shouldFetch(key: String): Boolean {
        val currentTime = now()
        val lastTimeFlow = lastFetchTimes.getOrPut(key) { MutableStateFlow(0L) }
        return (currentTime - lastTimeFlow.value) > timeout
    }

    fun updateFetchTime(key: String) {
        lastFetchTimes[key]?.value = now()
    }

    private fun now() = SystemClock.elapsedRealtime()

    @Synchronized
    fun reset(key: String) {
        lastFetchTimes[key]?.value = 0L
    }
}