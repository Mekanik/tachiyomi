package tachiyomi.data.bookmark

import tachiyomi.data.Database
import tachiyomi.data.DatabaseHandler
import tachiyomi.domain.bookmark.model.Bookmark
import tachiyomi.domain.bookmark.model.BookmarkDelete
import tachiyomi.domain.bookmark.model.BookmarkUpdate
import tachiyomi.domain.bookmark.model.BookmarkWithChapterNumber
import tachiyomi.domain.bookmark.model.BookmarkedPage
import tachiyomi.domain.bookmark.model.MangaWithBookmarks
import tachiyomi.domain.bookmark.repository.BookmarkRepository

class BookmarkRepositoryImpl(
    private val handler: DatabaseHandler,
) : BookmarkRepository {
    override suspend fun get(id: Long): Bookmark? {
        return handler.awaitOneOrNull { bookmarksQueries.getBookmarkById(id, BookmarkMapper::mapBookmark) }
    }

    override suspend fun get(mangaId: Long, chapterId: Long, pageIndex: Int?): Bookmark? {
        return handler.awaitOneOrNull {
            bookmarksQueries.getBookmarkByMangaAndChapterPage(
                mangaId,
                chapterId,
                pageIndex?.toLong(),
                BookmarkMapper::mapBookmark,
            )
        }
    }

    override suspend fun getAllByMangaId(mangaId: Long): List<Bookmark> {
        return handler.awaitList {
            bookmarksQueries.getAllByMangaId(mangaId, BookmarkMapper::mapBookmark)
        }
    }

    override suspend fun getMangaWithBookmarks(): List<MangaWithBookmarks> {
        return handler.awaitList {
            mangaWithBookmarksViewQueries.mangaWithBookmarks(BookmarkMapper::mapMangaWithBookmarks)
        }
    }

    override suspend fun getBookmarkedPagesByMangaId(mangaId: Long): List<BookmarkedPage> {
        return handler.awaitList {
            bookmarksViewQueries.getBookmarksByManga(mangaId, BookmarkMapper::mapBookmarkedPage)
        }
    }

    override suspend fun getWithChapterNumberByMangaId(mangaId: Long): List<BookmarkWithChapterNumber> {
        return handler.awaitList {
            bookmarksQueries.getWithChapterInfoByMangaId(mangaId, BookmarkMapper::mapBookmarkWithChapterNumber)
        }
    }

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

    override suspend fun insertOrReplaceAll(
        idsToDelete: List<Long>,
        bookmarksToAdd: List<Bookmark>,
    ) {
        handler.await(inTransaction = true) {
            idsToDelete.forEach { bookmarkId -> bookmarksQueries.delete(bookmarkId) }

            bookmarksToAdd.forEach { bookmark ->
                bookmarksQueries.insert(
                    mangaId = bookmark.mangaId,
                    chapterId = bookmark.chapterId,
                    pageIndex = bookmark.pageIndex?.toLong(),
                    note = bookmark.note,
                    lastModifiedAt = bookmark.lastModifiedAt / 1000,
                )
            }
        }
    }

    override suspend fun delete(bookmarkId: Long) {
        handler.await { bookmarksQueries.delete(bookmarkId = bookmarkId) }
    }

    override suspend fun delete(delete: BookmarkDelete) {
        handler.await { deleteBlocking(delete) }
    }

    override suspend fun deleteAll(delete: List<BookmarkDelete>) {
        handler.await(inTransaction = true) {
            for (bookmark in delete) {
                deleteBlocking(bookmark)
            }
        }
    }

    override suspend fun deleteAllByMangaId(mangaId: Long) {
        handler.await { bookmarksQueries.deleteAllByMangaId(mangaId = mangaId) }
    }

    private fun Database.deleteBlocking(delete: BookmarkDelete) {
        bookmarksQueries.deleteByMangaAndChapterPage(
            mangaId = delete.mangaId,
            chapterId = delete.chapterId,
            pageIndex = delete.pageIndex?.toLong(),
        )
    }
}
