package nest.service.user;

import nest.dto.user.UserInfoResponseDto;
import nest.dto.user.UserRegistrationRequestDto;
import nest.dto.user.UserRegistrationResponseDto;

public interface UserService {
    UserRegistrationResponseDto register(UserRegistrationRequestDto requestDto);

    UserInfoResponseDto getById(Long id);
}
