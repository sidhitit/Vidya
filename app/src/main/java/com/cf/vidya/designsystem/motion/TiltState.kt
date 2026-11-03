package com.cf.vidya.designsystem.motion

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.lifecycle.compose.LifecycleResumeEffect

/**
 * How the phone is tilted right now, from -1 to 1 on each axis (see [TiltFilter]).
 * Read [x] and [y] inside graphicsLayer or drawBehind so tilting doesn't recompose anything.
 */
@Stable
class TiltState {
    private val xState = mutableFloatStateOf(0f)
    private val yState = mutableFloatStateOf(0f)

    val x: Float get() = xState.floatValue
    val y: Float get() = yState.floatValue

    internal fun set(x: Float, y: Float) {
        xState.floatValue = x
        yState.floatValue = y
    }
}

/**
 * Follows the phone's tilt with the motion sensors, for parallax effects.
 *
 * - Listens only while the screen is in the foreground (saves battery).
 * - Stays at 0 (no movement) when the user turned on "Remove animations",
 *   when the phone has no gyroscope, and in Previews.
 */
@Composable
fun rememberTiltState(): TiltState {
    val state = remember { TiltState() }
    if (LocalInspectionMode.current) return state
    val context = LocalContext.current

    LifecycleResumeEffect(context) {
        val sensorManager = context.getSystemService(SensorManager::class.java)
        // "Game" rotation vector: smooth gyroscope-based orientation, no compass, so no sudden jumps.
        val sensor = sensorManager?.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR)
        val reduceMotion = Settings.Global.getFloat(
            context.contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE,
            1f,
        ) == 0f

        val filter = TiltFilter()
        val rotation = FloatArray(9)
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                SensorManager.getRotationMatrixFromVector(rotation, event.values)
                // The last row of the matrix is the world's "up" direction as seen by the phone.
                filter.update(upX = rotation[6], upY = rotation[7])
                state.set(filter.x, filter.y)
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }
        if (sensor != null && !reduceMotion) {
            sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME)
        }

        onPauseOrDispose {
            sensorManager?.unregisterListener(listener)
            state.set(0f, 0f)
        }
    }
    return state
}
