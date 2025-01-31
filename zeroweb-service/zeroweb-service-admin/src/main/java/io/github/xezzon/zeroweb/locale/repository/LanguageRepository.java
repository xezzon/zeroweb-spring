package io.github.xezzon.zeroweb.locale.repository;

import io.github.xezzon.zeroweb.locale.domain.Language;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author xezzon
 */
@Repository
public interface LanguageRepository extends
    JpaRepository<Language, String>,
    JpaSpecificationExecutor<Language> {

  Optional<Language> findByDictTagAndLanguageTag(String dictTag, String languageTag);

  List<Language> findByDictTagOrderByOrdinalAsc(String dictTag);
}
