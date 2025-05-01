package nest.controller;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import nest.service.google.GoogleOAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth2/callback")
public class OAuth2CallbackController {
    private final GoogleOAuthService googleOAuthService;

    @GetMapping("/google")
    public ResponseEntity<Map<String, String>> handleGoogleCallback(
            @RequestParam("code") String code
    ) {
        String token = googleOAuthService.authenticationWithGoogle(code);

        return ResponseEntity.ok(Map.of("token", token));
    }
}
