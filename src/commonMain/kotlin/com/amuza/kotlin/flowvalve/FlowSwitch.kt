package com.amuza.kotlin.flowvalve

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*

class FlowSwitch<T, E : Enum<E>>(
    values: () -> Array<E>,
    private val defaultValue: T,
    private val coroutineScope: CoroutineScope
) {
    private var subscribersCount = 0
    private val _outFlow = MutableStateFlow(defaultValue)
    val outFlow: Flow<T> = _outFlow
        .onStart {
            subscribersCount++

            if (subscribersCount == 1) {
                startCollectingFromInputFlow()
            }
        }
        .onCompletion {
            subscribersCount--

            if (subscribersCount == 0) {
                cancelCollectionFromInputFlow()
            }
        }

    private val switchMap = HashMap<E, Flow<T>>()
    private var runningJob: Job? = null
    private var selectedInput: E? = null

    fun selectInput(input: E) {
        selectedInput = input

        cancelCollectionFromInputFlow()

        if (!switchMap.containsKey(input)) {
            _outFlow.value = defaultValue
            return
        }

        if (subscribersCount > 0) {
            startCollectingFromInputFlow()
        }
    }

    private fun startCollectingFromInputFlow() {
        runningJob = switchMap[selectedInput]?.onEach {
            _outFlow.value = it
        }?.launchIn(coroutineScope)
    }

    private fun cancelCollectionFromInputFlow() {
        runningJob?.cancel()
    }

    fun addInput(input: E, flowInput: Flow<T>) {
        switchMap[input] = flowInput
    }
}