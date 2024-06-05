package com.example.timer.impl

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
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

    init {
        scope.launch {
            flow {
                var spendSeconds = 0
                stateFlow.flatMapLatest {  state ->
                    if (state == State.STOP) {
                        emptyFlow()
                    } else {
                        flow {
                            while (isActive) {
                                delay(1.seconds)
                                emit(Unit)
                            }
                        }
                    }
                }.collect {
                    spendSeconds += 1
                    emit (spendSeconds)
                }
            }.collect { seconds ->
                providers.filter {
                    seconds % it.getSyncInterval().toInt(DurationUnit.SECONDS) == 0
                }.forEach {
                    it.syncTime(seconds.seconds)
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
    }

    enum class State {
        START, STOP;
    }

}