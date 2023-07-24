package eu.kanade.presentation.bookmarks

import androidx.compose.runtime.Immutable
import tachiyomi.domain.bookmark.model.BookmarkedPage

sealed class BookmarksDetailsScreenState {
    @Immutable
    object Loading : BookmarksDetailsScreenState()

    @Immutable
    object Empty : BookmarksDetailsScreenState()

    @Immutable
    data class Success(
        // val bookmarks: List<BookmarkedPage>,
        val groupsOfBookmarks: List<List<BookmarkedPage>>,
    ) : BookmarksDetailsScreenState()
}
