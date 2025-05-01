package nest.dto.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Data;

@Data
public class BookRequestDto {
    @NotBlank
    private String title;

    @NotBlank
    private String author;

    @NotBlank
    private String condition;

    private int releaseYear;

    private String description;

    private String format;

    @NotEmpty
    private List<Long> genreIds;

}
