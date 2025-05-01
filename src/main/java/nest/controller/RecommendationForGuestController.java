package nest.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import nest.dto.book.BookResponseDto;
import nest.service.book.recommendation.BookRecommendationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("guest/recommendations")
@RequiredArgsConstructor
public class RecommendationForGuestController {
    private final BookRecommendationService recommendationService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<BookResponseDto> getRecommendationsForGuest() {
        return recommendationService.getRecommendationsForGuest();
    }
}
