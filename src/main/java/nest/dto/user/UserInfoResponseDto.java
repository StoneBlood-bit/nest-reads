package nest.dto.user;

import lombok.Data;

@Data
public class UserInfoResponseDto {
    private Long id;
    private String email;
    private String fullName;
    private int tokens;
}
