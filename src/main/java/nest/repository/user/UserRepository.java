package nest.repository.user;

import java.util.Optional;
import nest.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.favoriteBooks WHERE u.id = :userId")
    Optional<User> findByIdWithFavoriteBooks(@Param("userId") Long userId);
}
