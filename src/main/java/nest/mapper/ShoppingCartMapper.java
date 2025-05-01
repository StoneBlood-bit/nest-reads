package nest.mapper;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import nest.config.MapperConfig;
import nest.dto.shoppingcart.ShoppingCartResponseDto;
import nest.model.Genre;
import nest.model.ShoppingCart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface ShoppingCartMapper {
    @Mapping(source = "user.id", target = "userId")
    ShoppingCartResponseDto toDto(ShoppingCart shoppingCart);

    default List<String> map(Set<Genre> genres) {
        return genres.stream()
                .map(Genre::getName)
                .collect(Collectors.toList());
    }
}
