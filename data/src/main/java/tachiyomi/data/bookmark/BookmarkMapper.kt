package tachiyomi.data.bookmark

import tachiyomi.domain.bookmark.model.Bookmark
import tachiyomi.domain.bookmark.model.BookmarkWithChapterNumber
import tachiyomi.domain.bookmark.model.BookmarkedPage
import tachiyomi.domain.bookmark.model.MangaWithBookmarks
import tachiyomi.domain.manga.model.MangaCover

object BookmarkMapper {
    fun mapBookmark(
        id: Long,
        mangaId: Long,
        chapterId: Long,
        pageIndex: Long?,
        note: String?,
        lastModifiedAt: Long,
    ): Bookmark = Bookmark(
        id = id,
        mangaId = mangaId,
        chapterId = chapterId,
        pageIndex = pageIndex?.toInt(),
        note = note,
        lastModifiedAt = lastModifiedAt * 1000L,
    )

    fun mapMangaWithBookmarks(
        mangaId: Long,
        mangaTitle: String,
        mangaThumbnailUrl: String?,
        mangaSource: Long,
        isMangaFavorite: Boolean,
        mangaCoverLastModified: Long,
        numberOfBookmarks: Long,
        bookmarkLastModified: Long?,
    ): MangaWithBookmarks = MangaWithBookmarks(
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

    fun mapBookmarkedPage(
        bookmarkId: Long,
        mangaId: Long,
        chapterId: Long,
        pageIndex: Long?,
        mangaTitle: String,
        mangaThumbnailUrl: String?,
        mangaSource: Long,
        isMangaFavorite: Boolean,
        mangaCoverLastModified: Long,
        chapterNumber: Double,
        chapterName: String,
        note: String?,
        lastModifiedAt: Long,
    ): BookmarkedPage = BookmarkedPage(
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

    fun mapBookmarkWithChapterNumber(
        @Suppress("UNUSED_PARAMETER") chapterUrl: String,
        chapterNumber: Double,
        pageIndex: Long?,
        note: String?,
        lastModifiedAt: Long,
    ): BookmarkWithChapterNumber = BookmarkWithChapterNumber(
        chapterNumber = chapterNumber,
        pageIndex = pageIndex?.toInt(),
        note = note,
        lastModifiedAt = lastModifiedAt * 1000L,
    )
}
