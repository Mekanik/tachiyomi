package tachiyomi.domain.bookmark.model

import tachiyomi.domain.manga.model.MangaCover

/**
 * Represents a single bookmarked page with information
 * about manga, chapter and bookmark.
 */
class BookmarkedPage(
    val bookmarkId: Long,
    val mangaId: Long,
    val chapterId: Long,
    val pageIndex: Int?,
    val mangaTitle: String,
    val chapterNumber: Float,
    val chapterName: String,
    val note: String?,
    val lastModifiedAt: Long,
    val coverData: MangaCover,
)
