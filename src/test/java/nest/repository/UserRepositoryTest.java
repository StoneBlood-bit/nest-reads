package nest.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Optional;
import nest.model.User;
import nest.repository.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserRepositoryTest {
    @Mock
    private UserRepository userRepository;

    @Test
    void existsByEmail_UserExists_ShouldReturnTrue() {
        String email = "test@example.com";

        when(userRepository.existsByEmail(email)).thenReturn(true);

        boolean actual = userRepository.existsByEmail(email);

        assertTrue(actual);
    }

    @Test
    void existsByEmail_UserNotExists_ShouldReturnFalse() {
        String email = "test@example.com";

        when(userRepository.existsByEmail(email)).thenReturn(false);

        boolean actual = userRepository.existsByEmail(email);

        assertFalse(actual);
    }

    @Test
    void findByEmail_UserExists_ShouldReturnTrue() {
        String email = "test@example.com";

        User user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        Optional<User> actual = userRepository.findByEmail(email);

        assertFalse(actual.isEmpty());
        assertEquals(Optional.of(user), actual);
    }

    @Test
    void findByEmail_UserNotExists_ShouldReturnFalse() {
        String email = "test@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        Optional<User> actual = userRepository.findByEmail(email);

        assertFalse(actual.isPresent());
    }
}
