package com.example.timer.impl

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

const val TIMER_PERIOD = 1000L
const val TIMER_TICK = 1
interface Timer {
    fun startTimer()

    fun stopTimer()

    fun destroyTimer()
}
@RequiresApi(Build.VERSION_CODES.O)
class TimerImpl(
    private val scope: CoroutineScope,
    private val providers: List<Provider> = listOf(
        testProvider("TimerTag firstProvider", 30.seconds),
        testProvider("TimerTag secondProvider", 60.seconds),
        testProvider("TimerTag thirdProvider", 10.seconds),
        testProvider("TimerTag fourProvider", 44.seconds),
    )
) : Timer {


    private var timer: java.util.Timer? = null
    private var currentTimeSpent = 0L
    private var lastStartTime = 0L
    override fun startTimer() {
        Log.d("TimerTag","Instance = $this")
        lastStartTime = currentTimeSpent
        scope.launch {
            providers.forEach { it.onTimerStart(currentTimeSpent.seconds) }
            timer = kotlin.concurrent.timer(
                period = TIMER_PERIOD,
            ) {
                currentTimeSpent += TIMER_TICK
                notifyProvider()
                Log.d("TimerTag","timeTick = 1 currentTimeSpent = $currentTimeSpent")
            }
        }
    }

    override fun stopTimer() {
        scope.launch {
            val spentTime = (currentTimeSpent - lastStartTime).seconds
            providers.forEach { it.onTimerStop(spentTime) }
        }
        timer?.cancel()
    }

    override fun destroyTimer() {
        timer?.cancel()
        timer = null
    }

    private fun notifyProvider() {
        scope.launch {
            providers.forEach {
                val syncInterval = it.getSyncInterval()
                if(currentTimeSpent % syncInterval.inWholeSeconds == 0L ) {
                    it.syncTime(currentTimeSpent.seconds)
                }
            }
        }
    }
}