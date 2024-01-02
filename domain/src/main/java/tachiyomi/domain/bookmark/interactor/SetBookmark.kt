package tachiyomi.domain.bookmark.interactor

import logcat.LogPriority
import tachiyomi.core.util.lang.withNonCancellableContext
import tachiyomi.core.util.system.logcat
import tachiyomi.domain.bookmark.model.Bookmark
import tachiyomi.domain.bookmark.model.BookmarkUpdate
import tachiyomi.domain.bookmark.repository.BookmarkRepository
import tachiyomi.domain.chapter.model.Chapter
import tachiyomi.domain.chapter.model.ChapterUpdate
import tachiyomi.domain.chapter.repository.ChapterRepository
import java.util.Date

class SetBookmark(
    private val bookmarkRepository: BookmarkRepository,
    private val chapterRepository: ChapterRepository,
) {
    /**
     * Inserts a new bookmark or updates the note of an existing bookmark if one already exists
     * for the given manga, chapter, and page index.
     * Ensures that at most one bookmark per page exists.
     * Updates correspondent chapter bookmark field.
     *
     * @param pageIndex when null, bookmark is considered as chapter bookmark.
     * @param note Optional text note to be associated with the bookmark.
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
                if (pageIndex == null) {
                    chapterRepository.update(ChapterUpdate.bookmarkUpdate(chapterId, true))
                }
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
     * Updates or inserts new bookmarks
     * By default updates correspondent chapter bookmark field.
     */
    suspend fun awaitAll(addedBookmarks: List<Bookmark>, updateChapters: Boolean = true): Result {
        return try {
            if (updateChapters) {
                val chapters = addedBookmarks
                    .mapNotNull {
                        when (it.pageIndex) {
                            null -> ChapterUpdate.bookmarkUpdate(it.chapterId, true)
                            else -> null
                        }
                    }
                if (chapters.isNotEmpty()) {
                    chapterRepository.updateAll(chapters)
                }
            }

            // Check what should be removed to avoid duplication and when update can be skipped.
            val oldBookmarks = addedBookmarks
                .map { it.mangaId }
                .distinct()
                .flatMap { mangaId ->
                    bookmarkRepository.getAllByMangaId(mangaId)
                }
                .associate { Triple(it.mangaId, it.chapterId, it.pageIndex) to it.id }

            val idsToDelete = mutableListOf<Long>()
            val toAdd = mutableListOf<Bookmark>()

            addedBookmarks.forEach { bookmark ->
                val oldBookmarkId =
                    oldBookmarks[Triple(bookmark.mangaId, bookmark.chapterId, bookmark.pageIndex)]
                // Don't delete & insert if there's already an old bookmark and new has no note set.
                // However, this prevents merging or backup restores to remove existing notes.
                // If needed, then add `bookmark.pageIndex == null` to only check for chapter bookmarks.
                val isUpdateNeeded = oldBookmarkId == null || bookmark.note?.isNotBlank() ?: false

                if (isUpdateNeeded) {
                    oldBookmarkId?.let { idsToDelete.add(it) }
                    toAdd.add(bookmark)
                }
            }

            if (toAdd.isNotEmpty()) {
                bookmarkRepository.insertOrReplaceAll(idsToDelete, toAdd)
            }
            Result.Success
        } catch (e: Exception) {
            logcat(LogPriority.ERROR, e)
            Result.InternalError(e)
        }
    }

    suspend fun awaitByChapters(chaptersToBookmark: List<Chapter>): Result {
        return chaptersToBookmark
            .map { Bookmark.create().copy(mangaId = it.mangaId, chapterId = it.id) }
            .let { awaitAll(it) }
    }

    sealed class Result {
        object Success : Result()
        data class InternalError(val error: Throwable) : Result()
    }
}
