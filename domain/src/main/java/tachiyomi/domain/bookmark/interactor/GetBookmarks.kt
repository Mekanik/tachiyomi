package tachiyomi.domain.bookmark.interactor

import tachiyomi.domain.bookmark.repository.BookmarkRepository

class GetBookmarks(
    private val bookmarkRepository: BookmarkRepository,
) {
    suspend fun await(mangaId: Long) =
        run { bookmarkRepository.getAllByMangaId(mangaId) }

    suspend fun awaitWithChapterNumbers(mangaId: Long) =
        run { bookmarkRepository.getWithChapterNumberByMangaId(mangaId) }
}
