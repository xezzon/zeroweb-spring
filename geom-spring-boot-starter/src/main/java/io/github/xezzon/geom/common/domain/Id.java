package io.github.xezzon.geom.common.domain;

/**
 * 将ID封装成对象
 * @author xezzon
 */
public record Id(String id) {

  /**
   * 静态化构造ID对象
   * @param id ID
   * @return ID对象
   */
  public static Id of(String id) {
    return new Id(id);
  }
}
