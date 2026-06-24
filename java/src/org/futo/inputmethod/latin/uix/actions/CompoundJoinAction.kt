package org.futo.inputmethod.latin.uix.actions

import org.futo.inputmethod.latin.R
import org.futo.inputmethod.latin.uix.Action

/**
 * Joins the last two space-separated words into one compound word by removing the
 * space between them, e.g. "ferie planlegging" -> "ferieplanlegging".
 *
 * Why: compounding languages (Norwegian, German, ...) form novel compounds productively,
 * so they can't be predicted or swiped as a single word. This lets the user swipe each
 * component as a normal standalone word and join them after the fact, getting full swipe
 * accuracy on each part. Only handles clean concatenation (no linking morpheme).
 */

private const val CONTEXT_LEN = 64

/** The edit to perform: delete [deleteLength] chars before the cursor and commit [replacement]. */
data class CompoundJoin(val deleteLength: Int, val replacement: String)

/**
 * Given the text immediately before the cursor, finds the rightmost space that is followed by a
 * non-space character and returns the edit that removes just that space (joining whatever follows
 * it onto the word before it), or null if there is no such space.
 *
 * Deleting from the space to the cursor and re-committing what followed preserves any trailing
 * space (e.g. left behind after picking a suggestion) and avoids retyping through autocorrect.
 *
 * Pure (no Android dependencies) so it can be unit-tested directly.
 */
fun computeCompoundJoin(before: CharSequence): CompoundJoin? {
    for (i in before.length - 2 downTo 0) {
        if (before[i] == ' ' && before[i + 1] != ' ') {
            return CompoundJoin(
                deleteLength = before.length - i,
                replacement = before.substring(i + 1)
            )
        }
    }
    return null
}

val CompoundJoinAction = Action(
    icon = R.drawable.link,
    name = R.string.action_compound_join_title,
    simplePressImpl = { manager, _ ->
        val before = manager.getTextBeforeCursor(CONTEXT_LEN) ?: ""
        val join = computeCompoundJoin(before)
        if (join != null) {
            manager.replaceTextBeforeCursor(join.deleteLength, join.replacement)
        }
    },
    windowImpl = null,
)
