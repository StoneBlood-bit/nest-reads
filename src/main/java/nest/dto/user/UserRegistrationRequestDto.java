package nest.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegistrationRequestDto {
    @NotBlank
    @Email
    private String email;

    @Size(min = 8, max = 100)
    @NotBlank
    private String password;

    @NotBlank
    private String fullName;
}
