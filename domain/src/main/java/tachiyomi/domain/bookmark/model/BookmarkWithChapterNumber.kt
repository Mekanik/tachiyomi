package tachiyomi.domain.bookmark.model

data class BookmarkWithChapterNumber(
    val pageIndex: Int?,
    val note: String?,
    val lastModifiedAt: Long,
    val chapterNumber: Double,
) {
    fun toBookmarkImpl(): Bookmark {
        return Bookmark.create()
            .copy(
                pageIndex = pageIndex,
                note = note,
                lastModifiedAt = lastModifiedAt,
            )
    }
}
