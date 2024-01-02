package eu.kanade.presentation.bookmarks

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import eu.kanade.presentation.manga.components.MangaCover
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.ui.bookmarks.BookmarksTopScreenModel
import eu.kanade.tachiyomi.util.lang.toRelativeString
import tachiyomi.domain.bookmark.model.BookmarkedPage
import tachiyomi.presentation.core.components.ScrollbarLazyColumn
import tachiyomi.presentation.core.components.material.SecondaryItemAlpha
import tachiyomi.presentation.core.components.material.padding
import tachiyomi.presentation.core.util.plus
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import tachiyomi.domain.manga.model.MangaCover as CoverData

@Composable
fun BookmarksDetailsScreenContent(
    state: BookmarksTopScreenModel.State,
    paddingValues: PaddingValues,
    relativeTime: Boolean,
    dateFormat: DateFormat,
    onBookmarkClick: (mangaId: Long, chapterId: Long, pageIndex: Int?) -> Unit,
    onMangaClick: (mangaId: Long) -> Unit,
) {
    val statListState = rememberLazyListState()

    ScrollbarLazyColumn(
        contentPadding = paddingValues + PaddingValues(vertical = MaterialTheme.padding.medium),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.padding.extraSmall),
        state = statListState,
    ) {
        state.groupsOfBookmarks.fastForEachIndexed { i, bookmark ->
            // Header:
            if (i == 0 || bookmark.mangaId != state.groupsOfBookmarks[i - 1].mangaId) {
                item(
                    key = "bm-header-${bookmark.mangaId}",
                    contentType = "header",
                ) {
                    MangaCoverUiItem(
                        bookmark.coverData,
                        bookmark.mangaTitle,
                        onMangaClick = { onMangaClick(bookmark.mangaId) },
                        Modifier.animateItemPlacement(),
                    )

                    Spacer(modifier = Modifier.height(MaterialTheme.padding.extraSmall))
                }
            }

            item(key = "bm-id-${bookmark.bookmarkId}") {
                BookmarkUiItem(
                    modifier = Modifier.animateItemPlacement(),
                    info = bookmark,
                    relativeTime = relativeTime,
                    dateFormat = dateFormat,
                    onLongClick = {},
                    onClick = {
                        onBookmarkClick(
                            bookmark.mangaId,
                            bookmark.chapterId,
                            bookmark.pageIndex,
                        )
                    },
                )
            }
        }
    }
}

@Composable
fun MangaCoverUiItem(
    coverData: CoverData,
    title: String,
    onMangaClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .combinedClickable(onClick = onMangaClick)
            .height(56.dp)
            .padding(horizontal = MaterialTheme.padding.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.padding.medium),
    ) {
        MangaCover.Square(
            modifier = Modifier
                .padding(vertical = 6.dp)
                .fillMaxHeight(),
            data = coverData,
        )

        Text(
            text = title,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(weight = 1f, fill = true),
        )
    }
}

@Composable
private fun BookmarkUiItem(
    info: BookmarkedPage,
    relativeTime: Boolean,
    dateFormat: DateFormat,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val maxLinesForNoteText = 5
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current

    Row(
        modifier = modifier
            .combinedClickable(
                onClick = onClick,
                onLongClick = {
                    onLongClick()
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                },
            )
            .padding(
                vertical = MaterialTheme.padding.extraSmall,
                horizontal = MaterialTheme.padding.medium,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .weight(1f),
        ) {
            Text(
                text = info.chapterName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.padding.small),
            ) {
                Text(
                    text = info.pageIndex?.let { i ->
                        stringResource(R.string.bookmark_page_number, i + 1)
                    } ?: stringResource(R.string.bookmark_chapter),
                    style = MaterialTheme.typography.bodySmall,
                    color = LocalContentColor.current.copy(alpha = SecondaryItemAlpha),
                )

                Text(
                    text = Date(info.lastModifiedAt).toRelativeString(context, relativeTime, dateFormat),
                    maxLines = 1,
                    style = MaterialTheme.typography.bodySmall,
                    overflow = TextOverflow.Ellipsis,
                    color = LocalContentColor.current.copy(alpha = SecondaryItemAlpha),
                )
            }
            info.note?.takeIf { it.isNotBlank() }?.let { text ->
                Text(
                    text = text,
                    maxLines = maxLinesForNoteText,
                    style = MaterialTheme.typography.bodySmall,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Preview
@Composable
private fun BookmarkUiItemPreview() {
    BookmarkUiItem(
        modifier = Modifier,
        info = BookmarkedPage(
            lastModifiedAt = 123123,
            note = "Very long note here, ....sdf ljsadf kaslkdfjlkjlkdf , long ,asdkl jaskdjlkajsdlklkjlksdf lasfd ABC",
            bookmarkId = 1,
            pageIndex = 12,
            chapterName = "Chapte sadfjhks dfjksad kfjhksjdhf kjhsdfkj hkfdhkajdfh r",
            mangaId = 1,
            chapterId = 12,
            chapterNumber = 1.0,
            coverData = CoverData(
                mangaId = 1,
                isMangaFavorite = true,
                lastModified = 1,
                sourceId = 1,
                url = null,
            ),
            mangaTitle = "Manga",
        ),
        relativeTime = true,
        dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault()),
        onClick = {},
        onLongClick = {},
    )
}
