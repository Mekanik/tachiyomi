package tachiyomi.domain.bookmark.interactor

import tachiyomi.domain.bookmark.repository.BookmarkRepository

class GetBookmarks(
    private val bookmarkRepository: BookmarkRepository,
) {
    suspend fun awaitBookmarkedMangas() = run { bookmarkRepository.getMangaWithBookmarks() }
    suspend fun await(mangaId: Long) =
        run { bookmarkRepository.getBookmarkedPagesByMangaId(mangaId) }

    suspend fun awaitBookmarks(mangaId: Long) =
        run { bookmarkRepository.getBookmarksByMangaId(mangaId) }
}
