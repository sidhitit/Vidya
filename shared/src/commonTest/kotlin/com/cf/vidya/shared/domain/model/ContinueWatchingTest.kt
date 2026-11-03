package com.cf.vidya.shared.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals

class ContinueWatchingTest {

    private fun item(last: Int, total: Int) =
        ContinueWatching(Series("s1", "Excel Mastery", "", 0, total, isPremium = false), last)

    @Test
    fun progressIsShareOfEpisodes() {
        assertEquals(0.9f, item(last = 9, total = 10).progress)
        assertEquals(0.25f, item(last = 1, total = 4).progress)
    }

    @Test
    fun progressStaysBetweenZeroAndOne() {
        assertEquals(1f, item(last = 12, total = 10).progress)
        assertEquals(0f, item(last = 3, total = 0).progress)
    }
}
