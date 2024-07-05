package io.github.xezzon.geom.common.constant;

/**
 * @author xezzon
 */
public class CharacterConstant {

  /**
   * 小写字符集
   */
  public static final char[] LOWERCASE = new char['z' - 'a' + 1];
  static {
    for (int i = 0, cnt = LOWERCASE.length; i < cnt; i++) {
      LOWERCASE[i] = (char) ('a' + i);
    }
  }
  /**
   * 大写字符集
   */
  public static final char[] UPPERCASE = new char['Z' - 'A' + 1];
  static {
    for (int i = 0, cnt = UPPERCASE.length; i < cnt; i++) {
      UPPERCASE[i] = (char) ('A' + i);
    }
  }

  /**
   * 数字
   */
  public static final char[] DIGIT = new char[10];
  static {
    for (int i = 0, cnt = DIGIT.length; i < cnt; i++) {
      DIGIT[i] = (char) ('0' + i);
    }
  }

  private CharacterConstant() {
  }
}
