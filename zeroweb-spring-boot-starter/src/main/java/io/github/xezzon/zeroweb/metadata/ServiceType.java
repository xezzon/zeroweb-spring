package io.github.xezzon.zeroweb.metadata;

import io.github.xezzon.zeroweb.core.trait.IDict;

/**
 * 服务类型
 * @author xezzon
 */
public enum ServiceType implements IDict {
  CLIENT("前端"),
  SERVER("后端"),
  ;

  private final String label;

  ServiceType(final String label) {
    this.label = label;
  }

  @Override
  public String getTag() {
    return ServiceType.class.getSimpleName();
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
