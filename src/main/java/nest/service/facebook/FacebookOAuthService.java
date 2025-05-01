package nest.service.facebook;

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
public class FacebookOAuthService {
    private static final String PARAMS_CLIENT_ID = "client_id";
    private static final String PARAMS_REDIRECT_URL = "redirect_uri";
    private static final String PARAMS_CLIENT_SECRET = "client_secret";
    private static final String CODE_NAME = "code";
    private static final String PROPERTY_NAME_FACEBOOK_CLIENT_ID = "facebook.client-id";
    private static final String PROPERTY_NAME_FACEBOOK_REDIRECT_URI = "facebook.redirect-uri";
    private static final String PROPERTY_NAME_FACEBOOK_CLIENT_SECRET = "facebook.client-secret";
    private static final String PROPERTY_NAME_FACEBOOK_TOKEN_URI = "facebook.token-uri";
    private static final String PROPERTY_NAME_FACEBOOK_USER_INFO_URI = "facebook.user-info-uri";

    private static final Logger logger = LogManager.getLogger(AuthenticationService.class);

    private final RestTemplate restTemplate = new RestTemplate();
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final Environment env;
    private final ShoppingCartService shoppingCartService;

    @Transactional
    public String authenticationWithFacebook(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(PARAMS_CLIENT_ID, env.getProperty(PROPERTY_NAME_FACEBOOK_CLIENT_ID));
        params.add(PARAMS_REDIRECT_URL, env.getProperty(PROPERTY_NAME_FACEBOOK_REDIRECT_URI));
        params.add(PARAMS_CLIENT_SECRET, env.getProperty(PROPERTY_NAME_FACEBOOK_CLIENT_SECRET));
        params.add(CODE_NAME, code);

        String tokenUri = env.getProperty(PROPERTY_NAME_FACEBOOK_TOKEN_URI);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUri, request, Map.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new AuthenticationException("Error getting access token from Facebook");
        }

        String accessToken = (String) response.getBody().get("access_token");

        String userInfoUri = env.getProperty(PROPERTY_NAME_FACEBOOK_USER_INFO_URI);
        HttpHeaders userinfoHeaders = new HttpHeaders();
        userinfoHeaders.setBearerAuth(accessToken);

        HttpEntity<Void> userInfoRequest = new HttpEntity<>(userinfoHeaders);
        ResponseEntity<Map> userInfoResponse = restTemplate.exchange(
                userInfoUri, HttpMethod.GET, userInfoRequest, Map.class
        );

        if (!userInfoResponse.getStatusCode().is2xxSuccessful()) {
            throw new AuthenticationException("Error getting user information from Facebook");
        }

        Map<String, Object> userInfo = userInfoResponse.getBody();
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

        logger.error("Method authenticationWithFacebook was called, user: {}", user.getEmail());

        return jwtUtil.generateToken(user.getEmail());
    }
}
