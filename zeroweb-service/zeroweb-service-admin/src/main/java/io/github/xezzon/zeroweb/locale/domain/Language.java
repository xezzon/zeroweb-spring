package io.github.xezzon.zeroweb.locale.domain;

import io.github.xezzon.zeroweb.common.constant.DatabaseConstant;
import io.github.xezzon.zeroweb.common.jpa.IEntity;
import io.github.xezzon.zeroweb.common.jpa.IdGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 语言 底层基于字典实现
 *
 * @author xezzon
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "zeroweb_dict")
public class Language implements IEntity<String> {

  /**
   * 语言的字典目
   */
  public static final String LANGUAGE_DICT_TAG = "Language";
  /**
   * 语言的字典目的ID
   */
  public static final String LANGUAGE_DICT_PARENT_ID = "1";

  @Id
  @Column(name = "id", nullable = false, updatable = false, length = DatabaseConstant.ID_LENGTH)
  @IdGenerator
  String id;
  /**
   * 字典目
   */
  @Column(name = "tag", nullable = false, updatable = false)
  @Setter(AccessLevel.NONE)
  private String dictTag = LANGUAGE_DICT_TAG;
  /**
   * 语言标签
   */
  @Column(name = "code", nullable = false)
  String languageTag;
  /**
   * 语言描述
   */
  @Column(name = "label")
  String description;
  /**
   * 排序号 数值越小，顺序越靠前。
   */
  @Column(name = "ordinal", nullable = false)
  Integer ordinal;
  /**
   * 字典上级ID
   */
  @Column(
      name = "parent_id", nullable = false, updatable = false,
      length = DatabaseConstant.ID_LENGTH
  )
  @Setter(AccessLevel.NONE)
  private String parentId = LANGUAGE_DICT_PARENT_ID;
  /**
   * 启用状态
   */
  @Column(name = "enabled", nullable = false)
  Boolean enabled;
  /**
   * 是否可编辑，固定为true
   */
  @Column(name = "editable", nullable = false, updatable = false)
  @Setter(AccessLevel.NONE)
  private Boolean editable = true;
}
