package io.github.xezzon.zeroweb.third_party_app.repository;

import io.github.xezzon.zeroweb.third_party_app.domain.AccessSecret;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author xezzon
 */
@Repository
public interface AccessSecretRepository extends JpaRepository<AccessSecret, String> {

  @Transactional
  @Modifying
  @Query("update AccessSecret a set a.secretKey = :secretKey where a.id = :id")
  int updateSecretKeyById(@Param("id") String id, @Param("secretKey") String secretKey);
}
