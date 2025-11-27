/*
 * SPDX-FileCopyrightText: Copyright (C) 2025 xezzon
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * This file is part of ZeroWeb.
 *
 * ZeroWeb is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * ZeroWeb is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with ZeroWeb. If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.xezzon.zeroweb.locale.internal;

import io.github.xezzon.zeroweb.common.exception.RepeatDataException;
import io.github.xezzon.zeroweb.core.odata.ODataQueryOption;
import io.github.xezzon.zeroweb.locale.I18nMessage;
import io.github.xezzon.zeroweb.locale.II18nMessage;
import io.github.xezzon.zeroweb.locale.Language;
import io.github.xezzon.zeroweb.locale.Translation;
import io.github.xezzon.zeroweb.locale.event.I18nMessageChangedEvent;
import io.github.xezzon.zeroweb.locale.event.I18nMessageDeletedEvent;
import io.github.xezzon.zeroweb.locale.repository.TranslationRepository;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.jspecify.annotations.NonNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/// @author xezzon
@Service
public class LocalizedService {

  private final LanguageDAO languageDAO;
  private final I18nMessageDAO i18nMessageDAO;
  private final TranslationRepository translationRepository;
  @Resource
  private ApplicationEventPublisher eventPublisher;

  LocalizedService(
      final LanguageDAO languageDAO,
      final I18nMessageDAO i18nMessageDAO,
      final TranslationRepository translationRepository
  ) {
    this.languageDAO = languageDAO;
    this.i18nMessageDAO = i18nMessageDAO;
    this.translationRepository = translationRepository;
  }

  /// 新增语言
  ///
  /// @param language 语言
  /// @throws RepeatDataException 重复数据异常
  void addLanguage(final Language language) {
    /* 前置校验 */
    checkRepeat(language);
    /* 持久化 */
    languageDAO.get().save(language);
  }

  /// 查询语言列表
  ///
  /// @return 语言列表
  List<Language> queryLanguageList() {
    return languageDAO.findAllOrderByOrdinalAsc();
  }

  /// 更新语言
  ///
  /// @param language 语言
  void updateLanguage(final Language language) {
    final Language entity = languageDAO.get().findById(language.getId()).orElseThrow();
    /* 前置校验 */
    this.checkRepeat(language);
    /* 属性赋值 */
    final String oldTag = entity.getLanguageTag();
    languageDAO.getCopier().copy(language, entity);
    /* 持久化 */
    languageDAO.get().save(entity);
    /* 后置处理 */
    if (!Objects.equals(oldTag, language.getLanguageTag())) {
      translationRepository.updateByLanguage(oldTag, language.getLanguageTag());
    }
  }

  /// 删除语言
  ///
  /// @param id 语言ID
  void deleteLanguage(final String id) {
    final Optional<Language> entity = languageDAO.get().findById(id);
    if (entity.isEmpty()) {
      return;
    }
    languageDAO.get().deleteById(id);
    /* 后置处理 */
    translationRepository.deleteByLanguage(entity.get().getLanguageTag());
  }

  /// 新增国际化内容
  ///
  /// @param i18nMessage 国际化内容
  /// @throws RepeatDataException 重复数据异常
  void addI18nMessage(final I18nMessage i18nMessage) {
    /* 前置校验 */
    this.checkRepeat(i18nMessage);
    /* 持久化 */
    i18nMessageDAO.get().save(i18nMessage);
  }

  /// 列举国际化内容命名空间
  ///
  /// @return 国际化内容命名空间列表
  List<String> listI18nNamespace() {
    return i18nMessageDAO.get().findDistinctNamespace()
        .stream()
        .sorted()
        .toList();
  }

  /// 分页查询国际化内容
  ///
  /// @param namespace 命名空间
  /// @param odata 分页查询参数
  /// @return 国际化内容列表
  Page<@NonNull I18nMessage> queryI18nMessageList(final String namespace, final ODataQueryOption odata) {
    return i18nMessageDAO.findAllWithNamespace(namespace, odata);
  }

  /// 更新国际化内容
  ///
  /// @param i18nMessage 国际化内容
  /// @throws RepeatDataException 重复数据异常
  /// @throws jakarta.persistence.EntityNotFoundException 数据不存在或已删除
  void updateI18nMessage(final I18nMessage i18nMessage) {
    final I18nMessage entity = i18nMessageDAO.get().findById(i18nMessage.getId()).orElseThrow();
    final I18nMessage oldValue = new I18nMessage();
    i18nMessageDAO.getCopier().copy(entity, oldValue);
    /* 前置校验 */
    this.checkRepeat(i18nMessage);
    /* 持久化 */
    i18nMessageDAO.getCopier().copy(i18nMessage, entity);
    i18nMessageDAO.get().save(entity);
    /* 后置处理 */
    eventPublisher.publishEvent(new I18nMessageChangedEvent(oldValue, i18nMessage));
  }

  /// 删除国际化内容
  ///
  /// @param id 国际化内容ID
  void deleteI18nMessage(final String id) {
    final Optional<I18nMessage> entity = i18nMessageDAO.get().findById(id);
    if (entity.isEmpty()) {
      return;
    }
    i18nMessageDAO.get().deleteById(id);
    /* 后置处理 */
    eventPublisher.publishEvent(new I18nMessageDeletedEvent(entity.get()));
  }

  /// 查询国际化文本
  ///
  /// @param namespace 命名空间
  /// @param messageKey 国际化内容
  /// @return 语言-国际化文本
  Map<String, String> queryTranslation(final String namespace, final String messageKey) {
    return translationRepository.findByNamespaceAndMessageKey(namespace, messageKey)
        .stream()
        .collect(Collectors.toMap(Translation::getLanguage, Translation::getContent, (a, _) -> a));
  }

  /// 新增国际化文本，如果已存在则更新
  ///
  /// @param translation 国际化文本
  /// @throws EntityNotFoundException 数据不存在或已删除
  void upsertTranslation(final Translation translation) {
    /* 前置校验 */
    final String languageTag = translation.getLanguage();
    final Language language = languageDAO.findByLanguageTag(languageTag)
        .orElseThrow(() -> new EntityNotFoundException(
            String.format("Language %s not found", languageTag)
        ));
    final String namespace = translation.getNamespace();
    final String messageKey = translation.getMessageKey();
    final I18nMessage i18nMessage = i18nMessageDAO.get()
        .findByNamespaceAndMessageKey(namespace, messageKey)
        .orElseThrow(() -> new EntityNotFoundException(
            String.format("I18nMessage `%s`.`%s` not found", namespace, messageKey)
        ));
    /* 持久化 */
    final Optional<Translation> entity = translationRepository
        .findByNamespaceAndMessageKeyAndLanguage(
            i18nMessage.getNamespace(), i18nMessage.getMessageKey(), language.getLanguageTag()
        );
    entity.ifPresent(o -> translation.setId(o.getId()));
    translationRepository.save(translation);
  }

  /// 加载国际化资源
  ///
  /// @param language 语言标签
  /// @param namespace 命名空间
  /// @return 国际化内容-国际化文本
  Map<String, String> loadTranslation(final String language, final String namespace) {
    return translationRepository.findByNamespaceAndLanguage(namespace, language)
        .stream()
        .collect(Collectors.toMap(
            Translation::getMessageKey,
            Translation::getContent,
            (a, _) -> a
        ));
  }

  /// 语言之间不能有相同的 Language Tag
  private void checkRepeat(final Language language) {
    final Optional<Language> exist = languageDAO.findByLanguageTag(language.getLanguageTag());
    if (exist.isPresent() && !exist.get().getId().equals(language.getId())) {
      throw new RepeatDataException("`" + language.getLanguageTag() + "`");
    }
  }

  /// 同一命名空间下，国际化内容不能重复
  ///
  /// @param i18nMessage 国际化内容
  private void checkRepeat(final I18nMessage i18nMessage) {
    final String namespace = i18nMessage.getNamespace();
    final String messageKey = i18nMessage.getMessageKey();
    final Optional<I18nMessage> exist = i18nMessageDAO.get()
        .findByNamespaceAndMessageKey(namespace, messageKey);
    if (exist.isPresent() && !exist.get().getId().equals(i18nMessage.getId())) {
      throw new RepeatDataException(String.format("`%s`.`%s`", namespace, messageKey));
    }
  }

  @EventListener
  @Async
  public void listen(final I18nMessageChangedEvent event) {
    final II18nMessage oldValue = event.oldValue();
    final II18nMessage newValue = event.newValue();
    if (newValue.eq(oldValue)) {
      return;
    }
    translationRepository.updateByNamespaceAndMessageKey(oldValue, newValue);
  }

  @EventListener
  @Async
  public void listen(final I18nMessageDeletedEvent event) {
    final II18nMessage i18nMessage = event.i18nMessage();
    translationRepository.deleteByNamespaceAndMessageKey(
        i18nMessage.getNamespace(), i18nMessage.getMessageKey()
    );
  }
}
