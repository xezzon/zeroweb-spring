package io.github.xezzon.zeroweb.locale.repository;

import io.github.xezzon.zeroweb.locale.domain.I18nMessage;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author xezzon
 */
@Repository
public interface I18nMessageRepository extends
    JpaRepository<I18nMessage, String>,
    JpaSpecificationExecutor<I18nMessage> {

  Optional<I18nMessage> findByNamespaceAndMessageKey(String namespace, String messageKey);
}
