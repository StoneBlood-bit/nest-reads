package nest.dto.shoppingcart;

import java.util.List;
import lombok.Data;
import nest.dto.book.BookResponseDto;

@Data
public class ShoppingCartResponseDto {
    private Long id;

    private Long userId;

    private List<BookResponseDto> books;
}
