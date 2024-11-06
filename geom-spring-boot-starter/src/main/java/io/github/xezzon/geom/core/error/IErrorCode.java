package io.github.xezzon.geom.core.error;

/**
 * 错误码抽象
 * @author xezzon
 */
public interface IErrorCode {

  /**
   * 错误码编码 格式为 `${错误来源类型}{模块编码}{异常码序列号}`
   * @return 错误码编码
   */
  default String code() {
    return String.format(
        "%s%02X%02X",
        sourceType().getCode(),
        Short.toUnsignedInt(moduleCode()),
        Short.toUnsignedInt(serialNumber())
    );
  }

  /**
   * @return 错误来源类型
   */
  ErrorSourceType sourceType();

  /**
   * 模块编码 数据范围 1~127、-128~-1 0保留为公共异常 可转为2位16进制数的表示形式
   * @return 模块编码
   */
  byte moduleCode();

  /**
   * 异常码序列号 同一模块下的异常按从1~127、-128~-1编号
   * @return 异常码序列号
   */
  default byte serialNumber() {
    if (this instanceof Enum<?> e) {
      int ordinal = e.ordinal() + 1;
      return (byte) ordinal;
    }
    throw new UnsupportedOperationException("serialNumber() should be implemented by non-enum");
  }

  /**
   * 错误消息模板
   * @return 错误消息模板
   */
  String message();

  /**
   * 错误名称
   * 与错误码编码一一对应
   * @return 错误名称
   */
  String name();
}
