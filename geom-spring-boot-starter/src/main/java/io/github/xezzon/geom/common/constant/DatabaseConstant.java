package io.github.xezzon.geom.common.constant;

/**
 * 数据库相关常量
 * @author xezzon
 */
public class DatabaseConstant {

  /**
   * 主键字段长度
   */
  public static final int ID_LENGTH = 64;
  /**
   * 默认ID
   * 用于表示不存在的根节点
   */
  public static final String ROOT_ID = "0";

  private DatabaseConstant() {
  }
}
