package tachiyomi.domain.bookmark.repository

import tachiyomi.domain.bookmark.model.Bookmark
import tachiyomi.domain.bookmark.model.BookmarkDelete
import tachiyomi.domain.bookmark.model.BookmarkUpdate
import tachiyomi.domain.bookmark.model.BookmarkWithChapterNumber
import tachiyomi.domain.bookmark.model.BookmarkedPage
import tachiyomi.domain.bookmark.model.MangaWithBookmarks

interface BookmarkRepository {
    suspend fun get(id: Long): Bookmark?
    suspend fun get(mangaId: Long, chapterId: Long, pageIndex: Int?): Bookmark?
    suspend fun getAllByMangaId(mangaId: Long): List<Bookmark>

    suspend fun getMangaWithBookmarks(): List<MangaWithBookmarks>
    suspend fun getBookmarkedPagesByMangaId(mangaId: Long): List<BookmarkedPage>
    suspend fun getWithChapterNumberByMangaId(mangaId: Long): List<BookmarkWithChapterNumber>

    suspend fun insert(bookmark: Bookmark)
    suspend fun updatePartial(update: BookmarkUpdate)
    suspend fun insertOrReplaceAll(idsToDelete: List<Long>, bookmarksToAdd: List<Bookmark>)

    suspend fun delete(bookmarkId: Long)
    suspend fun delete(delete: BookmarkDelete)
    suspend fun deleteAll(delete: List<BookmarkDelete>)
    suspend fun deleteAllByMangaId(mangaId: Long)
}
