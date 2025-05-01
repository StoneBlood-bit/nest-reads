package nest.service.google;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import nest.exception.AuthenticationException;
import nest.model.User;
import nest.repository.user.UserRepository;
import nest.security.AuthenticationService;
import nest.security.JwtUtil;
import nest.service.shoppingcart.ShoppingCartService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class GoogleOAuthService {
    private static final Logger logger = LogManager.getLogger(AuthenticationService.class);

    private final RestTemplate restTemplate = new RestTemplate();
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final Environment env;
    private final ShoppingCartService shoppingCartService;

    @Transactional
    public String authenticationWithGoogle(String code) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("code", code);

        String redirectUri = env.getProperty("google.redirect-uri");
        params.add("redirect_uri", redirectUri);

        String clientId = env.getProperty("google.client-id");
        params.add("client_id", clientId);

        String clientSecret = env.getProperty("google.client-secret");
        params.add("client_secret", clientSecret);

        String tokenUri = env.getProperty("google.token-uri");

        logger.info("Sending POST request to Google for access token with parameters: {}",
                params);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUri, request, Map.class);

        logger.info("Response from Google for access token: Status code: {}, Response: {}",
                response.getStatusCode(), response.getBody());

        if (!response.getStatusCode().is2xxSuccessful()) {
            logger.error("Failed to get access token from Google. Status code: {}, Body: {}",
                    response.getStatusCode(), response.getBody());
            throw new AuthenticationException("Error getting access token from Google");
        }

        String accessToken = (String) response.getBody().get("access_token");
        logger.info("Received access token from Google: {}", accessToken);

        String userInfoUri = env.getProperty("google.user-info-uri");
        HttpHeaders userinfoHeaders = new HttpHeaders();
        userinfoHeaders.setBearerAuth(accessToken);

        HttpEntity<Void> userInfoRequest = new HttpEntity<>(userinfoHeaders);
        ResponseEntity<Map> userInfoResponse = restTemplate.exchange(
                userInfoUri, HttpMethod.GET, userInfoRequest, Map.class
        );

        logger.info("Response from Google for user info: Status code: {}, Response: {}",
                userInfoResponse.getStatusCode(), userInfoResponse.getBody());

        if (!userInfoResponse.getStatusCode().is2xxSuccessful()) {
            logger.error("Failed to get user information from Google. Status code: {}, Body: {}",
                    userInfoResponse.getStatusCode(), userInfoResponse.getBody());
            throw new AuthenticationException("Error getting user information");
        }

        Map<String, Object> userInfo = userInfoResponse.getBody();
        logger.info("Received user info: {}", userInfo);

        String email = (String) userInfo.get("email");
        String name = (String) userInfo.get("name");

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setFullName(name);
            newUser.setPassword("");
            newUser.setTokens(0);
            newUser.setRole(User.Role.CUSTOMER);
            newUser.setDeleted(false);
            User savedUser = userRepository.save(newUser);
            shoppingCartService.createShoppingCart(savedUser);
            return savedUser;
        });
        logger.error("Method authenticationWithGoogle was called, user: {}", user.getEmail());

        return jwtUtil.generateToken(user.getEmail());
    }
}
