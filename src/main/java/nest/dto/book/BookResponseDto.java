package nest.dto.book;

import java.util.List;
import lombok.Data;

@Data
public class BookResponseDto {
    private Long id;
    private String title;
    private String author;
    private String condition;
    private String description;
    private String slug;
    private String format;
    private List<String> genres;
    private int releaseYear;
}
