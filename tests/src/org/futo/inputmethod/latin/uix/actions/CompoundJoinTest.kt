package org.futo.inputmethod.latin.uix.actions

import androidx.test.filters.SmallTest
import androidx.test.runner.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

@SmallTest
@RunWith(AndroidJUnit4::class)
class CompoundJoinTest {
    /** Applies the computed join to [before], returning the resulting text, or null for a no-op. */
    private fun join(before: String): String? {
        val r = computeCompoundJoin(before) ?: return null
        return before.dropLast(r.deleteLength) + r.replacement
    }

    @Test fun joinsTwoWords() =
        assertEquals("ferieplanlegging", join("ferie planlegging"))

    @Test fun usesOnlyTheLastTwoWords() =
        assertEquals("Jeg liker ferieplanlegging", join("Jeg liker ferie planlegging"))

    @Test fun preservesCaseOfSecondWord() =
        assertEquals("FeriePlanering", join("Ferie Planering"))

    @Test fun joinsWordEndingInDigits() =
        assertEquals("sak2024", join("sak 2024"))

    // A trailing space (e.g. left after picking a suggestion) must still join, and is kept.
    @Test fun joinsDespiteTrailingSpace() =
        assertEquals("sykkelkurv ", join("sykkel kurv "))

    @Test fun singleWordDoesNothing() =
        assertNull(join("hello"))

    @Test fun emptyDoesNothing() =
        assertNull(join(""))

    @Test fun singleWordWithTrailingSpaceDoesNothing() =
        assertNull(join("hello "))

    // No real separator: nothing has a non-space immediately after a space.
    @Test fun nonSpaceSeparatorDoesNothing() =
        assertNull(join("ferie-planlegging"))

    // Double space collapses to a single one (rightmost space followed by a non-space).
    @Test fun doubleSpaceCollapsesToOne() =
        assertEquals("ferie planlegging", join("ferie  planlegging"))
}
