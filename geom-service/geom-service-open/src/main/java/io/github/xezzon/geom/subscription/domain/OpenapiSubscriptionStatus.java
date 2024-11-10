package io.github.xezzon.geom.subscription.domain;

import io.github.xezzon.tao.dict.IDict;

/**
 * @author xezzon
 */
public enum OpenapiSubscriptionStatus implements IDict {

  NONE("未订阅"),
  AUDITING("审核中"),
  SUBSCRIBED("已订阅"),
  ;

  private final String label;

  OpenapiSubscriptionStatus(String label) {
    this.label = label;
  }

  @Override
  public String getTag() {
    return OpenapiSubscriptionStatus.class.getSimpleName();
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
