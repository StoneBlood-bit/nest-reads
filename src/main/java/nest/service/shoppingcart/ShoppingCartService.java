package nest.service.shoppingcart;

import nest.dto.shoppingcart.ShoppingCartResponseDto;
import nest.model.ShoppingCart;
import nest.model.User;

public interface ShoppingCartService {
    ShoppingCartResponseDto getByUserId(Long userId);

    ShoppingCart createShoppingCart(User user);

    void addBookToShoppingCart(Long bookId, Long userId);

    void removeBookFromShoppingCart(Long bookId, Long userId);
}
