package nest.service.book.recommendation;

import java.util.List;
import nest.dto.book.BookResponseDto;

public interface BookRecommendationService {
    List<BookResponseDto> getRecommendationsForUser(Long userId);

    public List<BookResponseDto> getRecommendationsForGuest();
}
