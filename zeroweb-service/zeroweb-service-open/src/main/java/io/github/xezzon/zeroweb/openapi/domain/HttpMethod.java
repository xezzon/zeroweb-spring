package io.github.xezzon.zeroweb.openapi.domain;

import io.github.xezzon.tao.dict.IDict;

/**
 * 开放平台允许使用的HTTP方法
 */
public enum HttpMethod implements IDict {

  GET,
  POST,
  PUT,
  DELETE,
  PATCH,
  ;

  @Override
  public String getTag() {
    return this.getClass().getSimpleName();
  }

  @Override
  public String getCode() {
    return this.name();
  }

  @Override
  public String getLabel() {
    return this.name();
  }

  @Override
  public int getOrdinal() {
    return this.ordinal();
  }
}
