package com.example.timer.impl

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Date
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

interface Timer {
    fun startTimer()

    fun stopTimer()

    fun destroyTimer()
}

@OptIn(ExperimentalCoroutinesApi::class)
class TimerImpl(
    scope: CoroutineScope,
    private val providers: List<Provider> = listOf(
        testProvider("firstProvider", 30.seconds),
        testProvider("secondProvider", 60.seconds),
        testProvider("thirdProvider", 10.seconds),
        testProvider("fourProvider", 44.seconds),
    )
) : Timer {

    private val stateFlow = MutableStateFlow(State.STOP)

    private val timerJob: Job

    init {
        timerJob = scope.launch {
            flow {
                var spendSeconds = 0
                var lastStartTime: Long? = null
                stateFlow.flatMapLatest { state ->
                    if (state == State.STOP) {
                        flow {
                            lastStartTime?.let { startTime ->
                                val stopTime = Date().time / 1000
                                val duration = (stopTime - startTime).seconds
                                emit(TimerAction.Stop(duration))
                            }
                        }
                    } else {
                        flow<TimerAction> {
                            while (isActive) {
                                delay(1.seconds)
                                emit(TimerAction.Handle)
                            }
                        }.onStart {
                            val startTime = Date().time / 1000
                            lastStartTime = startTime
                            emit(TimerAction.Start(startTime.seconds))
                        }
                    }
                }.collect { action ->
                    when (action) {
                        is TimerAction.Handle -> {
                            spendSeconds += 1
                            emit(TimerEvent.Handle(spendSeconds.seconds))
                        }

                        is TimerAction.Start -> {
                            emit(TimerEvent.Start(action.duration))
                        }

                        is TimerAction.Stop -> {
                            emit(TimerEvent.Stop(action.duration))
                        }
                    }
                }
            }.collect { event ->
                when (event) {
                    is TimerEvent.Handle -> {
                        providers.filter {
                            event.duration.toInt(DurationUnit.SECONDS) % it.getSyncInterval().toInt(DurationUnit.SECONDS) == 0
                        }.forEach {
                            launch { it.syncTime(event.duration) }
                        }
                    }
                    is TimerEvent.Start -> {
                        providers.forEach {
                            launch { it.onTimerStart(event.duration) }
                        }
                    }
                    is TimerEvent.Stop -> {
                        providers.forEach {
                            launch { it.onTimerStop(event.duration) }
                        }
                    }
                }

            }
        }
    }


    override fun startTimer() {
        stateFlow.value = State.START
    }

    override fun stopTimer() {
        stateFlow.value = State.STOP
    }

    override fun destroyTimer() {
        stateFlow.value = State.STOP
        timerJob.cancel()
    }

    private enum class State {
        START, STOP;
    }

    private sealed interface TimerAction {
        data class Start(val duration: Duration) : TimerAction
        data class Stop(val duration: Duration) : TimerAction
        data object Handle : TimerAction
    }

    private sealed interface TimerEvent {
        data class Start(val duration: Duration) : TimerEvent
        data class Stop(val duration: Duration) : TimerEvent
        data class Handle(val duration: Duration) : TimerEvent
    }
}