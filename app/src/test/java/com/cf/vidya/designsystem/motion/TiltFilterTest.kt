package com.cf.vidya.designsystem.motion

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TiltFilterTest {

    @Test
    fun firstReadingIsFlat() {
        val filter = TiltFilter()
        filter.update(upX = 0.3f, upY = -0.8f) // held at an angle
        assertEquals(0f, filter.x, 0f)
        assertEquals(0f, filter.y, 0f)
    }

    @Test
    fun tiltingMovesTowardsOneButNeverPast() {
        val filter = TiltFilter(recentering = 0f)
        filter.update(0f, 0f)
        repeat(100) { filter.update(upX = -1f, upY = -1f) } // a big tilt, far past the range

        assertEquals(1f, filter.x, 0.01f)
        assertEquals(1f, filter.y, 0.01f)
    }

    @Test
    fun smallShakesAreSmoothedOut() {
        val filter = TiltFilter(recentering = 0f)
        filter.update(0f, 0f)
        filter.update(upX = -0.17f, upY = 0f) // one sudden full-range reading

        assertTrue("moved only part of the way", filter.x in 0.01f..0.5f)
    }

    @Test
    fun holdingStillAtANewAngleBecomesTheNewFlat() {
        val filter = TiltFilter()
        filter.update(0f, 0f)
        repeat(1_000) { filter.update(upX = -0.1f, upY = 0f) }

        assertEquals(0f, filter.x, 0.02f)
    }
}
