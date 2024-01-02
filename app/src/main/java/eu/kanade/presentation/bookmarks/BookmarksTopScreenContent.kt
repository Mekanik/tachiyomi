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
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import eu.kanade.presentation.manga.components.MangaCover
import eu.kanade.tachiyomi.ui.bookmarks.BookmarksTopScreenModel
import eu.kanade.tachiyomi.util.lang.toRelativeString
import tachiyomi.domain.bookmark.model.MangaWithBookmarks
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.ScrollbarLazyColumn
import tachiyomi.presentation.core.components.material.SecondaryItemAlpha
import tachiyomi.presentation.core.components.material.padding
import tachiyomi.presentation.core.i18n.stringResource
import tachiyomi.presentation.core.util.plus
import java.text.DateFormat
import java.util.Date

@Composable
fun BookmarksTopScreenContent(
    state: BookmarksTopScreenModel.State,
    paddingValues: PaddingValues,
    relativeTime: Boolean,
    dateFormat: DateFormat,
    onMangaSelected: (Long) -> Unit,
) {
    val statListState = rememberLazyListState()
    ScrollbarLazyColumn(
        contentPadding = paddingValues + PaddingValues(vertical = MaterialTheme.padding.medium),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.padding.medium),
        state = statListState,
    ) {
        items(
            items = state.mangaWithBookmarks,
            key = { "bm-manga-${it.mangaId}" },
        ) {
            MangaWithBookmarksUiItem(
                info = it,
                relativeTime = relativeTime,
                dateFormat = dateFormat,
                onLongClick = {
                    onMangaSelected(it.mangaId)
                },
                onClick = {
                    onMangaSelected(it.mangaId)
                },
                onClickCover = {
                    onMangaSelected(it.mangaId)
                },
                modifier = Modifier.animateItemPlacement(),
            )
        }
    }
}

@Composable
private fun MangaWithBookmarksUiItem(
    info: MangaWithBookmarks,
    relativeTime: Boolean,
    dateFormat: DateFormat,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onClickCover: (() -> Unit)?,
    modifier: Modifier = Modifier,
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
            .height(56.dp)
            .padding(horizontal = MaterialTheme.padding.medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MangaCover.Square(
            modifier = Modifier
                .padding(vertical = 6.dp)
                .fillMaxHeight(),
            data = info.coverData,
            onClick = onClickCover,
        )

        Column(
            modifier = Modifier
                .padding(horizontal = MaterialTheme.padding.medium)
                .weight(1f),
        ) {
            Text(
                text = info.mangaTitle,
                maxLines = 1,
                style = MaterialTheme.typography.bodyMedium,
                overflow = TextOverflow.Ellipsis,
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                var textHeight by remember { mutableIntStateOf(0) }
                Icon(
                    imageVector = Icons.Filled.Bookmark,
                    contentDescription = stringResource(MR.strings.action_filter_bookmarked),
                    modifier = Modifier
                        .sizeIn(maxHeight = with(LocalDensity.current) { textHeight.toDp() - 2.dp }),
                    tint = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = stringResource(
                        MR.strings.bookmark_total_in_manga,
                        info.numberOfBookmarks,
                    ),
                    maxLines = 1,
                    style = MaterialTheme.typography.bodySmall,
                    overflow = TextOverflow.Ellipsis,
                    onTextLayout = { textHeight = it.size.height },
                    modifier = Modifier
                        .weight(weight = 1f, fill = false),
                )
            }

            if (info.bookmarkLastModified > 0) {
                Text(
                    text = stringResource(
                        MR.strings.bookmark_last_updated_in_manga,
                        Date(info.bookmarkLastModified).toRelativeString(context, relativeTime, dateFormat),
                    ),
                    maxLines = 1,
                    style = MaterialTheme.typography.bodySmall,
                    overflow = TextOverflow.Ellipsis,
                    color = LocalContentColor.current.copy(alpha = SecondaryItemAlpha),
                )
            }
        }
    }
}
