package nest.mapper;

import java.util.List;
import java.util.stream.Collectors;
import nest.config.MapperConfig;
import nest.dto.order.OrderResponseDto;
import nest.model.Book;
import nest.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class)
public interface OrderMapper {
    @Mapping(target = "userEmail", source = "user.email")
    @Mapping(target = "bookTitles", source = "books", qualifiedByName = "mapBookTitles")
    OrderResponseDto toDto(Order order);

    @Named("mapBookTitles")
    default List<String> mapBookTitles(List<Book> books) {
        return books.stream()
                .map(Book::getTitle)
                .collect(Collectors.toList());
    }
}
