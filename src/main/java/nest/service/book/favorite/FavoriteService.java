package nest.service.book.favorite;

import java.util.List;
import nest.dto.book.AddToFavoriteRequestDto;
import nest.dto.book.BookResponseDto;

public interface FavoriteService {
    void addBookToFavorite(AddToFavoriteRequestDto favoriteRequestDto, Long userId);

    void removeBookFromFavorite(Long bookId, Long userId);

    List<BookResponseDto> getAllBooksFromFavorite(Long userId);
}
