package nest.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import nest.dto.book.BookRequestDto;
import nest.dto.book.BookResponseDto;
import nest.dto.book.UpdateBookRequestDto;
import nest.dto.book.UpdateBookResponseDto;
import nest.exception.EntityNotFoundException;
import nest.exception.InvalidContentTypeException;
import nest.model.User;
import nest.service.book.BookService;
import nest.service.image.ImageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;
    private final ImageService imageService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponseDto create(
            @RequestBody @Valid BookRequestDto requestDto,
            @AuthenticationPrincipal User user) {
        return bookService.save(requestDto, user.getId());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookResponseDto getById(@PathVariable Long id) {
        return bookService.getById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<BookResponseDto> findAll(
            @RequestParam(required = false) List<String> genre,
            @RequestParam(required = false) String condition,
            @RequestParam(required = false) String format,
            @RequestParam(required = false) String sort,
            @PageableDefault(size = 10, sort = "releaseYear", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        return bookService.findAll(genre, condition, format, sort, pageable);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UpdateBookResponseDto update(
            @PathVariable Long id,
            @ModelAttribute @Valid UpdateBookRequestDto updateBookRequestDto
    ) {
        return bookService.update(id, updateBookRequestDto);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        bookService.delete(id);
    }

    @GetMapping("/titles")
    @ResponseStatus(HttpStatus.OK)
    public List<String> findAllBookTitles() {
        return bookService.findAllBookTitles();
    }

    @PutMapping("/{bookId}/image")
    public ResponseEntity<String> updateBookImage(
            @PathVariable Long bookId,
            @RequestParam("image") MultipartFile image,
            HttpServletRequest request) {
        String contentType = request.getContentType();
        if (contentType == null || !contentType.startsWith("multipart/form-data")) {
            throw new InvalidContentTypeException(
                    "Invalid Content-Type: received '" + contentType
                            + "', but expected 'multipart/form-data'");
        }
        try {
            byte[] updatedImage = imageService.updateImage(image, null);
            bookService.updateBookImage(bookId, updatedImage);

            return ResponseEntity.ok("Image updated successfully");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Book not found with id: " + bookId);
        }
    }
}
