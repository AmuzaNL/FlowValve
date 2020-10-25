package com.amuza.kotlin.flowvalve

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*

@OptIn(ExperimentalCoroutinesApi::class)
class FlowSwitch<T, E : Enum<E>>(
    values: () -> Array<E>,
    private val defaultValue: T,
    private val coroutineScope: CoroutineScope
) {

    private val _outFlow = MutableStateFlow(defaultValue)
    val outFlow: StateFlow<T> = _outFlow

    private val switchMap = HashMap<E, Flow<T>>()
    private var runningJob: Job? = null

    fun selectInput(input: E) {
        runningJob?.cancel()

        if (!switchMap.containsKey(input)) {
            _outFlow.value = defaultValue
        }

        runningJob = switchMap[input]?.onEach {
            _outFlow.value = it
        }?.launchIn(coroutineScope)
    }

    fun addInput(input: E, flowInput: Flow<T>) {
        switchMap[input] = flowInput
    }
}