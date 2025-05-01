package nest.service.genre;

import java.util.List;
import java.util.Set;
import nest.dto.genre.GenreDto;
import nest.model.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GenreService {
    GenreDto save(GenreDto genreDto);

    GenreDto getById(Long id);

    Page<GenreDto> findAll(Pageable pageable);

    void delete(Long id);

    Set<Genre> findByIds(List<Long> ids);
}
