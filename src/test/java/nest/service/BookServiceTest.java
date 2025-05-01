package nest.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import nest.component.SlugGenerator;
import nest.dto.book.BookRequestDto;
import nest.dto.book.BookResponseDto;
import nest.dto.book.UpdateBookRequestDto;
import nest.dto.book.UpdateBookResponseDto;
import nest.exception.EntityNotFoundException;
import nest.mapper.BookMapper;
import nest.model.Book;
import nest.model.Genre;
import nest.model.User;
import nest.repository.book.BookRepository;
import nest.repository.user.UserRepository;
import nest.service.book.BookServiceImpl;
import nest.service.genre.GenreService;
import nest.service.image.ImageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    @InjectMocks
    private BookServiceImpl bookService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SlugGenerator slugGenerator;

    @Mock
    private GenreService genreService;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private ImageService imageService;

    @Test
    void save_ValidDtoAndUserId_ShouldReturnDto() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setTokens(1);

        Genre genre = new Genre();
        genre.setId(1L);
        genre.setName("Genre");

        BookRequestDto requestDto = new BookRequestDto();
        requestDto.setTitle("Book");
        requestDto.setAuthor("Author");
        requestDto.setCondition("Like New");
        List<Long> genreIds = Arrays.asList(genre.getId());
        requestDto.setGenreIds(genreIds);

        Set<Genre> genres = new HashSet<>(Arrays.asList(genre));
        Book book = new Book();
        book.setTitle(requestDto.getTitle());
        book.setAuthor(requestDto.getAuthor());
        book.setCondition(requestDto.getCondition());
        book.setGenres(genres);
        book.setDonor(user);

        BookResponseDto responseDto = new BookResponseDto();
        responseDto.setTitle(book.getTitle());
        responseDto.setAuthor(book.getAuthor());
        responseDto.setCondition(book.getCondition());
        responseDto.setGenres(List.of("Genre"));

        when(bookMapper.toModel(requestDto)).thenReturn(book);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(responseDto);

        BookResponseDto actual = bookService.save(requestDto, userId);

        assertNotNull(responseDto);
        assertEquals("Book", responseDto.getTitle());
        assertEquals(2, user.getTokens());
        verifyNoMoreInteractions(bookMapper, userRepository, bookRepository);
    }

    @Test
    void save_UserNotFound_ShouldThrowException() {
        Genre genre = new Genre();
        genre.setId(1L);
        genre.setName("Genre");

        BookRequestDto requestDto = new BookRequestDto();
        requestDto.setTitle("Book");
        requestDto.setAuthor("Author");
        requestDto.setCondition("Like New");
        List<Long> genreIds = Arrays.asList(genre.getId());
        requestDto.setGenreIds(genreIds);

        Set<Genre> genres = new HashSet<>(Arrays.asList(genre));
        Book book = new Book();
        book.setTitle(requestDto.getTitle());
        book.setAuthor(requestDto.getAuthor());
        book.setCondition(requestDto.getCondition());
        book.setGenres(genres);

        Long userId = 999L;
        when(bookMapper.toModel(requestDto)).thenReturn(book);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            bookService.save(requestDto, userId);
        });
        assertEquals("Can't find user with id: " + userId, exception.getMessage());
    }

    @Test
    void getById_BookExists_ShouldReturnDto() {
        Genre genre = new Genre();
        genre.setId(1L);
        genre.setName("Genre");

        Set<Genre> genres = new HashSet<>(Arrays.asList(genre));

        Long validBookId = 1L;
        Book book = new Book();
        book.setId(validBookId);
        book.setTitle("Book");
        book.setAuthor("Author");
        book.setGenres(genres);

        BookResponseDto responseDto = new BookResponseDto();
        responseDto.setId(book.getId());
        responseDto.setTitle(book.getTitle());
        responseDto.setAuthor(book.getAuthor());
        responseDto.setGenres(List.of("Genre"));

        when(bookRepository.findById(validBookId)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(responseDto);

        BookResponseDto actual = bookService.getById(validBookId);

        assertThat(actual).isEqualTo(responseDto);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    void getById_InvalidId_ShouldThrowException() {
        Long invalidBookId = 999L;

        when(bookRepository.findById(invalidBookId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookService.getById(invalidBookId)
        );

        assertThat(exception.getMessage()).isEqualTo("Can't find book with id: " + invalidBookId);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    void findAll_WithFormatFilter_ShouldReturnFilteredBooks() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setFormat("PAPERBACK");

        BookResponseDto bookResponseDto = new BookResponseDto();
        bookResponseDto.setId(1L);
        bookResponseDto.setTitle("Test Book");

        List<Book> books = List.of(book);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());

        when(bookRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(bookPage);
        when(bookMapper.toDto(book)).thenReturn(bookResponseDto);

        Page<BookResponseDto> result = bookService.findAll(
                null, null, "PAPERBACK", null, pageable
        );

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void findAll_WithGenreFilter_ShouldReturnFilteredBooks() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");

        BookResponseDto bookResponseDto = new BookResponseDto();
        bookResponseDto.setId(1L);
        bookResponseDto.setTitle("Test Book");

        List<Book> books = List.of(book);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());

        Mockito.when(bookRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(bookPage);
        Mockito.when(bookMapper.toDto(any(Book.class))).thenReturn(bookResponseDto);

        Page<BookResponseDto> result = bookService.findAll(
                List.of("Fantasy"), null, null, null, pageable
        );

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void findAll_WithNoFilters_ShouldReturnAllBooks() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");

        BookResponseDto bookResponseDto = new BookResponseDto();
        bookResponseDto.setId(1L);
        bookResponseDto.setTitle("Test Book");

        List<Book> books = List.of(book);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());

        Mockito.when(bookRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(bookPage);
        Mockito.when(bookMapper.toDto(any(Book.class))).thenReturn(bookResponseDto);

        Page<BookResponseDto> result = bookService.findAll(
                null, null, null, null, pageable
        );

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Book", result.getContent().get(0).getTitle());
    }

    @Test
    void update_WithValidData_ShouldUpdateBook() {
        Long bookId = 1L;
        UpdateBookRequestDto requestDto = new UpdateBookRequestDto();
        requestDto.setCondition("New");
        requestDto.setGenreIds(List.of(2L, 3L));

        Book existingBook = new Book();
        existingBook.setId(bookId);
        existingBook.setAuthor("Author");
        existingBook.setTitle("Title");

        Genre genre1 = new Genre();
        genre1.setId(2L);
        genre1.setName("First");
        Genre genre2 = new Genre();
        genre2.setId(3L);
        genre2.setName("Second");

        Set<Genre> updatedGenres = Set.of(genre1, genre2);
        String newSlug = "author-title-1";

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
        when(genreService.findByIds(requestDto.getGenreIds())).thenReturn(updatedGenres);
        when(slugGenerator.generateSlug(
                existingBook.getAuthor(), existingBook.getTitle(), existingBook.getId()))
                .thenReturn(newSlug);
        when(bookRepository.save(existingBook)).thenReturn(existingBook);
        when(bookMapper.toUpdateDto(existingBook)).thenReturn(new UpdateBookResponseDto());

        UpdateBookResponseDto responseDto = bookService.update(bookId, requestDto);

        assertNotNull(responseDto);
        assertEquals(updatedGenres, existingBook.getGenres());
        assertEquals(newSlug, existingBook.getSlug());

        verify(bookRepository).save(existingBook);
    }

    @Test
    void update_WhenBookNotFound_ShouldThrowException() {
        Long bookId = 1L;
        UpdateBookRequestDto requestDto = new UpdateBookRequestDto();

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookService.update(bookId, requestDto));

        verify(bookRepository, never()).save(any());
    }

    @Test
    void delete_WhenBookExists_ShouldDeleteBook() {
        Long bookId = 1L;

        doNothing().when(bookRepository).deleteById(bookId);

        bookService.delete(bookId);

        verify(bookRepository).deleteById(bookId);
    }

    @Test
    void delete_WhenBookDoesNotExist_ShouldThrowException() {
        Long bookId = 1L;

        doThrow(new EmptyResultDataAccessException(1)).when(bookRepository).deleteById(bookId);

        assertThrows(EmptyResultDataAccessException.class, () -> bookService.delete(bookId));

        verify(bookRepository).deleteById(bookId);
    }

    @Test
    void findAllBookTitles_ShouldReturnTitles() {
        List<String> expectedTitles = Arrays.asList("Book 1", "Book 2", "Book 3");
        when(bookRepository.findAllBookTitles()).thenReturn(expectedTitles);

        List<String> actualTitles = bookService.findAllBookTitles();

        assertEquals(expectedTitles, actualTitles);
        verify(bookRepository).findAllBookTitles();
    }

    @Test
    void findAllBookTitles_WhenNoTitles_ShouldReturnEmptyList() {
        when(bookRepository.findAllBookTitles()).thenReturn(Collections.emptyList());

        List<String> actualTitles = bookService.findAllBookTitles();

        assertTrue(actualTitles.isEmpty());
        verify(bookRepository).findAllBookTitles();
    }

}
