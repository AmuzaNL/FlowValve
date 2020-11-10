# FlowValve

[ ![Download](https://api.bintray.com/packages/amuza/kotlin/flow-valve/images/download.svg?version=1.0.2) ](https://bintray.com/amuza/kotlin/flow-valve/1.0.2/link)
![FlowValve JVM](https://github.com/AmuzaNL/FlowValve/workflows/FlowValve%20JVM/badge.svg)

To include this library in your project

`implementation 'com.amuza.kotlin:flow-valve:1.0.1'`

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
