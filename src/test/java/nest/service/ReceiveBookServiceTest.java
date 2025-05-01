package nest.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import nest.dto.book.BookResponseDto;
import nest.exception.EntityNotFoundException;
import nest.mapper.BookMapper;
import nest.model.Book;
import nest.model.User;
import nest.repository.user.UserRepository;
import nest.service.book.receive.ReceiveBookServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ReceiveBookServiceTest {
    @InjectMocks
    private ReceiveBookServiceImpl receiveBookService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookMapper bookMapper;

    @Test
    void getAllDonatedBooks_ValidData_ShouldReturnListOfBooks() {
        Long userId = 1L;
        User user = new User();
        Book book1 = new Book();
        Book book2 = new Book();
        List<Book> receivedBooks = Arrays.asList(book1, book2);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        user.setReceivedBooks(receivedBooks);

        BookResponseDto bookResponseDto1 = new BookResponseDto();
        BookResponseDto bookResponseDto2 = new BookResponseDto();
        when(bookMapper.toDto(book1)).thenReturn(bookResponseDto1);
        when(bookMapper.toDto(book2)).thenReturn(bookResponseDto2);

        List<BookResponseDto> actual = receiveBookService.getAllReceivedBooks(userId);

        assertEquals(2, actual.size());
        assertSame(bookResponseDto1, actual.get(0));
        assertSame(bookResponseDto2, actual.get(1));

        verify(userRepository).findById(userId);
        verify(bookMapper).toDto(book1);
        verify(bookMapper).toDto(book2);
    }

    @Test
    void getAllDonatedBooks_UserNotFound_ShouldThrowException() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> receiveBookService.getAllReceivedBooks(userId)
        );

        assertEquals("Can't find user with id: " + userId, exception.getMessage());
        verify(userRepository).findById(userId);
    }
}
