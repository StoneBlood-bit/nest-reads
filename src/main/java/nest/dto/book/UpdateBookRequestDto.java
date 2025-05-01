package nest.dto.book;

import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UpdateBookRequestDto {
    private String title;
    private String author;
    @Positive
    private int releaseYear;
    private String condition;
    private String description;
    private String format;
    private List<Long> genreIds;
    private MultipartFile file;
}
