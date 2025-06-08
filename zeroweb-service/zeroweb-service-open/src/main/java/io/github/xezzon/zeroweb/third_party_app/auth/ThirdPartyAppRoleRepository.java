package io.github.xezzon.zeroweb.third_party_app.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author xezzon
 */
@Repository
public interface ThirdPartyAppRoleRepository extends JpaRepository<ThirdPartyAppRole, String> {

}
