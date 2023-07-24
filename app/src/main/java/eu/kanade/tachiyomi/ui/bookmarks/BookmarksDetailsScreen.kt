package eu.kanade.tachiyomi.ui.bookmarks

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.bookmarks.BookmarksDetailsScreenContent
import eu.kanade.presentation.bookmarks.BookmarksDetailsScreenState
import eu.kanade.presentation.components.AppBar
import eu.kanade.presentation.util.Screen
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.ui.reader.ReaderActivity
import tachiyomi.presentation.core.components.material.Scaffold
import tachiyomi.presentation.core.screens.EmptyScreen
import tachiyomi.presentation.core.screens.LoadingScreen

/**
 * Show list of individual bookmarks grouped together.
 * Initially, only support bookmarks for a single manga.
 */
class BookmarksDetailsScreen(
    val mangaId: Long,
) : Screen() {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { BookmarksDetailsScreenModel(mangaId) }
        val state by screenModel.state.collectAsState()
        val context = LocalContext.current

        Scaffold(
            topBar = { scrollBehavior ->
                AppBar(
                    title = "Found bookmarks",
                    navigateUp = navigator::pop,
                    scrollBehavior = scrollBehavior,
                )
            },
        ) { paddingValues ->
            when (val currentState = state) {
                is BookmarksDetailsScreenState.Loading -> {
                    LoadingScreen()
                }

                is BookmarksDetailsScreenState.Empty -> {
                    EmptyScreen(
                        textResource = R.string.information_no_bookmarks,
                        modifier = androidx.compose.ui.Modifier.padding(paddingValues),
                    )
                }

                is BookmarksDetailsScreenState.Success -> {
                    BookmarksDetailsScreenContent(
                        paddingValues = paddingValues,
                        state = currentState,
                        dateRelativeTime = screenModel.relativeTime,
                        dateFormat = screenModel.dateFormat,
                        onBookmarkClick = { mangaId, chapterId, pageIndex ->
                            val intent = ReaderActivity.newIntent(context, mangaId, chapterId, pageIndex)
                            context.startActivity(intent)
                        },
                    )
                }
            }
        }
    }
}
