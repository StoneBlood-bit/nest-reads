package nest.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Optional;
import nest.model.ShoppingCart;
import nest.model.User;
import nest.repository.shoppingcart.ShoppingCartRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ShoppingCartRepositoryTest {

    @Mock
    private ShoppingCartRepository shoppingCartRepository;

    @Test
    void findByUserId_CartExists_ShouldReturnTrue() {
        User user = new User();
        user.setId(1L);
        ShoppingCart expected = new ShoppingCart();
        expected.setUser(user);

        when(shoppingCartRepository.findByUserId(1L)).thenReturn(Optional.of(expected));

        Optional<ShoppingCart> actual = shoppingCartRepository.findByUserId(1L);

        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
    }

    @Test
    void findByUserId_CartNotExists_ShouldReturnEmptyOptional() {
        Long userId = 2L;

        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.empty());

        Optional<ShoppingCart> actual = shoppingCartRepository.findByUserId(userId);

        assertFalse(actual.isPresent());
    }
}
