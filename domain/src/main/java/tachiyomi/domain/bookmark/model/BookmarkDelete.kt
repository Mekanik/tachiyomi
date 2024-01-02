package tachiyomi.domain.bookmark.model

data class BookmarkDelete(
    val mangaId: Long,
    val chapterId: Long,
    val pageIndex: Int? = null,
)
