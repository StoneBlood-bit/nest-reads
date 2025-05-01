package nest.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import nest.dto.book.AddToFavoriteRequestDto;
import nest.dto.book.BookResponseDto;
import nest.model.User;
import nest.service.book.favorite.FavoriteService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/favorites")
public class FavoriteController {
    private final FavoriteService favoriteService;

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void addBookToFavorite(
            @RequestBody @Valid AddToFavoriteRequestDto requestDto,
            @AuthenticationPrincipal User user
    ) {
        favoriteService.addBookToFavorite(requestDto, user.getId());
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @DeleteMapping("/{bookId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeBookFromFavorite(
            @PathVariable Long bookId,
            @AuthenticationPrincipal User user
    ) {
        favoriteService.removeBookFromFavorite(bookId, user.getId());
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BookResponseDto> getAllBooksFromFavorite(@AuthenticationPrincipal User user) {
        return favoriteService.getAllBooksFromFavorite(user.getId());
    }
}
