package nest.service.user;

import lombok.RequiredArgsConstructor;
import nest.dto.user.UserInfoResponseDto;
import nest.dto.user.UserRegistrationRequestDto;
import nest.dto.user.UserRegistrationResponseDto;
import nest.exception.EntityNotFoundException;
import nest.exception.RegistrationException;
import nest.mapper.UserMapper;
import nest.model.ShoppingCart;
import nest.model.User;
import nest.repository.user.UserRepository;
import nest.security.AuthenticationService;
import nest.service.shoppingcart.ShoppingCartService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final Logger logger = LogManager.getLogger(AuthenticationService.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final ShoppingCartService shoppingCartService;

    @Override
    @Transactional
    public UserRegistrationResponseDto register(
            UserRegistrationRequestDto requestDto
    ) {
        logger.info("Method register was called, user: {}", requestDto.getEmail());
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RegistrationException("User with email "
                    + requestDto.getEmail() + " already exists.");
        }
        User user = userMapper.toModel(requestDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(User.Role.CUSTOMER);

        userRepository.save(user);
        logger.info("User ID: {}", user.getId());

        ShoppingCart shoppingCart = shoppingCartService.createShoppingCart(user);
        return userMapper.toDto(user);
    }

    @Transactional(readOnly = true)
    @Override
    public UserInfoResponseDto getById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find user with id: " + id)
        );
        return userMapper.toInfoDto(user);
    }
}
