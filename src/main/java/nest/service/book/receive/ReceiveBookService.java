package nest.service.book.receive;

import java.util.List;
import nest.dto.book.BookResponseDto;

public interface ReceiveBookService {
    List<BookResponseDto> getAllReceivedBooks(Long userId);
}
