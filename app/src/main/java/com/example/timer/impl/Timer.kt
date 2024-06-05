package com.example.timer.impl

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

interface Timer {
    fun startTimer()

    fun stopTimer()

    fun destroyTimer()
}

class TimerImpl(
    val scope: CoroutineScope,
    val providers: List<Provider> = listOf(
        testProvider("firstProvider", 30.seconds),
        testProvider("secondProvider", 60.seconds),
        testProvider("thirdProvider", 10.seconds),
        testProvider("fourProvider", 44.seconds),
    )
) : Timer {
    private val isTimerPaused = MutableStateFlow(false)
    private val startTime = MutableStateFlow((System.currentTimeMillis() / 1000).seconds)
    private val leaveTime = MutableStateFlow((System.currentTimeMillis() / 1000).seconds)
    override fun startTimer() {
        if (isTimerPaused.value.not()) {
            startTime.value = (System.currentTimeMillis() / 1000).seconds
            scope.launch {
                providers.forEach { provider ->
                    provider.onTimerStart(startTime.value)
                }
            }
        } else {
            scope.launch {
                providers.forEach { provider ->
                    if (leaveTime.value.inWholeSeconds - startTime.value.inWholeSeconds < provider.getSyncInterval().inWholeSeconds) {
                        provider.onTimerStart(startTime.value)
                    } else {
                        provider.onTimerStart((System.currentTimeMillis() / 1000).seconds)
                    }
                }
            }
            isTimerPaused.value = false
        }
    }

    override fun stopTimer() {
        isTimerPaused.value = true
        val stopTime = (System.currentTimeMillis() / 1000).seconds
        leaveTime.value = stopTime
        scope.launch {
            providers.forEach { provider ->
                provider.onTimerStop(stopTime - startTime.value)
            }
        }
    }

    override fun destroyTimer() {
    }

}