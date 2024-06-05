package com.example.timer.impl

import kotlin.time.Duration

interface Provider {
    suspend fun onTimerStart(
        currentTime: Duration,
    )

    suspend fun onTimerStop(
        currentTime: Duration,
    )

    suspend fun syncTime(
        spendTime: Duration,
    )

    fun getSyncInterval(): Duration
}

fun testProvider(tag: String, interval: Duration) : Provider = object : Provider {
    override suspend fun onTimerStart(currentTime: Duration) {
        println("$tag: onTimerStart $currentTime")
    }

    override suspend fun onTimerStop(currentTime: Duration) {
        println("$tag: onTimerStop $currentTime")
    }

    override suspend fun syncTime(spendTime: Duration) {
        println("$tag: syncTime $spendTime")
    }

    override fun getSyncInterval(): Duration {
        return interval
    }
}