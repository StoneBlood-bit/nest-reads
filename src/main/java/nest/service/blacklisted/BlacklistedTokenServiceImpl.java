package nest.service.blacklisted;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import nest.model.BlacklistedToken;
import nest.repository.blacklisted.BlacklistedTokenRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BlacklistedTokenServiceImpl implements BlacklistedTokenService {
    private final BlacklistedTokenRepository blacklistedTokenRepository;

    @Override
    public void addTokenToBlackList(String token, LocalDateTime expirationTime) {
        BlacklistedToken blacklistedToken = new BlacklistedToken();
        blacklistedToken.setToken(token);
        blacklistedToken.setExpirationTime(expirationTime);
        blacklistedTokenRepository.save(blacklistedToken);
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokenRepository.findByToken(token).isPresent();
    }

    @Override
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void removeExpiredTokens() {
        blacklistedTokenRepository.deleteExpiredToken(LocalDateTime.now());
    }
}
