package eu.kanade.tachiyomi.ui.bookmarks

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.bookmarks.BookmarksTopScreenContent
import eu.kanade.presentation.bookmarks.BookmarksTopScreenState
import eu.kanade.presentation.components.AppBar
import eu.kanade.presentation.util.Screen
import eu.kanade.tachiyomi.R
import tachiyomi.presentation.core.components.material.Scaffold
import tachiyomi.presentation.core.screens.EmptyScreen
import tachiyomi.presentation.core.screens.LoadingScreen

/**
 * Top-level screen for bookmarks. Shows aggregated information by manga.
 */
class BookmarksTopScreen : Screen() {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { BookmarksTopScreenModel() }
        val state by screenModel.state.collectAsState()

        Scaffold(
            topBar = { scrollBehavior ->
                AppBar(
                    title = stringResource(R.string.label_bookmarks),
                    navigateUp = navigator::pop,
                    scrollBehavior = scrollBehavior,
                )
            },
        ) { paddingValues ->
            when (val currentState = state) {
                is BookmarksTopScreenState.Loading -> {
                    LoadingScreen()
                }

                is BookmarksTopScreenState.Empty -> {
                    EmptyScreen(
                        textResource = R.string.information_no_bookmarks,
                        modifier = Modifier.padding(paddingValues),
                    )
                }

                is BookmarksTopScreenState.Success -> {
                    BookmarksTopScreenContent(
                        paddingValues = paddingValues,
                        state = currentState,
                        dateRelativeTime = screenModel.relativeTime,
                        dateFormat = screenModel.dateFormat,
                        onMangaSelected = { mangaId ->
                            navigator.push(BookmarksDetailsScreen(mangaId))
                        },
                    )
                }
            }
        }
    }
}
