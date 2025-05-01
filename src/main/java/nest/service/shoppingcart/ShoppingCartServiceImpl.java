package nest.service.shoppingcart;

import lombok.RequiredArgsConstructor;
import nest.dto.shoppingcart.ShoppingCartResponseDto;
import nest.exception.BookNotInShoppingCartException;
import nest.exception.EntityNotFoundException;
import nest.mapper.ShoppingCartMapper;
import nest.model.Book;
import nest.model.ShoppingCart;
import nest.model.User;
import nest.repository.book.BookRepository;
import nest.repository.shoppingcart.ShoppingCartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final BookRepository bookRepository;

    @Override
    public ShoppingCartResponseDto getByUserId(Long userId) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(userId).orElseThrow(
                () -> new EntityNotFoundException(
                        "Can't find shopping cart for user with id: " + userId)
        );
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Transactional
    @Override
    public ShoppingCart createShoppingCart(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        return shoppingCartRepository.save(shoppingCart);
    }

    @Transactional
    @Override
    public void addBookToShoppingCart(Long bookId, Long userId) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(userId).orElseThrow(
                () -> new EntityNotFoundException(
                        "Can't find shopping cart for user with id: " + userId)
        );

        Book book = bookRepository.findById(bookId).orElseThrow(
                () -> new EntityNotFoundException("Can't find book with id: " + bookId)
        );

        if (!shoppingCart.getBooks().contains(book)) {
            shoppingCart.getBooks().add(book);
        }
    }

    @Transactional
    @Override
    public void removeBookFromShoppingCart(Long bookId, Long userId) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(userId).orElseThrow(
                () -> new EntityNotFoundException(
                        "Can't find shopping cart for user with id: " + userId)
        );

        Book book = bookRepository.findById(bookId).orElseThrow(
                () -> new EntityNotFoundException("Can't find book with id: " + bookId)
        );

        if (shoppingCart.getBooks().contains(book)) {
            shoppingCart.getBooks().remove(book);
        } else {
            throw new BookNotInShoppingCartException("Book not found in the shopping cart");
        }
    }
}
