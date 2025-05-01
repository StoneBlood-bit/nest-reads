package nest.mapper;

import nest.config.MapperConfig;
import nest.dto.user.UserInfoResponseDto;
import nest.dto.user.UserRegistrationRequestDto;
import nest.dto.user.UserRegistrationResponseDto;
import nest.model.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserRegistrationResponseDto toDto(User user);

    User toModel(UserRegistrationRequestDto requestDto);

    UserInfoResponseDto toInfoDto(User user);
}
