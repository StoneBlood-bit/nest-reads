package nest.dto.genre;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GenreDto {
    private Long id;

    @NotBlank
    private String name;
}
