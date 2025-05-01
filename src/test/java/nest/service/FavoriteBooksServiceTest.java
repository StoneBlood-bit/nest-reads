package nest.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import nest.dto.book.AddToFavoriteRequestDto;
import nest.dto.book.BookResponseDto;
import nest.exception.BookAlreadyInFavoritesException;
import nest.exception.BookNotInFavoritesException;
import nest.exception.EntityNotFoundException;
import nest.mapper.BookMapper;
import nest.model.Book;
import nest.model.User;
import nest.repository.book.BookRepository;
import nest.repository.user.UserRepository;
import nest.service.book.favorite.FavoriteServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class FavoriteBooksServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private FavoriteServiceImpl favoriteService;

    @Test
    void addBookToFavorite_ShouldAddBookToFavorites() {
        Long userId = 1L;
        Long bookId = 2L;

        User user = new User();
        user.setId(userId);
        Book book = new Book();
        book.setId(bookId);
        AddToFavoriteRequestDto favoriteRequestDto = new AddToFavoriteRequestDto();
        favoriteRequestDto.setBookId(bookId);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        favoriteService.addBookToFavorite(favoriteRequestDto, userId);

        assertTrue(user.getFavoriteBooks().contains(book));
        verify(userRepository).save(user);
    }

    @Test
    void addBookToFavorite_BookAlreadyInFavorites_ShouldThrowException() {
        Long userId = 1L;
        Long bookId = 2L;

        User user = new User();
        user.setId(userId);
        Book book = new Book();
        book.setId(bookId);
        AddToFavoriteRequestDto favoriteRequestDto = new AddToFavoriteRequestDto();
        favoriteRequestDto.setBookId(bookId);

        user.getFavoriteBooks().add(book);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        BookAlreadyInFavoritesException exception = assertThrows(
                BookAlreadyInFavoritesException.class,
                () -> favoriteService.addBookToFavorite(favoriteRequestDto, userId)
        );

        assertEquals("The book is already on your favorite list", exception.getMessage());
    }

    @Test
    void removeBookFromFavorite_ShouldRemoveBookFromFavorites() {
        Long userId = 1L;
        Long bookId = 2L;

        User user = new User();
        user.setId(userId);
        Book book = new Book();
        book.setId(bookId);
        user.getFavoriteBooks().add(book);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        favoriteService.removeBookFromFavorite(bookId, userId);

        assertFalse(user.getFavoriteBooks().contains(book));
        verify(userRepository).save(user);
    }

    @Test
    void removeBookFromFavorite_BookNotInFavorites_ShouldThrowException() {
        Long userId = 1L;
        Long bookId = 2L;

        User user = new User();
        user.setId(userId);
        Book book = new Book();
        book.setId(bookId);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        BookNotInFavoritesException exception = assertThrows(
                BookNotInFavoritesException.class,
                () -> favoriteService.removeBookFromFavorite(bookId, userId)
        );

        assertEquals("The book is not in your favorite list", exception.getMessage());
    }

    @Test
    void getAllBooksFromFavorite_ShouldReturnListOfFavoriteBooks() {
        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        Book book1 = new Book();
        Book book2 = new Book();
        user.getFavoriteBooks().add(book1);
        user.getFavoriteBooks().add(book2);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookMapper.toDto(book1)).thenReturn(new BookResponseDto());
        when(bookMapper.toDto(book2)).thenReturn(new BookResponseDto());

        List<BookResponseDto> result = favoriteService.getAllBooksFromFavorite(userId);

        assertEquals(2, result.size());
        verify(userRepository).findById(userId);
    }

    @Test
    void getAllBooksFromFavorite_UserNotFound_ShouldThrowException() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> favoriteService.getAllBooksFromFavorite(userId)
        );

        assertEquals("Can't find user by id: " + userId, exception.getMessage());
        verify(userRepository).findById(userId);
    }
}
