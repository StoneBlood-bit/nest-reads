package nest.security;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import nest.dto.user.UserLoginRequestDto;
import nest.dto.user.UserLoginResponseDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private static final Logger logger = LogManager.getLogger(AuthenticationService.class);

    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final CookieUtil cookieUtil;

    public UserLoginResponseDto authenticate(
            UserLoginRequestDto requestDto,
            HttpServletResponse response
    ) throws IOException {
        logger.info("authenticate method was called, user: = {}", requestDto.getEmail());
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        requestDto.getEmail(),
                        requestDto.getPassword()
                )
        );

        String token = jwtUtil.generateToken(authentication.getName());
        cookieUtil.addTokenCookie(response, token);
        return new UserLoginResponseDto(token);
    }

    public boolean isTokenValid(String token) {
        return token != null && jwtUtil.isValidToken(token);
    }
}
