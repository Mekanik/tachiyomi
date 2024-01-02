package eu.kanade.tachiyomi.data.backup.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import tachiyomi.domain.bookmark.model.Bookmark

@Serializable
class BackupBookmark(
    @ProtoNumber(1) var chapterUrl: String,
    @ProtoNumber(2) var pageIndex: Int? = null,
    @ProtoNumber(3) var note: String? = null,
    @ProtoNumber(4) var lastModifiedAt: Long = 0,
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

val backupBookmarkMapper =
    { chapterUrl: String, _: Double, pageIndex: Long?, note: String?, lastModifiedAt: Long ->
        BackupBookmark(
            chapterUrl = chapterUrl,
            pageIndex = pageIndex?.toInt(),
            note = note,
            lastModifiedAt = lastModifiedAt * 1000L,
        )
    }
