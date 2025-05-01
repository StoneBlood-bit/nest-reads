package nest.service.order;

import nest.dto.order.OrderResponseDto;
import nest.model.User;

public interface OrderService {
    OrderResponseDto createOrder(User user);
}
