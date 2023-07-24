package tachiyomi.domain.bookmark.model

data class BookmarkUpdate(
    val id: Long,
    val note: String? = null,
)
