package tachiyomi.domain.bookmark.interactor

import logcat.LogPriority
import tachiyomi.core.util.lang.withNonCancellableContext
import tachiyomi.core.util.system.logcat
import tachiyomi.domain.bookmark.model.Bookmark
import tachiyomi.domain.bookmark.model.BookmarkUpdate
import tachiyomi.domain.bookmark.repository.BookmarkRepository
import java.util.Date

class SetBookmark(
    private val bookmarkRepository: BookmarkRepository,
) {
    /**
     * Inserts a new bookmark or updates the note of an existing bookmark if one already exists
     * for the given manga, chapter, and page index.
     * Ensures that at most one bookmark per page exists.
     *
     * @param note Optional text note to be associated with the bookmark.
     * @return A [Result] indicating the success or failure of the operation.
     */
    suspend fun await(
        mangaId: Long,
        chapterId: Long,
        pageIndex: Int?,
        note: String?,
        lastModifiedAt: Long? = null,
    ): Result =
        withNonCancellableContext {
            try {
                val existingBookmark = bookmarkRepository.get(mangaId, chapterId, pageIndex)
                if (existingBookmark != null) {
                    bookmarkRepository.updatePartial(
                        BookmarkUpdate(
                            id = existingBookmark.id,
                            note = note,
                        ),
                    )
                } else {
                    val newBookmark =
                        Bookmark.create()
                            .copy(
                                mangaId = mangaId,
                                chapterId = chapterId,
                                pageIndex = pageIndex,
                                note = note,
                                lastModifiedAt = lastModifiedAt ?: Date().time,
                            )
                    bookmarkRepository.insert(newBookmark)
                }
                Result.Success
            } catch (e: Exception) {
                logcat(LogPriority.ERROR, e)
                Result.InternalError(e)
            }
        }

    /**
     * Inserts a new bookmark or updates the note and lastModifiedAt
     * of an existing bookmark if one already exists.
     *
     * @return A [Result] indicating the success or failure of the operation.
     */
    suspend fun awaitAll(addedBookmarks: List<Bookmark>): Result {
        return try {
            bookmarkRepository.setAll(addedBookmarks)
            Result.Success
        } catch (e: Exception) {
            logcat(LogPriority.ERROR, e)
            Result.InternalError(e)
        }
    }

    sealed class Result {
        object Success : Result()
        data class InternalError(val error: Throwable) : Result()
    }
}
