package com.risyan.quickshutdownphone

import com.risyan.quickshutdownphone.screen_content_guard.extensions.gradeFuzzyOccurrence
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue

class GradeFuzzyOccurrenceTest {

    private fun grade(
        safeCount: Int,
        nsfwCount: Int,
        blankCount: Int
    ): Boolean =
        gradeFuzzyOccurrence(
            safeCount = safeCount,
            nsfwCount = nsfwCount,
            blankCount = blankCount,
            resetCounters = {}
        )

    // ─────────────────────────────
    // A. Guard clause (total < 12)
    // ─────────────────────────────

    @Test
    fun totalLessThan12_shouldNotLock_A1() {
        assertFalse(
            grade(
                safeCount = 5,
                nsfwCount = 3,
                blankCount = 3
            )
        )
    }

    @Test
    fun totalLessThan12_shouldNotLock_A2() {
        assertFalse(
            grade(
                safeCount = 4,
                nsfwCount = 4,
                blankCount = 3
            )
        )
    }

    // ─────────────────────────────
    // B. NSFW-driven lock
    // ─────────────────────────────

    @Test
    fun nsfwEqualsSafe_shouldLock_B1() {
        assertTrue(
            grade(
                safeCount = 6,
                nsfwCount = 6,
                blankCount = 0
            )
        )
    }

    @Test
    fun nsfwDominant_shouldLock_B2() {
        assertTrue(
            grade(
                safeCount = 4,
                nsfwCount = 7,
                blankCount = 1
            )
        )
    }

    @Test
    fun heavyNsfw_shouldLock_B3() {
        assertTrue(
            grade(
                safeCount = 2,
                nsfwCount = 8,
                blankCount = 2
            )
        )
    }

    @Test
    fun allNsfw_shouldLock_B4() {
        assertTrue(
            grade(
                safeCount = 0,
                nsfwCount = 12,
                blankCount = 0
            )
        )
    }

    // ─────────────────────────────
    // C. Blank-dominant (incognito abuse)
    // ─────────────────────────────

    @Test
    fun blankEqualsSafe_shouldLock_C1() {
        assertTrue(
            grade(
                safeCount = 6,
                nsfwCount = 0,
                blankCount = 6
            )
        )
    }

    @Test
    fun blankCloseToSafe_shouldNotLock_C2() {
        assertFalse(
            grade(
                safeCount = 7,
                nsfwCount = 0,
                blankCount = 5
            )
        )
    }

    @Test
    fun blankGreaterThanSafe_shouldLock_C3() {
        assertTrue(
            grade(
                safeCount = 5,
                nsfwCount = 0,
                blankCount = 7
            )
        )
    }

    @Test
    fun heavyBlank_shouldLock_C4() {
        assertTrue(
            grade(
                safeCount = 4,
                nsfwCount = 0,
                blankCount = 8
            )
        )
    }

    @Test
    fun allBlank_shouldLock_C5() {
        assertTrue(
            grade(
                safeCount = 0,
                nsfwCount = 0,
                blankCount = 12
            )
        )
    }

    // ─────────────────────────────
    // D. Mixed but suspicious
    // ─────────────────────────────

    @Test
    fun blankWithMinorNsfw_shouldNotLock_D1() {
        assertFalse(
            grade(
                safeCount = 6,
                nsfwCount = 1,
                blankCount = 5
            )
        )
    }

    @Test
    fun blankAndNsfwBalanced_shouldLock_D2() {
        assertTrue(
            grade(
                safeCount = 5,
                nsfwCount = 2,
                blankCount = 5
            )
        )
    }


    // ─────────────────────────────
    // E. Clearly safe behavior
    // ─────────────────────────────

    @Test
    fun mostlySafe_shouldNotLock_E1() {
        assertFalse(
            grade(
                safeCount = 10,
                nsfwCount = 0,
                blankCount = 2
            )
        )
    }

    @Test
    fun safeDominant_shouldNotLock_E2() {
        assertFalse(
            grade(
                safeCount = 9,
                nsfwCount = 1,
                blankCount = 2
            )
        )
    }

    @Test
    fun safeWithMinorBlank_shouldNotLock_E3() {
        assertFalse(
            grade(
                safeCount = 8,
                nsfwCount = 0,
                blankCount = 4
            )
        )
    }
}
