package tachiyomi.domain.bookmark.interactor

import tachiyomi.domain.bookmark.repository.BookmarkRepository

class GetBookmarkedPages(
    private val bookmarkRepository: BookmarkRepository,
) {
    suspend fun await(mangaId: Long) =
        run { bookmarkRepository.getBookmarkedPagesByMangaId(mangaId) }
}
