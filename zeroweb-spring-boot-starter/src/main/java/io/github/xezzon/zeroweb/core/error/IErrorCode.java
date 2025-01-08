package io.github.xezzon.zeroweb.core.error;

/**
 * 错误码抽象
 * @author xezzon
 */
public interface IErrorCode {

  /**
   * 错误码国际化的命名空间
   */
  String I18N_BASENAME = "ErrorCode";

  /**
   * 错误码编码 格式为 `${错误来源类型}{模块编码}{异常码序列号}`
   * @return 错误码编码
   */
  default String code() {
    return String.format(
        "%s%02X%02X",
        sourceType().getCode(),
        Byte.toUnsignedInt(moduleCode()),
        Byte.toUnsignedInt(serialNumber())
    );
  }

  /**
   * @return 错误来源类型
   */
  ErrorSourceType sourceType();

  /**
   * 模块编码
   * 0 为公共模块
   * ZeroWeb 的模块编码从 -1~-128 分配
   * 1~127 由使用者分配
   * @return 模块编码
   */
  byte moduleCode();

  /**
   * 异常码序列号
   * 同一模块下的异常按从 1~255 分配
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
   * 错误名称
   * 与错误码编码一一对应
   * @return 错误名称
   */
  String name();
}
