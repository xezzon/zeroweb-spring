package io.github.xezzon.zeroweb.dict;

import io.github.xezzon.zeroweb.core.trait.IDict;

/// @author xezzon
public enum TestEnum implements IDict {

  NORMAL("正常"),
  DISABLE("禁用"),
  ;

  private final String label;

  TestEnum(String label) {
    this.label = label;
  }

  @Override
  public String getTag() {
    return TestEnum.class.getSimpleName();
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
