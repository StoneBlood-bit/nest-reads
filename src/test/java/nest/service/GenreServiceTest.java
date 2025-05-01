package nest.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import nest.dto.genre.GenreDto;
import nest.exception.EntityNotFoundException;
import nest.mapper.GenreMapper;
import nest.model.Genre;
import nest.repository.genre.GenreRepository;
import nest.service.genre.GenreServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class GenreServiceTest {
    @InjectMocks
    private GenreServiceImpl genreService;

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private GenreMapper genreMapper;

    @Test
    void save_ValidDto_ShouldReturnDto() {
        GenreDto genreDto = new GenreDto();
        genreDto.setName("Genre");

        Genre genre = new Genre();
        genre.setName(genreDto.getName());

        when(genreMapper.toModel(genreDto)).thenReturn(genre);
        when(genreRepository.save(genre)).thenReturn(genre);
        when(genreMapper.toDto(genre)).thenReturn(genreDto);

        GenreDto actual = genreService.save(genreDto);

        assertEquals(genreDto, actual);

        verifyNoMoreInteractions(genreMapper, genreRepository);
    }

    @Test
    void getById_ValidId_ShouldReturnDto() {
        Long validId = 1L;

        Genre genre = new Genre();
        genre.setId(validId);
        genre.setName("Genre");

        GenreDto genreDto = new GenreDto();
        genreDto.setId(genre.getId());
        genreDto.setName("Genre");

        when(genreRepository.findById(validId)).thenReturn(Optional.of(genre));
        when(genreMapper.toDto(genre)).thenReturn(genreDto);

        GenreDto actual = genreService.getById(validId);

        assertEquals(genreDto, actual);

        verifyNoMoreInteractions(genreRepository, genreMapper);
    }

    @Test
    void getById_InvalidId_ShouldThrowException() {
        Long invalidId = 999L;

        when(genreRepository.findById(invalidId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> genreService.getById(invalidId)
        );

        assertEquals(exception.getMessage(), "Can't find the genre with id: " + invalidId);
    }

    @Test
    void findAll_ValidPageable_ShouldReturnPageOfGenres() {
        Genre genre = new Genre();
        genre.setName("Genre");

        GenreDto genreDto = new GenreDto();
        genreDto.setName(genre.getName());

        Pageable pageable = PageRequest.of(0, 12);
        List<Genre> genres = List.of(genre);
        Page<Genre> genresPage = new PageImpl<>(genres, pageable, genres.size());

        when(genreRepository.findAll(pageable)).thenReturn(genresPage);
        when(genreMapper.toDto(genre)).thenReturn(genreDto);

        Page<GenreDto> actualGenresPage = genreService.findAll(pageable);

        assertThat(actualGenresPage).hasSize(1);
        assertThat(actualGenresPage.getContent().get(0)).isEqualTo(genreDto);

        verifyNoMoreInteractions(genreRepository, genreMapper);
    }

    @Test
    void delete_GenreExists_ShouldDeleteGenre() {
        Long genreId = 1L;

        doNothing().when(genreRepository).deleteById(genreId);

        genreService.delete(genreId);

        verify(genreRepository).deleteById(genreId);
    }

    @Test
    void findByIds_ValidIds_ShouldReturnSetOfGenres() {
        Genre genre1 = new Genre();
        genre1.setId(1L);
        genre1.setName("Fiction");

        Genre genre2 = new Genre();
        genre2.setId(2L);
        genre2.setName("Fantasy");
        List<Long> ids = List.of(1L, 2L);

        when(genreRepository.findAllById(ids)).thenReturn(List.of(genre1, genre2));

        Set<Genre> actual = genreService.findByIds(ids);

        assertEquals(new HashSet<>(List.of(genre1, genre2)), actual);
        verify(genreRepository).findAllById(ids);
    }

    @Test
    void findByIds_NoGenresFound_ShouldReturnEmptySet() {
        List<Long> ids = List.of(10L, 20L);

        when(genreRepository.findAllById(ids)).thenReturn(List.of());

        Set<Genre> actual = genreService.findByIds(ids);

        assertTrue(actual.isEmpty());
        verify(genreRepository).findAllById(ids);
    }
}
