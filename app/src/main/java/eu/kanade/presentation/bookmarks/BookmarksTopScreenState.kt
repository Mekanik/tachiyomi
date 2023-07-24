package eu.kanade.presentation.bookmarks

import androidx.compose.runtime.Immutable
import tachiyomi.domain.bookmark.model.MangaWithBookmarks

sealed class BookmarksTopScreenState {
    @Immutable
    object Loading : BookmarksTopScreenState()

    @Immutable
    object Empty : BookmarksTopScreenState()

    @Immutable
    data class Success(
        val mangaWithBookmarks: List<MangaWithBookmarks>,
    ) : BookmarksTopScreenState()
}
