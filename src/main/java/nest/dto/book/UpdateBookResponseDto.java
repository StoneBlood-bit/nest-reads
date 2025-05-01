package nest.dto.book;

import java.util.List;
import lombok.Data;

@Data
public class UpdateBookResponseDto {
    private Long id;
    private String title;
    private String author;
    private int releaseYear;
    private String condition;
    private String description;
    private String slug;
    private String format;
    private List<String> genres;
    private byte[] image;
}
