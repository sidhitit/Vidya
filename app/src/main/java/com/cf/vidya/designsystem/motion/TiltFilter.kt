package com.cf.vidya.designsystem.motion

/**
 * Turns raw phone-orientation readings into a calm tilt value from -1 to 1 on each axis.
 *
 * Input is the "up" direction as the phone sees it (x and y parts of the gravity-up vector,
 * each roughly the sine of the tilt angle). Plain maths with no Android types, so it's unit-tested.
 *
 * @param range how far you must tilt to reach ±1 (0.17 ≈ 10°).
 * @param smoothing how quickly the output follows the phone (0..1); lower is calmer, filters hand jitter.
 * @param recentering how quickly a new way of holding the phone becomes the new "flat" (0..1).
 */
class TiltFilter(
    private val range: Float = 0.17f,
    private val smoothing: Float = 0.15f,
    private val recentering: Float = 0.01f,
) {
    /** Positive when the right edge of the phone dips down. */
    var x = 0f
        private set

    /** Positive when the top edge of the phone tips away from you. */
    var y = 0f
        private set

    private var baseX = Float.NaN
    private var baseY = Float.NaN

    fun update(upX: Float, upY: Float) {
        // The first reading is "flat", whatever angle the phone is held at.
        if (baseX.isNaN()) {
            baseX = upX
            baseY = upY
        }
        baseX += (upX - baseX) * recentering
        baseY += (upY - baseY) * recentering

        val targetX = ((baseX - upX) / range).coerceIn(-1f, 1f)
        val targetY = ((baseY - upY) / range).coerceIn(-1f, 1f)
        x += (targetX - x) * smoothing
        y += (targetY - y) * smoothing
    }
}
