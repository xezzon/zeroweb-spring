package io.github.xezzon.zeroweb.third_party_app.repository;

import io.github.xezzon.zeroweb.third_party_app.domain.ThirdPartyApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ThirdPartyAppRepository extends
    JpaRepository<ThirdPartyApp, String>,
    JpaSpecificationExecutor<ThirdPartyApp> {

}
