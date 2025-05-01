package nest.dto.order;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import nest.model.Order;

@Data
public class OrderResponseDto {
    private Long id;
    private String userEmail;
    private List<String> bookTitles;
    private Order.OrderStatus orderStatus;
    private LocalDateTime createdAt;
}
