package com.example.timer.impl

import kotlin.time.Duration.Companion.seconds

interface Timer {
    fun startTimer()

    fun stopTimer()

    fun destroyTimer()
}

class TimerImpl(
    val providers: List<Provider> = listOf(
        testProvider("firstProvider", 30.seconds),
        testProvider("secondProvider", 60.seconds),
        testProvider("thirdProvider", 10.seconds),
        testProvider("fourProvider", 44.seconds),
    )
) : Timer {
    override fun startTimer() {
        // TODO("Not yet implemented")
    }

    override fun stopTimer() {
        // TODO("Not yet implemented")
    }

    override fun destroyTimer() {
        // TODO("Not yet implemented")
    }

}