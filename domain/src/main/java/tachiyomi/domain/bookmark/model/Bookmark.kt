package tachiyomi.domain.bookmark.model

import java.util.Date

data class Bookmark(
    val id: Long,
    val mangaId: Long,
    val chapterId: Long,
    /**
     * null is for chapter-level bookmark. Currently only non-null values are supported.
     */
    val pageIndex: Int?,
    val note: String?,
    val lastModifiedAt: Long,
) {
    companion object {
        fun create() = Bookmark(
            id = -1,
            mangaId = -1,
            chapterId = -1,
            pageIndex = null,
            note = null,
            lastModifiedAt = Date().time,
        )
    }
}
