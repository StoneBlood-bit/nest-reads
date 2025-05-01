package nest.controller;

import lombok.RequiredArgsConstructor;
import nest.service.image.ImageService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;

    @GetMapping("/image/{bookId}")
    public ResponseEntity<byte[]> getImages(@PathVariable Long bookId) {
        byte[] image = imageService.getImage(bookId);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, "image/png");
        return new ResponseEntity<>(image, headers, HttpStatus.OK);
    }
}
