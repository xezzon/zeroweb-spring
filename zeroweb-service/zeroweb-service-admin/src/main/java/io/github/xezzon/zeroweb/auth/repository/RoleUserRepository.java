package io.github.xezzon.zeroweb.auth.repository;

import io.github.xezzon.zeroweb.auth.domain.RoleUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleUserRepository extends
    JpaRepository<RoleUser, String>, JpaSpecificationExecutor<RoleUser> {

}
