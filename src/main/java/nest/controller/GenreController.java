package nest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nest.dto.genre.GenreDto;
import nest.service.genre.GenreService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Genre manager", description = "Endpoints for maneging book`s genres")
@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {
    private final GenreService genreService;

    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Create a new genre", description = "Create a new genre")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GenreDto create(@RequestBody @Valid GenreDto genreDto) {
        return genreService.save(genreDto);
    }

    @Operation(
            summary = "Get genre by id",
            description = "Find a genre with a passed id"
    )
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public GenreDto findById(@PathVariable Long id) {
        return genreService.getById(id);
    }

    @Operation(summary = "Get all genres", description = "Get list of all available genres")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public Page<GenreDto> findAll(Pageable pageable) {
        return genreService.findAll(pageable);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @Operation(
            summary = "Delete a genre",
            description = "Delete a genre with a passed id"
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        genreService.delete(id);
    }
}
