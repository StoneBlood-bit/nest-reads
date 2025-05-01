package nest.service.book.donate;

import java.util.List;
import nest.dto.book.BookResponseDto;

public interface DonateBookService {
    List<BookResponseDto> getAllDonatedBooks(Long userId);
}
