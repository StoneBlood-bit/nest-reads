package nest.service.genre;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import nest.dto.genre.GenreDto;
import nest.exception.EntityNotFoundException;
import nest.mapper.GenreMapper;
import nest.model.Genre;
import nest.repository.genre.GenreRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {
    private static final int SIZE_OF_PAGE = 12;
    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;

    @Transactional
    @Override
    public GenreDto save(GenreDto genreDto) {
        Genre genre = genreMapper.toModel(genreDto);
        return genreMapper.toDto(genreRepository.save(genre));
    }

    @Override
    public GenreDto getById(Long id) {
        Genre genre = genreRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find the genre with id: " + id)
        );
        return genreMapper.toDto(genre);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<GenreDto> findAll(Pageable pageable) {
        Pageable adjustedPageable = PageRequest.of(
                pageable.getPageNumber(), SIZE_OF_PAGE, pageable.getSort()
        );
        Page<Genre> genresPage = genreRepository.findAll(adjustedPageable);
        return genresPage.map(genreMapper::toDto);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        genreRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Genre> findByIds(List<Long> ids) {
        return new HashSet<>(genreRepository.findAllById(ids));
    }
}
