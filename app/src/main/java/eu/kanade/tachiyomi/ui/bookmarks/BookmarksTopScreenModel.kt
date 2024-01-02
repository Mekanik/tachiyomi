package eu.kanade.tachiyomi.ui.bookmarks

import android.content.Context
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.navigator.Navigator
import eu.kanade.core.preference.asState
import eu.kanade.domain.ui.UiPreferences
import eu.kanade.tachiyomi.ui.reader.ReaderActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import tachiyomi.core.util.lang.launchIO
import tachiyomi.domain.bookmark.interactor.DeleteBookmark
import tachiyomi.domain.bookmark.interactor.GetBookmarkedMangas
import tachiyomi.domain.bookmark.interactor.GetBookmarkedPages
import tachiyomi.domain.bookmark.model.BookmarkedPage
import tachiyomi.domain.bookmark.model.MangaWithBookmarks
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import kotlin.time.Duration.Companion.seconds

class BookmarksTopScreenModel(
    private val getBookmarkedMangas: GetBookmarkedMangas = Injekt.get(),
    private val getBookmarkedPages: GetBookmarkedPages = Injekt.get(),
    private val deleteBookmark: DeleteBookmark = Injekt.get(),
    uiPreferences: UiPreferences = Injekt.get(),
) : StateScreenModel<BookmarksTopScreenModel.State>(State()) {
    val relativeTime by uiPreferences.relativeTime().asState(screenModelScope)
    val dateFormat by mutableStateOf(UiPreferences.dateFormat(uiPreferences.dateFormat().get()))

    init {
        screenModelScope.launchIO {
            loadMangaWithBookmarks()
        }
    }

    fun refresh() {
        screenModelScope.launchIO {
            mutableState.update { it.copy(isRefreshing = true) }
            delay(0.5.seconds)

            state.value.selectedMangaId?.let { mangaId ->
                loadGroupedBookmarks(mangaId)
            } ?: loadMangaWithBookmarks()
        }
    }

    fun openReader(context: Context, mangaId: Long, chapterId: Long, pageIndex: Int?) {
        context.startActivity(ReaderActivity.newIntent(context, mangaId, chapterId, pageIndex))
    }

    fun onMangaSelected(mangaId: Long) {
        screenModelScope.launchIO {
            loadGroupedBookmarks(mangaId)
        }
    }

    fun onNavigationUp(navigator: Navigator) {
        if (state.value.selectedMangaId != null) {
            mutableState.update {
                it.copy(
                    selectedMangaId = null,
                )
            }
        } else {
            navigator.pop()
        }
    }

    fun delete() {
        state.value.selectedMangaId?.let { mangaId ->
            screenModelScope.launchIO {
                deleteBookmark.awaitAllByMangaId(mangaId)
                // Refresh after deletion and return to top level view.
                loadMangaWithBookmarks()
            }
        }
    }

    private suspend fun loadMangaWithBookmarks() {
        val mangaWithBookmarks = getBookmarkedMangas.await()
        mutableState.update {
            it.copy(
                mangaWithBookmarks = mangaWithBookmarks,
                groupsOfBookmarks = listOf(),
                isLoading = false,
                isRefreshing = false,
                selectedMangaId = null,
            )
        }
    }

    private suspend fun loadGroupedBookmarks(mangaId: Long) {
        val bookmarks = getBookmarkedPages.await(mangaId)
        val groupsOfBookmarks = bookmarks
            .sortedWith(
                compareBy(
                    { it.mangaId },
                    { it.chapterNumber },
                    // chapterName is needed for sorting when all numbers are -1 (e.g. Volume 1, Volume 2)
                    { it.chapterName },
                    { it.pageIndex ?: -1 },
                ),
            )

        mutableState.update {
            it.copy(
                groupsOfBookmarks = groupsOfBookmarks,
                isLoading = false,
                isRefreshing = false,
                selectedMangaId = mangaId,
            )
        }
    }

    @Immutable
    data class State(
        val isLoading: Boolean = true,
        val isRefreshing: Boolean = false,
        val mangaWithBookmarks: List<MangaWithBookmarks> = listOf(),
        val groupsOfBookmarks: List<BookmarkedPage> = listOf(),
        val selectedMangaId: Long? = null,
    ) {
        val isEmpty =
            selectedMangaId?.let { groupsOfBookmarks.isEmpty() } ?: mangaWithBookmarks.isEmpty()
    }
}
