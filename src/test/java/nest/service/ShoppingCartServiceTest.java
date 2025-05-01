package nest.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Optional;
import nest.dto.shoppingcart.ShoppingCartResponseDto;
import nest.exception.BookNotInShoppingCartException;
import nest.exception.EntityNotFoundException;
import nest.mapper.ShoppingCartMapper;
import nest.model.Book;
import nest.model.ShoppingCart;
import nest.model.User;
import nest.repository.book.BookRepository;
import nest.repository.shoppingcart.ShoppingCartRepository;
import nest.service.shoppingcart.ShoppingCartServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ShoppingCartServiceTest {
    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;

    @Mock
    private ShoppingCartRepository shoppingCartRepository;

    @Mock
    private ShoppingCartMapper shoppingCartMapper;

    @Mock
    private BookRepository bookRepository;

    @Test
    void getByUserId_ValidId_ShouldReturnDto() {
        Long validId = 1L;

        User user = new User();
        user.setId(validId);

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);

        ShoppingCartResponseDto responseDto = new ShoppingCartResponseDto();
        responseDto.setUserId(validId);

        when(shoppingCartRepository.findByUserId(validId)).thenReturn(Optional.of(shoppingCart));
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(responseDto);

        ShoppingCartResponseDto actual = shoppingCartService.getByUserId(validId);

        assertThat(actual).isEqualTo(responseDto);
        verifyNoMoreInteractions(shoppingCartRepository, shoppingCartMapper);
    }

    @Test
    void getByUserId_InvalidId_ShouldReturnException() {
        Long invalidId = 999L;

        when(shoppingCartRepository.findByUserId(invalidId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> shoppingCartService.getByUserId(invalidId)
        );

        assertThat(exception.getMessage())
                .isEqualTo("Can't find shopping cart for user with id: " + invalidId);

        verifyNoMoreInteractions(shoppingCartRepository);
    }

    @Test
    void createShoppingCart_ValidUser_ShouldReturnShoppingCart() {
        User user = new User();
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);

        when(shoppingCartRepository.save(any(ShoppingCart.class))).thenReturn(shoppingCart);

        ShoppingCart actual = shoppingCartService.createShoppingCart(user);

        assertEquals(user, actual.getUser());

        verifyNoMoreInteractions(shoppingCartRepository);
    }

    @Test
    void addBookToShoppingCart_ValidUserIdAndBookId_ShouldReturnTrue() {
        Long userId = 1L;
        Long bookId = 10L;
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setBooks(new ArrayList<>());

        Book book = new Book();
        book.setId(bookId);

        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.of(shoppingCart));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        shoppingCartService.addBookToShoppingCart(bookId, userId);

        assertTrue(shoppingCart.getBooks().contains(book));
        verifyNoMoreInteractions(shoppingCartRepository, bookRepository);
    }

    @Test
    void addBookToShoppingCart_CartNotExists_ShouldThrowException() {
        Long userId = 1L;
        Long bookId = 10L;

        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.addBookToShoppingCart(bookId, userId));

        assertEquals("Can't find shopping cart for user with id: " + userId,
                exception.getMessage());

        verifyNoMoreInteractions(shoppingCartRepository);
    }

    @Test
    void addBookToShoppingCart_BookNotExists_ShouldThrowException() {
        Long userId = 1L;
        Long bookId = 10L;
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setBooks(new ArrayList<>());

        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.of(shoppingCart));
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.addBookToShoppingCart(bookId, userId));

        assertEquals("Can't find book with id: " + bookId, exception.getMessage());

        verifyNoMoreInteractions(shoppingCartRepository, bookRepository);
    }

    @Test
    void addBookToShoppingCart_BookAlreadyExists_ShouldNotAddDuplicate() {
        Long bookId = 10L;
        ShoppingCart shoppingCart = new ShoppingCart();
        Book book = new Book();
        book.setId(bookId);
        shoppingCart.setBooks(new ArrayList<>());
        shoppingCart.getBooks().add(book);

        Long userId = 1L;
        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.of(shoppingCart));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        shoppingCartService.addBookToShoppingCart(bookId, userId);

        assertEquals(1, shoppingCart.getBooks().size());

        verifyNoMoreInteractions(shoppingCartRepository, bookRepository);
    }

    @Test
    void removeBookFromShoppingCart_ValidUserIdAndBookId_ShouldReturnTrue() {
        Long bookId = 10L;
        ShoppingCart shoppingCart = new ShoppingCart();
        Book book = new Book();
        book.setId(bookId);
        shoppingCart.setBooks(new ArrayList<>());
        shoppingCart.getBooks().add(book);

        Long userId = 1L;

        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.of(shoppingCart));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        shoppingCartService.removeBookFromShoppingCart(bookId, userId);

        assertFalse(shoppingCart.getBooks().contains(book));

        verifyNoMoreInteractions(shoppingCartRepository, bookRepository);
    }

    @Test
    void removeBookFromShoppingCart_CartNotExists_ShouldThrowException() {
        Long userId = 1L;
        Long bookId = 10L;

        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.removeBookFromShoppingCart(bookId, userId));

        assertEquals("Can't find shopping cart for user with id: " + userId,
                exception.getMessage());

        verifyNoMoreInteractions(shoppingCartRepository);
    }

    @Test
    void removeBookFromShoppingCart_BookNotExistsInRepository_ShouldThrowException() {
        Long userId = 1L;
        Long bookId = 10L;
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setBooks(new ArrayList<>());

        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.of(shoppingCart));
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.removeBookFromShoppingCart(bookId, userId));

        assertEquals("Can't find book with id: " + bookId, exception.getMessage());

        verifyNoMoreInteractions(shoppingCartRepository, bookRepository);
    }

    @Test
    void removeBookFromShoppingCart_BookNotExistsInCart_ShouldThrowException() {
        Long userId = 1L;
        Long bookId = 10L;
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setBooks(new ArrayList<>());

        Book book = new Book();
        book.setId(bookId);

        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.of(shoppingCart));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        BookNotInShoppingCartException exception = assertThrows(
                BookNotInShoppingCartException.class,
                () -> shoppingCartService.removeBookFromShoppingCart(bookId, userId)
        );

        assertEquals("Book not found in the shopping cart", exception.getMessage());

        verifyNoMoreInteractions(shoppingCartRepository, bookRepository);
    }
}
