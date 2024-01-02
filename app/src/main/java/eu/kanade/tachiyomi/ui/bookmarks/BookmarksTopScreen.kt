package eu.kanade.tachiyomi.ui.bookmarks

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.bookmarks.BookmarksDetailsScreenContent
import eu.kanade.presentation.bookmarks.BookmarksTopScreenContent
import eu.kanade.presentation.components.AppBar
import eu.kanade.presentation.components.AppBarActions
import eu.kanade.presentation.util.Screen
import eu.kanade.tachiyomi.ui.manga.MangaScreen
import kotlinx.collections.immutable.persistentListOf
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.material.PullRefresh
import tachiyomi.presentation.core.components.material.Scaffold
import tachiyomi.presentation.core.i18n.stringResource
import tachiyomi.presentation.core.screens.EmptyScreen
import tachiyomi.presentation.core.screens.LoadingScreen

/**
 * Top-level screen for bookmarks.
 * Displays aggregated information by manga with details on manga selection.
 */
class BookmarksTopScreen : Screen() {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { BookmarksTopScreenModel() }
        val state by screenModel.state.collectAsState()
        var showRemoveAllConfirmationDialog by remember { mutableStateOf(false) }
        val context = LocalContext.current

        Scaffold(
            topBar = { scrollBehavior ->
                AppBar(
                    title = stringResource(MR.strings.label_bookmarks),
                    navigateUp = { screenModel.onNavigationUp(navigator) },
                    scrollBehavior = scrollBehavior,
                    actions = {
                        state.selectedMangaId?.let {
                            AppBarActions(
                                persistentListOf(
                                    AppBar.Action(
                                        title = stringResource(MR.strings.action_delete_all_bookmarks),
                                        icon = Icons.Outlined.DeleteSweep,
                                        onClick = {
                                            showRemoveAllConfirmationDialog = true
                                        },
                                    ),
                                ),
                            )
                        }
                    },
                )
            },
        ) { contentPadding ->
            when {
                state.isLoading -> LoadingScreen(modifier = Modifier.padding(contentPadding))

                state.isEmpty ->
                    EmptyScreen(
                        stringRes = MR.strings.information_no_bookmarks,
                        modifier = Modifier.padding(contentPadding),
                    )

                else ->
                    PullRefresh(
                        refreshing = state.isRefreshing,
                        onRefresh = screenModel::refresh,
                        indicatorPadding = contentPadding,
                        enabled = { true },
                    ) {
                        when (state.selectedMangaId) {
                            null ->
                                BookmarksTopScreenContent(
                                    paddingValues = contentPadding,
                                    state = state,
                                    relativeTime = screenModel.relativeTime,
                                    dateFormat = screenModel.dateFormat,
                                    onMangaSelected = screenModel::onMangaSelected,
                                )

                            else -> BookmarksDetailsScreenContent(
                                paddingValues = contentPadding,
                                state = state,
                                relativeTime = screenModel.relativeTime,
                                dateFormat = screenModel.dateFormat,
                                onBookmarkClick = { mangaId, chapterId, pageIndex ->
                                    screenModel.openReader(
                                        context,
                                        mangaId,
                                        chapterId,
                                        pageIndex,
                                    )
                                },
                                onMangaClick = { mangaId ->
                                    navigator.push(MangaScreen(mangaId))
                                },
                            )
                        }
                    }
            }
        }

        if (showRemoveAllConfirmationDialog) {
            BookmarksDeleteAllDialog(
                onDelete = {
                    screenModel.delete()
                    showRemoveAllConfirmationDialog = false
                },
                onDismissRequest = { showRemoveAllConfirmationDialog = false },
            )
        }
    }
}

@Composable
fun BookmarksDeleteAllDialog(
    onDismissRequest: () -> Unit,
    onDelete: () -> Unit,
) {
    AlertDialog(
        title = {
            Text(text = stringResource(MR.strings.action_delete_all_bookmarks))
        },
        text = {
            Text(text = stringResource(MR.strings.bookmark_delete_manga_confirmation))
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onDelete) {
                Text(text = stringResource(MR.strings.action_delete))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(MR.strings.action_cancel))
            }
        },
    )
}
