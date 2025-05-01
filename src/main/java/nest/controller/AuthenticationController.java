package nest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import nest.dto.user.UserLoginRequestDto;
import nest.dto.user.UserLoginResponseDto;
import nest.dto.user.UserRegistrationRequestDto;
import nest.dto.user.UserRegistrationResponseDto;
import nest.exception.RegistrationException;
import nest.security.AuthenticationService;
import nest.security.CookieUtil;
import nest.security.JwtUtil;
import nest.service.blacklisted.BlacklistedTokenService;
import nest.service.facebook.FacebookOAuthService;
import nest.service.google.GoogleOAuthService;
import nest.service.user.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "User registration",
        description = "Endpoints for registration and authentication user"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {
    public static final int TOKEN_INDEX_SHORT = 7;
    public static final String RESPONSE_CONTENT_TYPE = "text/html;charset=UTF-8";
    public static final String RESPONSE_DEFAULT_URL
            = "https://book-nest-frontend-pearl.vercel.app/redirect";
    private static final Logger logger = LogManager.getLogger(AuthenticationService.class);
    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final GoogleOAuthService googleOAuthService;
    private final FacebookOAuthService facebookOAuthService;
    private final CookieUtil cookieUtil;
    private final JwtUtil jwtUtil;
    private final BlacklistedTokenService blacklistedTokenService;

    @Operation(summary = "login user", description = "user authentication")
    @PostMapping("/login")
    public UserLoginResponseDto login(
            @RequestBody @Valid UserLoginRequestDto requestDto,
            HttpServletResponse response
    ) throws IOException {
        UserLoginResponseDto authenticate =
                authenticationService.authenticate(requestDto, response);
        return authenticate;
    }

    @Operation(summary = "registration user", description = "registration a new user")
    @PostMapping("/registration")
    public UserRegistrationResponseDto register(
            @RequestBody @Valid UserRegistrationRequestDto requestDto
    )
            throws RegistrationException {
        return userService.register(requestDto);
    }

    @Operation(summary = "login user", description = "user authentication from Google")
    @GetMapping("/callback/google")
    public void handleGoogleCallback(
            @RequestParam("code") String code,
            @RequestParam(value = "state", required = false) String redirectUrl,
            HttpServletResponse response) throws IOException {
        logger.info("Received Google callback. Code: {}, State: {}", code, redirectUrl);
        String token = googleOAuthService.authenticationWithGoogle(code);
        cookieUtil.addTokenCookie(response, token);
        sendJsRedirect(response, redirectUrl);
    }

    @Operation(summary = "login user", description = "user authentication from Facebook")
    @GetMapping("/callback/facebook")
    public void handleFacebookCallback(
            @RequestParam("code") String code,
            @RequestParam(value = "state", required = false) String redirectUrl,
            HttpServletResponse response
    ) throws IOException {
        String token = facebookOAuthService.authenticationWithFacebook(code);
        cookieUtil.addTokenCookie(response, token);
        sendJsRedirect(response, redirectUrl);
    }

    @PostMapping("/signout")
    public ResponseEntity<String> signOut(
            @CookieValue(value = "token", required = false) String token,
            HttpServletResponse response
    ) {
        if (token == null) {
            return ResponseEntity.badRequest().body("Invalid token");
        }

        LocalDateTime expirationTime = jwtUtil.getExpiration(token);

        blacklistedTokenService.addTokenToBlackList(token, expirationTime);
        cookieUtil.clearTokenCookie(response);
        return ResponseEntity.ok("User signed out successfully.");
    }

    @GetMapping("/validate-token")
    public ResponseEntity<Void> validateToken(
            @CookieValue(name = "token", required = false) String token
    ) {
        return authenticationService.isTokenValid(token)
                ? ResponseEntity.ok().build()
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    private void sendJsRedirect(
            HttpServletResponse response, String redirectUrl
    ) throws IOException {
        response.setContentType(RESPONSE_CONTENT_TYPE);
        String targetUrl = (redirectUrl != null) ? redirectUrl : RESPONSE_DEFAULT_URL;
        response.getWriter().write("<script>window.location.href='" + targetUrl + "';</script>");
    }
}
