package eu.kanade.tachiyomi.ui.bookmarks

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import eu.kanade.core.preference.asState
import eu.kanade.domain.ui.UiPreferences
import eu.kanade.presentation.bookmarks.BookmarksTopScreenState
import kotlinx.coroutines.flow.update
import tachiyomi.core.util.lang.launchIO
import tachiyomi.domain.bookmark.interactor.GetBookmarks
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class BookmarksTopScreenModel(
    private val getBookmarks: GetBookmarks = Injekt.get(),
    uiPreferences: UiPreferences = Injekt.get(),
) : StateScreenModel<BookmarksTopScreenState>(BookmarksTopScreenState.Loading) {
    val relativeTime by uiPreferences.relativeTime().asState(coroutineScope)
    val dateFormat by mutableStateOf(UiPreferences.dateFormat(uiPreferences.dateFormat().get()))

    init {
        coroutineScope.launchIO {
            val mangaWithBookmarks = getBookmarks.awaitBookmarkedMangas()

            mutableState.update {
                if (mangaWithBookmarks.isEmpty()) {
                    BookmarksTopScreenState.Empty
                } else {
                    BookmarksTopScreenState.Success(
                        mangaWithBookmarks = mangaWithBookmarks,
                    )
                }
            }
        }
    }
}
