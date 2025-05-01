package nest.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import nest.dto.order.OrderResponseDto;
import nest.exception.EntityNotFoundException;
import nest.exception.InsufficientTokensException;
import nest.mapper.OrderMapper;
import nest.model.Book;
import nest.model.Order;
import nest.model.ShoppingCart;
import nest.model.User;
import nest.repository.order.OrderRepository;
import nest.repository.shoppingcart.ShoppingCartRepository;
import nest.repository.user.UserRepository;
import nest.service.order.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ShoppingCartRepository shoppingCartRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderMapper orderMapper;

    @Test
    void createOrder_ValidShoppingCartAndTokens_ShouldReturnDto() {
        User user = new User();
        user.setId(1L);
        user.setTokens(5);

        Book book1 = new Book();
        book1.setId(10L);
        Book book2 = new Book();
        book2.setId(20L);
        List<Book> books = new ArrayList<>(List.of(book1, book2));

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCart.setBooks(books);

        Order order = new Order();
        order.setUser(user);
        order.setBooks(books);
        order.setOrderStatus(Order.OrderStatus.NEW);
        order.setCreatedAt(LocalDateTime.now());

        OrderResponseDto orderResponseDto = new OrderResponseDto();

        when(shoppingCartRepository.findByUserId(user.getId()))
                .thenReturn(Optional.of(shoppingCart));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.toDto(order)).thenReturn(orderResponseDto);
        when(userRepository.save(any(User.class))).thenReturn(user);

        OrderResponseDto actual = orderService.createOrder(user);

        assertEquals(orderResponseDto, actual);
        assertEquals(3, user.getTokens());
        assertTrue(shoppingCart.getBooks().isEmpty());
        verify(orderRepository).save(any(Order.class));
        verify(shoppingCartRepository).save(shoppingCart);
    }

    @Test
    void createOrder_ShoppingCartNotExists_ShouldThrowException() {
        User user = new User();
        user.setId(1L);

        when(shoppingCartRepository.findByUserId(user.getId())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> orderService.createOrder(user)
        );

        assertEquals("Can't find shopping cart for user with id: 1", exception.getMessage());
        verifyNoInteractions(orderRepository, orderMapper, userRepository);
    }

    @Test
    void createOrder_TokensNotEnough_ShouldThrowInsufficientTokensException() {
        User user = new User();
        user.setId(1L);
        user.setTokens(1);

        Book book1 = new Book();
        Book book2 = new Book();
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCart.setBooks(new ArrayList<>(List.of(book1, book2)));

        when(shoppingCartRepository.findByUserId(user.getId()))
                .thenReturn(Optional.of(shoppingCart));

        InsufficientTokensException exception = assertThrows(
                InsufficientTokensException.class,
                () -> orderService.createOrder(user)
        );

        assertEquals("User with id 1 doesn't have enough tokens",
                exception.getMessage());
        verifyNoInteractions(orderRepository, orderMapper, userRepository);
    }
}
