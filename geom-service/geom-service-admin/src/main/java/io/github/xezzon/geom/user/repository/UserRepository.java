package io.github.xezzon.geom.user.repository;

import io.github.xezzon.geom.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author xezzon
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {

  Optional<User> findByUsername(String username);
}
