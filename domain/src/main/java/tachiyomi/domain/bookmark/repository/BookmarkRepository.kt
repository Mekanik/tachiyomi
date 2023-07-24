package tachiyomi.domain.bookmark.repository

import tachiyomi.domain.bookmark.model.Bookmark
import tachiyomi.domain.bookmark.model.BookmarkUpdate
import tachiyomi.domain.bookmark.model.BookmarkedPage
import tachiyomi.domain.bookmark.model.MangaWithBookmarks

interface BookmarkRepository {
    suspend fun insert(bookmark: Bookmark)

    suspend fun setAll(addedBookmarks: List<Bookmark>)

    suspend fun updatePartial(update: BookmarkUpdate)

    suspend fun delete(bookmarkId: Long)

    suspend fun deleteAllByMangaId(mangaId: Long)

    suspend fun get(id: Long): Bookmark?

    suspend fun get(mangaId: Long, chapterId: Long, pageIndex: Int?): Bookmark?

    suspend fun getBookmarkedPagesByMangaId(mangaId: Long): List<BookmarkedPage>

    suspend fun getBookmarksByMangaId(mangaId: Long): List<Bookmark>

    suspend fun getMangaWithBookmarks(): List<MangaWithBookmarks>
}
