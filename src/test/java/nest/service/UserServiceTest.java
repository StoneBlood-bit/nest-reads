package nest.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import nest.dto.user.UserInfoResponseDto;
import nest.dto.user.UserRegistrationRequestDto;
import nest.dto.user.UserRegistrationResponseDto;
import nest.exception.EntityNotFoundException;
import nest.exception.RegistrationException;
import nest.mapper.UserMapper;
import nest.model.ShoppingCart;
import nest.model.User;
import nest.repository.user.UserRepository;
import nest.service.shoppingcart.ShoppingCartService;
import nest.service.user.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ShoppingCartService shoppingCartService;

    @Test
    void register_ValidRequestDto_ShouldReturnResponseDto() {
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto();
        requestDto.setEmail("test@example.com");
        requestDto.setPassword("password");
        requestDto.setFullName("User");

        User user = new User();
        user.setEmail(requestDto.getEmail());
        user.setPassword(requestDto.getPassword());
        user.setFullName(requestDto.getFullName());

        UserRegistrationResponseDto responseDto = new UserRegistrationResponseDto();
        responseDto.setEmail(user.getEmail());
        responseDto.setFullName(user.getFullName());

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);

        when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(false);
        when(userMapper.toModel(requestDto)).thenReturn(user);
        when(passwordEncoder.encode(user.getPassword())).thenReturn("Encoded password");
        when(userRepository.save(user)).thenReturn(user);
        when(shoppingCartService.createShoppingCart(user)).thenReturn(shoppingCart);
        when(userMapper.toDto(user)).thenReturn(responseDto);

        UserRegistrationResponseDto actual = userService.register(requestDto);

        assertThat(actual).isEqualTo(responseDto);

        verifyNoMoreInteractions(
                userRepository, userMapper, passwordEncoder, shoppingCartService
        );
    }

    @Test
    void register_UserAlreadyExists_ShouldThrowRegistrationException() {
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto();
        requestDto.setEmail("test@example.com");
        requestDto.setPassword("password");
        requestDto.setFullName("User");

        when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(true);
        assertThatThrownBy(() -> userService.register(requestDto))
                .isInstanceOf(RegistrationException.class)
                .hasMessageContaining("User with email "
                        + requestDto.getEmail() + " already exists.");

        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getById_ValidId_ShouldReturnResponseDto() {
        Long validId = 1L;

        User user = new User();
        user.setId(validId);
        user.setEmail("test@example.com");
        user.setFullName("User");
        user.setTokens(5);

        UserInfoResponseDto responseDto = new UserInfoResponseDto();
        responseDto.setId(user.getId());
        responseDto.setEmail(user.getEmail());
        responseDto.setFullName(user.getFullName());
        responseDto.setTokens(user.getTokens());

        when(userRepository.findById(validId)).thenReturn(Optional.of(user));
        when(userMapper.toInfoDto(user)).thenReturn(responseDto);

        UserInfoResponseDto actual = userService.getById(validId);

        assertThat(actual).isEqualTo(responseDto);

        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    void getById_InvalidId_ShouldThrowException() {
        Long invalidId = 999L;

        when(userRepository.findById(invalidId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> userService.getById(invalidId)
        );

        assertThat(exception.getMessage())
                .isEqualTo("Can't find user with id: " + invalidId);

        verifyNoMoreInteractions(userRepository);
    }
}
