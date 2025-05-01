package nest.controller;

import lombok.RequiredArgsConstructor;
import nest.dto.shoppingcart.ShoppingCartResponseDto;
import nest.model.User;
import nest.service.shoppingcart.ShoppingCartService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/shopping-carts")
@RequiredArgsConstructor
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ShoppingCartResponseDto getByUserId(@AuthenticationPrincipal User user) {
        return shoppingCartService.getByUserId(user.getId());
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/books/{bookId}")
    @ResponseStatus(HttpStatus.OK)
    public void addBookToShoppingCart(@PathVariable Long bookId,
                                      @AuthenticationPrincipal User user
    ) {
        shoppingCartService.addBookToShoppingCart(bookId, user.getId());
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @DeleteMapping("/books/remove/{bookId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeBookFromShoppingCart(
            @PathVariable Long bookId,
            @AuthenticationPrincipal User user
    ) {
        shoppingCartService.removeBookFromShoppingCart(bookId, user.getId());
    }
}
