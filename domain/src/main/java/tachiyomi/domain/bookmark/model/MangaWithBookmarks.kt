package tachiyomi.domain.bookmark.model

import tachiyomi.domain.manga.model.MangaCover

/**
 * Represents a single Manga with information about number of bookmarks.
 */
class MangaWithBookmarks(
    val mangaId: Long,
    val mangaTitle: String,
    val numberOfBookmarks: Long,
    val bookmarkLastModified: Long,
    val coverData: MangaCover,
)
