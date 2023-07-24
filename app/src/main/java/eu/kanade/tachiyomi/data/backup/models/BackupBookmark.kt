package eu.kanade.tachiyomi.data.backup.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import tachiyomi.domain.bookmark.model.Bookmark

@Serializable
class BackupBookmark(
    // Is number too brittle? Will URL be better? Number is used for migrating, so could be fine. Or is `var url: String,` better?
    @ProtoNumber(1) var chapterNumber: Float,
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

    companion object {
        fun copyFrom(bookmark: Bookmark): BackupBookmark {
            return BackupBookmark(
                chapterNumber = 0F, // TODO??
                pageIndex = bookmark.pageIndex,
                note = bookmark.note,
                lastModifiedAt = bookmark.lastModifiedAt,
            )
        }
    }
}

val backupBookmarkMapper =
    { chapterNumber: Float, pageIndex: Long?, note: String?, lastModifiedAt: Long ->
        BackupBookmark(
            chapterNumber = chapterNumber,
            pageIndex = pageIndex?.toInt(),
            note = note,
            lastModifiedAt = lastModifiedAt,
        )
    }
