package tachiyomi.data.bookmark

import tachiyomi.data.DatabaseHandler
import tachiyomi.domain.bookmark.model.Bookmark
import tachiyomi.domain.bookmark.model.BookmarkUpdate
import tachiyomi.domain.bookmark.model.BookmarkedPage
import tachiyomi.domain.bookmark.model.MangaWithBookmarks
import tachiyomi.domain.bookmark.repository.BookmarkRepository

class BookmarkRepositoryImpl(
    private val handler: DatabaseHandler,
) : BookmarkRepository {

    override suspend fun insert(bookmark: Bookmark) {
        handler.await {
            bookmarksQueries.insert(
                mangaId = bookmark.mangaId,
                chapterId = bookmark.chapterId,
                pageIndex = bookmark.pageIndex?.toLong(),
                note = bookmark.note,
                // Seconds in DB.
                lastModifiedAt = bookmark.lastModifiedAt / 1000,
            )
        }
    }

    override suspend fun updatePartial(update: BookmarkUpdate) {
        handler.await {
            bookmarksQueries.update(
                bookmarkId = update.id,
                note = update.note,
            )
        }
    }

    override suspend fun delete(bookmarkId: Long) {
        handler.await { bookmarksQueries.delete(bookmarkId = bookmarkId) }
    }

    override suspend fun setAll(addedBookmarks: List<Bookmark>) {
        // Load existing bookmarks, to remove in case added have a replacement.
        val oldBookmarks = addedBookmarks
            .map { it.mangaId }
            .distinct()
            .flatMap { mangaId ->
                handler.awaitList { bookmarksQueries.getAllByMangaId(mangaId, bookmarkMapper) }
            }.associate { Triple(it.mangaId, it.chapterId, it.pageIndex) to it.id }

        handler.await(inTransaction = true) {
            addedBookmarks.forEach { bookmark ->
                oldBookmarks[Triple(bookmark.mangaId, bookmark.chapterId, bookmark.pageIndex)]
                    ?.let { oldBookmarkId -> bookmarksQueries.delete(oldBookmarkId) }

                bookmarksQueries.insert(
                    mangaId = bookmark.mangaId,
                    chapterId = bookmark.chapterId,
                    pageIndex = bookmark.pageIndex?.toLong(),
                    note = bookmark.note,
                    lastModifiedAt = bookmark.lastModifiedAt,
                )
            }
        }
    }

    override suspend fun deleteAllByMangaId(mangaId: Long) {
        handler.await { bookmarksQueries.deleteAllByMangaId(mangaId = mangaId) }
    }

    override suspend fun get(id: Long): Bookmark? {
        return handler.awaitOneOrNull { bookmarksQueries.getBookmarkById(id, bookmarkMapper) }
    }

    override suspend fun get(mangaId: Long, chapterId: Long, pageIndex: Int?): Bookmark? {
        return handler.awaitOneOrNull {
            bookmarksQueries.getBookmarkByMangaAndChapterPage(
                mangaId,
                chapterId,
                pageIndex?.toLong(),
                bookmarkMapper,
            )
        }
    }

    override suspend fun getBookmarkedPagesByMangaId(mangaId: Long): List<BookmarkedPage> {
        return handler.awaitList {
            bookmarksViewQueries.getBookmarksByManga(mangaId, bookmarkedPageMapper)
        }
    }

    override suspend fun getBookmarksByMangaId(mangaId: Long): List<Bookmark> {
        return handler.awaitList {
            bookmarksQueries.getAllByMangaId(mangaId, bookmarkMapper)
        }
    }

    override suspend fun getMangaWithBookmarks(): List<MangaWithBookmarks> {
        return handler.awaitList {
            mangaWithBookmarksViewQueries.mangaWithBookmarks(mangaWithBookmarksMapper)
        }
    }
}
