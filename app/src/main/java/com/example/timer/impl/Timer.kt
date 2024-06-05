package com.example.timer.impl

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.toDuration

interface Timer {
    fun startTimer()

    fun stopTimer()

    fun destroyTimer()
}

private const val delayForTimerMiliss = 1000

class TimerImpl(
    val scope: CoroutineScope,
    val providers: List<Provider> = listOf(
        testProvider("firstProvider", 30.seconds),
        testProvider("secondProvider", 60.seconds),
        testProvider("thirdProvider", 10.seconds),
        testProvider("fourProvider", 44.seconds),
    )
) : Timer {
    private var iterationJob: Job? = null

    private val currentTimeFlow = MutableStateFlow(0)

//    private val currentTimeFlowNew = MutableStateFlow(Duration.ZERO)

    init {
        scope.launch {
            currentTimeFlow.collect { value ->
                providers.forEach {
                    val synhInterval = it.getSyncInterval().toLong(DurationUnit.SECONDS)
                    if (value % synhInterval == 0L) {
                        it.syncTime(value.toDuration(DurationUnit.MILLISECONDS))
                    }
                }
            }
        }

//        scope.launch {
//            currentTimeFlowNew
//                .collect { value ->
//                    providers.forEach {
//                        val synhInterval = it.getSyncInterval()
//                        val isNeedSynhTim: Boolean = false//TODO
//                        if (isNeedSynhTim) {
//                            it.syncTime(value)
//                        }
//                    }
//                }
//        }

    }


    override fun startTimer() {
        scope.launch {
            providers.forEach {
                it.onTimerStart(currentTimeFlow.value.toDuration(DurationUnit.MILLISECONDS))
            }
        }
        iterationJob = createIterationJob()
        iterationJob?.start()
    }

    override fun stopTimer() {
        destroyIteration()
        scope.launch {
            providers.forEach {
                it.onTimerStop(currentTimeFlow.value.toDuration(DurationUnit.MILLISECONDS))
            }
        }
    }

    override fun destroyTimer() {
        destroyIteration()
    }

    private fun destroyIteration() {
        iterationJob?.cancel()
        iterationJob = null
    }

    private fun createIterationJob(): Job {
        return scope.launch {
            //init delay
            delay(delayForTimerMiliss.toLong())
            while (true) {
                currentTimeFlow.emit(currentTimeFlow.value + 1)
                delay(delayForTimerMiliss.toLong())
            }

        }
    }

    //New
//    private fun createIterationJobNew(): Job {
//        val delay = 100.toDuration(DurationUnit.MILLISECONDS)
//        return scope.launch {
//            //init delay
//            while (true) {
//                val startDuration = getCurrentInDuration()
//                delay(delayForTimerMiliss.toLong())
//                val endDuration = getCurrentInDuration()
//                val delta = currentTimeFlowNew.value + (endDuration - startDuration)
//                currentTimeFlowNew.emit(delta)
//            }
//
//        }
//    }

//    private fun getCurrentInDuration(unit: DurationUnit = DurationUnit.MILLISECONDS) =
//        System.currentTimeMillis().toDuration(unit)

}