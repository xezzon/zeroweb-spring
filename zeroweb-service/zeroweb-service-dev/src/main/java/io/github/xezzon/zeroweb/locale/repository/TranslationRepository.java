package io.github.xezzon.zeroweb.locale.repository;

import io.github.xezzon.zeroweb.locale.II18nMessage;
import io.github.xezzon.zeroweb.locale.Translation;
import java.util.List;
import java.util.Optional;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@NullMarked
public interface TranslationRepository extends
    JpaRepository<Translation, String>,
    JpaSpecificationExecutor<Translation> {

  List<Translation> findByNamespaceAndMessageKey(String namespace, String messageKey);

  Optional<Translation> findByNamespaceAndMessageKeyAndLanguage(
      String namespace, String messageKey, String language
  );

  List<Translation> findByNamespaceAndLanguage(String namespace, String language);

  @Transactional
  @Modifying
  @Query("""
      update Translation i
      set i.namespace = :#{#n.namespace},
      i.messageKey = :#{#n.messageKey}
      where i.namespace = :#{#o.namespace}
      and i.messageKey = :#{#o.messageKey}"""
  )
  int updateByNamespaceAndMessageKey(
      @Param("o") II18nMessage oldI18nMessage,
      @Param("n") II18nMessage newI18nMessage
  );


  @Transactional
  void deleteByNamespaceAndMessageKey(String namespace, String messageKey);

  @Transactional
  void deleteByLanguage(String language);

  @Transactional
  @Modifying
  @Query("update Translation i set i.language = ?2 where i.language = ?1")
  int updateByLanguage(String oldLanguageTag, String newLanguageTag);
}
