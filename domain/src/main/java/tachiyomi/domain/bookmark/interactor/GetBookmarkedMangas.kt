package tachiyomi.domain.bookmark.interactor

import tachiyomi.domain.bookmark.repository.BookmarkRepository

class GetBookmarkedMangas(
    private val bookmarkRepository: BookmarkRepository,
) {
    suspend fun await() = run { bookmarkRepository.getMangaWithBookmarks() }
}
