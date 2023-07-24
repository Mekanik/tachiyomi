package tachiyomi.domain.bookmark.interactor

import logcat.LogPriority
import tachiyomi.core.util.lang.withNonCancellableContext
import tachiyomi.core.util.system.logcat
import tachiyomi.domain.bookmark.repository.BookmarkRepository

class DeleteBookmark(
    private val bookmarkRepository: BookmarkRepository,
) {
    suspend fun await(bookmarkId: Long) = withNonCancellableContext {
        try {
            bookmarkRepository.delete(bookmarkId)
            Result.Success
        } catch (e: Exception) {
            logcat(LogPriority.ERROR, e)
            return@withNonCancellableContext Result.InternalError(e)
        }
    }

    suspend fun await(mangaId: Long, chapterId: Long, pageIndex: Int) =
        withNonCancellableContext {
            try {
                bookmarkRepository.get(mangaId, chapterId, pageIndex)?.let { bm ->
                    bookmarkRepository.delete(bm.id)
                }
                Result.Success
            } catch (e: Exception) {
                logcat(LogPriority.ERROR, e)
                return@withNonCancellableContext Result.InternalError(e)
            }
        }

    suspend fun awaitAllForManga(mangaId: Long) = withNonCancellableContext {
        try {
            bookmarkRepository.deleteAllByMangaId(mangaId)
            Result.Success
        } catch (e: Exception) {
            logcat(LogPriority.ERROR, e)
            return@withNonCancellableContext Result.InternalError(e)
        }
    }

    sealed class Result {
        object Success : Result()
        data class InternalError(val error: Throwable) : Result()
    }
}
