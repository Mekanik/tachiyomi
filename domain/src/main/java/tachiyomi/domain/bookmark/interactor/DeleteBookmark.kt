package tachiyomi.domain.bookmark.interactor

import logcat.LogPriority
import tachiyomi.core.util.lang.withNonCancellableContext
import tachiyomi.core.util.system.logcat
import tachiyomi.domain.bookmark.model.BookmarkDelete
import tachiyomi.domain.bookmark.repository.BookmarkRepository
import tachiyomi.domain.chapter.model.Chapter
import tachiyomi.domain.chapter.model.ChapterUpdate
import tachiyomi.domain.chapter.repository.ChapterRepository

class DeleteBookmark(
    private val bookmarkRepository: BookmarkRepository,
    private val chapterRepository: ChapterRepository,
) {
    suspend fun await(mangaId: Long, chapterId: Long, pageIndex: Int?) = withNonCancellableContext {
        try {
            if (pageIndex == null) {
                chapterRepository.update(ChapterUpdate.bookmarkUpdate(chapterId, false))
            }
            bookmarkRepository.delete(
                BookmarkDelete(
                    mangaId = mangaId,
                    chapterId = chapterId,
                    pageIndex = pageIndex,
                ),
            )
            Result.Success
        } catch (e: Exception) {
            logcat(LogPriority.ERROR, e)
            return@withNonCancellableContext Result.InternalError(e)
        }
    }

    suspend fun awaitAll(delete: List<BookmarkDelete>) = withNonCancellableContext {
        try {
            // Not in transaction, but chapters first
            // to not to affect existing chapter-level bookmarks.
            val chapters = delete
                .mapNotNull {
                    when (it.pageIndex) {
                        null -> ChapterUpdate.bookmarkUpdate(it.chapterId, false)
                        else -> null
                    }
                }
            if (chapters.isNotEmpty()) {
                chapterRepository.updateAll(chapters)
            }

            bookmarkRepository.deleteAll(delete)

            Result.Success
        } catch (e: Exception) {
            logcat(LogPriority.ERROR, e)
            return@withNonCancellableContext Result.InternalError(e)
        }
    }

    suspend fun awaitByChapters(chaptersToUnbookmark: List<Chapter>) {
        return chaptersToUnbookmark
            .map { BookmarkDelete(mangaId = it.mangaId, chapterId = it.id) }
            .let { awaitAll(it) }
    }

    suspend fun awaitAllByMangaId(mangaId: Long, updateChapters: Boolean = true) =
        withNonCancellableContext {
            try {
                if (updateChapters) {
                    val unbookmarkChapters =
                        chapterRepository.getBookmarkedChaptersByMangaId(mangaId)
                            .map { ChapterUpdate.bookmarkUpdate(id = it.id, bookmark = false) }
                    if (unbookmarkChapters.isNotEmpty()) {
                        chapterRepository.updateAll(unbookmarkChapters)
                    }
                }

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
