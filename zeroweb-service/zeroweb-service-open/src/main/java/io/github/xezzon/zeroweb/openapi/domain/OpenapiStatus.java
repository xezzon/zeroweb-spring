package io.github.xezzon.zeroweb.openapi.domain;

import io.github.xezzon.tao.dict.IDict;

/**
 * 接口状态
 * @author xezzon
 */
public enum OpenapiStatus implements IDict {

  DRAFT("草稿"),
  PUBLISHED("已发布"),
  ;

  private final String label;

  OpenapiStatus(String label) {
    this.label = label;
  }

  @Override
  public String getTag() {
    return OpenapiStatus.class.getSimpleName();
  }

  @Override
  public String getCode() {
    return this.name();
  }

  @Override
  public String getLabel() {
    return this.label;
  }

  @Override
  public int getOrdinal() {
    return this.ordinal();
  }
}
