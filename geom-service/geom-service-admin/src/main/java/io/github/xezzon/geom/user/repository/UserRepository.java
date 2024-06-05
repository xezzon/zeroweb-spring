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

  /**
   * 根据用户名获取记录
   * @param username 用户名
   * @return 用户记录
   */
  Optional<User> findByUsername(String username);
}
