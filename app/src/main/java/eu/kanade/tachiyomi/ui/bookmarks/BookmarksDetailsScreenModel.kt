package eu.kanade.tachiyomi.ui.bookmarks

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import eu.kanade.core.preference.asState
import eu.kanade.domain.ui.UiPreferences
import eu.kanade.presentation.bookmarks.BookmarksDetailsScreenState
import eu.kanade.tachiyomi.ui.history.HistoryScreenModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import tachiyomi.core.util.lang.launchIO
import tachiyomi.domain.bookmark.interactor.GetBookmarks
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class BookmarksDetailsScreenModel(
    private val mangaId: Long,
    private val getBookmarks: GetBookmarks = Injekt.get(),
    uiPreferences: UiPreferences = Injekt.get(),
) : StateScreenModel<BookmarksDetailsScreenState>(BookmarksDetailsScreenState.Loading) {
    val relativeTime by uiPreferences.relativeTime().asState(coroutineScope)
    val dateFormat by mutableStateOf(UiPreferences.dateFormat(uiPreferences.dateFormat().get()))

    private val _events: Channel<HistoryScreenModel.Event> = Channel(Channel.UNLIMITED)

    // TODO: do I need this, what does it do?
    val events: Flow<HistoryScreenModel.Event> = _events.receiveAsFlow()

    init {
        coroutineScope.launchIO {
            val bookmarks = getBookmarks.await(mangaId)

            // TODO: could be expensive, can move to sql, especially there's already index for this.
            //  And do a single loop grouping.
            //  Or only use when non-default sorting is used (e.g. by last updated)
            val groupsOfBookmarks = bookmarks
                .groupBy({ it.mangaId }, { it })
                .mapValues { (_, pages) ->
                    pages.sortedWith(compareBy({ it.chapterNumber }, { it.pageIndex ?: -1 }))
                }
                .values
                .toList()

            mutableState.update {
                if (bookmarks.isEmpty()) {
                    BookmarksDetailsScreenState.Empty
                } else {
                    BookmarksDetailsScreenState.Success(
                        groupsOfBookmarks = groupsOfBookmarks,
                    )
                }
            }
        }
    }
}
