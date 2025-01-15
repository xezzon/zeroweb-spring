package io.github.xezzon.zeroweb.locale.repository;

import io.github.xezzon.zeroweb.locale.domain.I18nText;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface I18nTextRepository extends
    JpaRepository<I18nText, String>,
    JpaSpecificationExecutor<I18nText> {

  List<I18nText> findByNamespaceAndMessageKey(String namespace, String messageKey);

  Optional<I18nText> findByNamespaceAndMessageKeyAndLanguage(
      String namespace, String messageKey, String language
  );

  List<I18nText> findByNamespaceAndLanguage(String namespace, String language);

  @Transactional
  @Modifying
  @Query("""
      update I18nText i
      set i.namespace = ?1,
      i.messageKey = ?2
      where i.namespace = ?3
      and i.messageKey = ?4"""
  )
  int updateByNamespaceAndMessageKey(
      String newNamespace, String newMessageKey,
      String oldNamespace, String oldMessageKey
  );

  @Transactional
  long deleteByNamespaceAndMessageKey(String namespace, String messageKey);

  @Transactional
  long deleteByLanguage(String language);

  @Transactional
  @Modifying
  @Query("update I18nText i set i.language = ?1 where i.language = ?2")
  int updateByLanguage(String newLanguageTag, String oldLanguageTag);
}
