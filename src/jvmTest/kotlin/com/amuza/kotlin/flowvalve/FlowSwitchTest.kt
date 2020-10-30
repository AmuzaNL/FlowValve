package com.amuza.kotlin.flowvalve

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runBlockingTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class FlowSwitchTest {
    enum class TestEnum {
        A, B, C, D, E
    }

    @Test
    fun testFlowSwitch_withoutInput_shouldGiveDefaultValue() = runBlockingTest {
        val flowSwitch = FlowSwitch(TestEnum::values, "value", this)

        assertEquals("value", flowSwitch.outFlow.value)
    }

    @Test
    fun testFlowSwitch_withoutInput_SelectInput() = runBlockingTest {
        val flowSwitch = FlowSwitch(TestEnum::values, "value", this)

        flowSwitch.selectInput(TestEnum.A)

        assertEquals("value", flowSwitch.outFlow.value)
    }

    @Test
    fun testFlowSwitch_withInput_SelectInput() = runBlockingTest {
        val flowSwitch = FlowSwitch(TestEnum::values, "value", this)

        flowSwitch.addInput(TestEnum.A, flow<String> {
            emit("another value")
        })

        flowSwitch.selectInput(TestEnum.A)

        assertEquals("another value", flowSwitch.outFlow.value)
    }

    @Test
    fun testFlowSwitch_withMultipleInputs_SelectInputs() = runBlockingTest {
        val flowSwitch = FlowSwitch(TestEnum::values, "value", this)

        flowSwitch.addInput(TestEnum.B, flow<String> {
            emit("Value from B")
        })
        flowSwitch.addInput(TestEnum.C, flow<String> {
            emit("Value from C")
        })

        flowSwitch.selectInput(TestEnum.A)
        assertEquals("value", flowSwitch.outFlow.value)

        flowSwitch.selectInput(TestEnum.C)
        assertEquals("Value from C", flowSwitch.outFlow.value)

        flowSwitch.selectInput(TestEnum.B)
        assertEquals("Value from B", flowSwitch.outFlow.value)
    }
}