package tachiyomi.data.bookmark

import tachiyomi.domain.bookmark.model.Bookmark
import tachiyomi.domain.bookmark.model.BookmarkedPage
import tachiyomi.domain.bookmark.model.MangaWithBookmarks
import tachiyomi.domain.manga.model.MangaCover

val bookmarkMapper: (Long, Long, Long, Long?, String?, Long) -> Bookmark =
    { id, mangaId, chapterId, pageIndex, note, lastModifiedAt ->
        Bookmark(
            id = id,
            mangaId = mangaId,
            chapterId = chapterId,
            pageIndex = pageIndex?.toInt(),
            note = note,
            lastModifiedAt = lastModifiedAt * 1000L,
        )
    }

val mangaWithBookmarksMapper: (Long, String, String?, Long, Boolean, Long, Long, Long?) -> MangaWithBookmarks =
    { mangaId, mangaTitle, mangaThumbnailUrl, mangaSource, isMangaFavorite, mangaCoverLastModified, numberOfBookmarks, bookmarkLastModified ->
        MangaWithBookmarks(
            mangaId = mangaId,
            mangaTitle = mangaTitle,
            numberOfBookmarks = numberOfBookmarks,
            bookmarkLastModified = (bookmarkLastModified ?: 0L) * 1000L,
            coverData = MangaCover(
                mangaId = mangaId,
                sourceId = mangaSource,
                isMangaFavorite = isMangaFavorite,
                url = mangaThumbnailUrl,
                lastModified = mangaCoverLastModified,
            ),
        )
    }

val bookmarkedPageMapper: (Long, Long, Long, Long?, String, String?, Long, Boolean, Long, Float, String, String?, Long) -> BookmarkedPage =
    { bookmarkId, mangaId, chapterId, pageIndex, mangaTitle, mangaThumbnailUrl, mangaSource, isMangaFavorite, mangaCoverLastModified, chapterNumber, chapterName, note, lastModifiedAt ->
        BookmarkedPage(
            bookmarkId = bookmarkId,
            mangaId = mangaId,
            chapterId = chapterId,
            pageIndex = pageIndex?.toInt(),
            mangaTitle = mangaTitle,
            chapterNumber = chapterNumber,
            chapterName = chapterName,
            note = note,
            lastModifiedAt = lastModifiedAt * 1000L,
            coverData = MangaCover(
                mangaId = mangaId,
                sourceId = mangaSource,
                isMangaFavorite = isMangaFavorite,
                url = mangaThumbnailUrl,
                lastModified = mangaCoverLastModified,
            ),
        )
    }
