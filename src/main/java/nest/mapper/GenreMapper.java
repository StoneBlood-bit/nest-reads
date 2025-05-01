package nest.mapper;

import nest.config.MapperConfig;
import nest.dto.genre.GenreDto;
import nest.model.Genre;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface GenreMapper {
    Genre toModel(GenreDto genreDto);

    GenreDto toDto(Genre genre);
}
