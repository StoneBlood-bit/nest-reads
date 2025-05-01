package nest.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import nest.repository.book.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BookRepositoryTest {
    @Mock
    private BookRepository bookRepository;

    @Test
    void findAllBookTitles_ValidData_ShouldReturnTrue() {
        List<String> expectedList = Arrays.asList("First Book", "Second Book");

        when(bookRepository.findAllBookTitles()).thenReturn(expectedList);

        List<String> actualList = bookRepository.findAllBookTitles();

        assertEquals(expectedList, actualList);
    }

    @Test
    void findAllBookTitles_EmptyData_ShouldReturnTrue() {
        List<String> expectedList = Collections.emptyList();

        when(bookRepository.findAllBookTitles()).thenReturn(expectedList);

        List<String> actualList = bookRepository.findAllBookTitles();

        assertEquals(expectedList, actualList);
    }
}
