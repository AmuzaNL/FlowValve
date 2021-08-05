# FlowValve

[![FlowValve JVM](https://github.com/AmuzaNL/FlowValve/actions/workflows/jvm.yml/badge.svg)](https://github.com/AmuzaNL/FlowValve/actions/workflows/jvm.yml)[![FlowValve JS](https://github.com/AmuzaNL/FlowValve/actions/workflows/js.yml/badge.svg)](https://github.com/AmuzaNL/FlowValve/actions/workflows/js.yml)
[![FlowValve Native Linux](https://github.com/AmuzaNL/FlowValve/actions/workflows/native-linux.yml/badge.svg)](https://github.com/AmuzaNL/FlowValve/actions/workflows/native-linux.yml)
[![FlowValve Native Mac OS](https://github.com/AmuzaNL/FlowValve/actions/workflows/native-macos.yml/badge.svg)](https://github.com/AmuzaNL/FlowValve/actions/workflows/native-macos.yml)
[![FlowValve Native Windows](https://github.com/AmuzaNL/FlowValve/actions/workflows/native-windows.yml/badge.svg)](https://github.com/AmuzaNL/FlowValve/actions/workflows/native-windows.yml)

[![Maven Central](https://img.shields.io/maven-central/v/io.github.amuzanl.kotlin/FlowValve.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.amuzanl.kotlin%22%20AND%20a:%22FlowValve%22)

To include this library in your project

`implementation 'io.github.amuzanl.kotlin:FlowValve:<version>'`

## FlowSwitch

FlowSwitch is class that allow you to choose between different Kotlin Flows.

Below is an example how the FlowSwitch can be used to switch between 3 camera Flows. Every camera has a Flow that will
be added to the FlowSwitch with the `addInput` function. After setup of the FlowSwitch you can choose what Flow items
should be emitted into the `outFlow` by calling the `selectInput` function.

```kotlin
enum class Camera {
    Camera1,
    Camera2,
    Camera3
}

val cameraFlowSwitch = FlowSwitch(Camera::values, Bitmap(), this)

fun setup() {
    cameraFlowSwitch.addInput(Camera.Camera1, flow<Bitmap> {
        while (true) {
            val imageFromCamera = retriveFromCamera1()
            emit(imageFromCamera)
        }
    })

    cameraFlowSwitch.addInput(Camera.Camera2, flow<Bitmap> {
        while (true) {
            val imageFromCamera = retriveFromCamera2()
            emit(imageFromCamera)
        }
    })

    cameraFlowSwitch.outFlow.collect { image ->
        showImageOnScreen(image)
    }
}

fun onCamera1Click() {
    cameraFlowSwitch.selectInput(Camera.Camera1)
}

fun onCamera2Click() {
    cameraFlowSwitch.selectInput(Camera.Camera2)
}
```
