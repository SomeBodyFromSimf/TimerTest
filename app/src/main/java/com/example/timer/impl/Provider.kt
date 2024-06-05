package com.example.timer.impl

import kotlin.time.Duration

interface Provider {
    suspend fun onTimerStart(
        currentTime: Duration, //  Время во сколько стартанул таймер
    )

    suspend fun onTimerStop(
        currentTime: Duration, //  Время сколько работал таймер с последнего запуска
    )

    suspend fun syncTime(
        spendTime: Duration, // суммарное время. Например провайдер на 10 сек. Отправляем 10, 20, 30 и тд
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