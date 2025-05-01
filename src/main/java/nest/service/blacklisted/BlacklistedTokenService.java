package nest.service.blacklisted;

import java.time.LocalDateTime;

public interface BlacklistedTokenService {
    void addTokenToBlackList(String token, LocalDateTime expirationTime);

    boolean isTokenBlacklisted(String token);

    void removeExpiredTokens();
}
