package io.github.xezzon.zeroweb.third_party_app.repository;

import io.github.xezzon.zeroweb.third_party_app.ThirdPartyApp;
import java.util.Collection;
import java.util.List;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
@NullMarked
public interface ThirdPartyAppRepository extends
    JpaRepository<ThirdPartyApp, String>,
    JpaSpecificationExecutor<ThirdPartyApp> {

  List<ThirdPartyApp> findByIdInOrderByCreateTimeDesc(Collection<String> ids);
}
