package eu.kanade.tachiyomi.ui.bookmarks

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.delay
import tachiyomi.domain.bookmark.model.Bookmark
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.i18n.stringResource
import kotlin.time.Duration.Companion.seconds

/**
 * Dialog for creating a new Bookmark, for updating or removing an existing Bookmark.
 */
@Composable
fun EditBookmarkDialog(
    onConfirm: (note: String) -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit,
    bookmark: Bookmark?,
) {
    var bookmarkNoteText by remember { mutableStateOf(bookmark?.note ?: "") }
    // For in-place delete confirmation.
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }
    val saveButtonText = bookmark?.let { stringResource(MR.strings.action_update_bookmark) }
        ?: stringResource(MR.strings.action_add)

    val titleText = bookmark?.let { stringResource(MR.strings.action_update_page_bookmark) }
        ?: stringResource(MR.strings.action_add_page_bookmark)

    AlertDialog(
        title = {
            Text(
                text = if (showDeleteConfirmation) {
                    stringResource(MR.strings.action_delete_bookmark)
                } else {
                    titleText
                },
            )
        },
        text = {
            if (showDeleteConfirmation) {
                Text(text = stringResource(MR.strings.delete_bookmark_confirmation))
            } else {
                OutlinedTextField(
                    modifier = Modifier.focusRequester(focusRequester),
                    value = bookmarkNoteText,
                    onValueChange = { bookmarkNoteText = it },
                    label = { Text(stringResource(MR.strings.bookmark_note_placeholder)) },
                    singleLine = false,
                    maxLines = 10,
                )
            }
        },
        confirmButton = {
            Row {
                if (showDeleteConfirmation) {
                    TextButton(onClick = { showDeleteConfirmation = false }) {
                        Text(text = stringResource(MR.strings.action_cancel))
                    }
                    TextButton(onClick = { onDelete() }) {
                        Text(text = stringResource(MR.strings.action_delete))
                    }
                } else {
                    if (bookmark != null) {
                        TextButton(onClick = { showDeleteConfirmation = true }) {
                            Text(text = stringResource(MR.strings.action_delete))
                        }
                    }
                    TextButton(onClick = { onConfirm(bookmarkNoteText) }) {
                        Text(text = saveButtonText)
                    }
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(MR.strings.action_cancel))
                    }
                }
            }
        },
        onDismissRequest = onDismiss,
    )

    LaunchedEffect(focusRequester) {
        // TODO: https://issuetracker.google.com/issues/204502668
        delay(0.1.seconds)
        focusRequester.requestFocus()
    }
}

@Preview
@Composable
fun EditPageBookmarkDialogPreview() {
    EditBookmarkDialog(
        onConfirm = { },
        onDelete = {},
        onDismiss = {},
        bookmark = Bookmark(1, 1, 1, 10, "ABC", 2),
    )
}
