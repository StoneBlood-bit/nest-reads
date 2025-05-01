package nest.repository.blacklisted;

import java.time.LocalDateTime;
import java.util.Optional;
import nest.model.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, Long> {
    Optional<BlacklistedToken> findByToken(String token);

    @Modifying
    @Query("DELETE FROM BlacklistedToken t WHERE t.expirationTime < :now")
    void deleteExpiredToken(@Param("now") LocalDateTime now);
}
