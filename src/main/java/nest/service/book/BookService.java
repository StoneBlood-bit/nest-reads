package nest.service.book;

import java.util.List;
import nest.dto.book.BookRequestDto;
import nest.dto.book.BookResponseDto;
import nest.dto.book.UpdateBookRequestDto;
import nest.dto.book.UpdateBookResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookResponseDto save(BookRequestDto requestDto, Long userId);

    BookResponseDto getById(Long id);

    Page<BookResponseDto> findAll(
            List<String> genres, String condition,
            String format, String sort, Pageable pageable
    );

    UpdateBookResponseDto update(Long id, UpdateBookRequestDto requestDto);

    void delete(Long id);

    List<String> findAllBookTitles();

    void updateBookImage(Long bookId, byte[] updatedImage);
}
