package io.github.xezzon.zeroweb.locale.repository;

import io.github.xezzon.zeroweb.locale.domain.I18nText;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface I18nTextRepository extends
    JpaRepository<I18nText, String>,
    JpaSpecificationExecutor<I18nText> {

  List<I18nText> findByNamespaceAndMessageKey(String namespace, String messageKey);
}
