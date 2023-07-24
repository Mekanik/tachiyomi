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
import androidx.compose.foundation.lazy.items
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
import eu.kanade.presentation.manga.components.MangaCover
import eu.kanade.tachiyomi.R
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
    state: BookmarksDetailsScreenState.Success,
    paddingValues: PaddingValues,
    dateRelativeTime: Int,
    dateFormat: DateFormat,
    onBookmarkClick: (mangaId: Long, chapterId: Long, pageIndex: Int?) -> Unit,
) {
    val statListState = rememberLazyListState()

    ScrollbarLazyColumn(
        contentPadding = paddingValues + PaddingValues(vertical = MaterialTheme.padding.medium),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.padding.tiny),
        state = statListState,
    ) {
        items(
            items = state.groupsOfBookmarks,
            key = { "bookmark-header-${it[0].mangaId}" },
        ) { bookmarks ->
            MangaCoverUiItem(
                Modifier.animateItemPlacement(),
                bookmarks[0].coverData,
                bookmarks[0].mangaTitle,
            )

            Spacer(modifier = Modifier.height(MaterialTheme.padding.tiny))

            this@ScrollbarLazyColumn.items(
                items = bookmarks,
                key = { "bookmark-id-${it.bookmarkId}" },
            ) { bookmark ->
                BookmarkUiItem(
                    modifier = Modifier.animateItemPlacement(),
                    info = bookmark,
                    dateRelativeTime = dateRelativeTime,
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
    modifier: Modifier,
    coverData: CoverData,
    title: String,
) {
    Row(
        modifier = modifier
            .combinedClickable(
                onClick = {},
                onLongClick = {},
            )
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
    modifier: Modifier,
    info: BookmarkedPage,
    dateRelativeTime: Int,
    dateFormat: DateFormat,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
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
                vertical = MaterialTheme.padding.tiny,
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
                        stringResource(
                            R.string.bookmark_page_number,
                            i + 1,
                        )
                    } ?: stringResource(R.string.bookmark_chapter),
                    style = MaterialTheme.typography.bodySmall,
                    color = LocalContentColor.current.copy(alpha = SecondaryItemAlpha),
                )

                Text(
                    text =
                    Date(info.lastModifiedAt).toRelativeString(
                        context,
                        dateRelativeTime,
                        dateFormat,
                    ),
                    maxLines = 1,
                    style = MaterialTheme.typography.bodySmall,
                    overflow = TextOverflow.Ellipsis,
                    color = LocalContentColor.current.copy(alpha = SecondaryItemAlpha),
                )
            }
            info.note?.let { text ->
                Text(
                    text = text,
                    maxLines = 2,
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
            note = "Very long note here, ....sdf ljsadf kaslkdfjlkjlkdf , oh so long ,asdkl jaskdjlkajsdlklkjlksdf lkasfd ABC",
            bookmarkId = 1,
            pageIndex = 12,
            chapterName = "Chapte sadfjhks dfjksad kfjhksjdhf kjhsdfkj hkfdhkajdfh r",
            mangaId = 1,
            chapterId = 12,
            chapterNumber = 1.0F,
            coverData = CoverData(
                mangaId = 1,
                isMangaFavorite = true,
                lastModified = 1,
                sourceId = 1,
                url = null,
            ),
            mangaTitle = "Manga",
        ),
        dateRelativeTime = 1,
        dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault()),
        onClick = {},
        onLongClick = {},
    )
}
