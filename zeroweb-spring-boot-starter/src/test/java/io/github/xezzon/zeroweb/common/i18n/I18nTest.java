package io.github.xezzon.zeroweb.common.i18n;

import cn.hutool.core.util.RandomUtil;
import java.util.Locale;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author xezzon
 */
class I18nTest {

  private static final String BASENAME = "test";
  private static final Locale TARGET_LOCALE = Locale.CHINA;
  private static final String KEY = "hello";
  private static final String NAME = RandomUtil.randomString(8);
  private static final String EXCEPT_ZH_CN = "你好，" + NAME;
  private static final String EXCEPT_EN = "Hello " + NAME;
  private static final String ZH_CN_FALLBACK = "兜底";

  static {
    Locale.setDefault(TARGET_LOCALE);
  }

  @Test
  void format() {
    final String fallback = "fallback";

    String zhCnActual = I18nUtil.formatter(BASENAME)
        .format(KEY, fallback, NAME);
    Assertions.assertEquals(EXCEPT_ZH_CN, zhCnActual);

    String enActual = I18nUtil.formatter(BASENAME)
        .locale(Locale.ENGLISH)
        .format(KEY, fallback, NAME);
    Assertions.assertEquals(EXCEPT_EN, enActual);
  }

  @Test
  void format_fallback() {
    final String fallback = "fallback";

    String zhCnActual = I18nUtil.formatter(BASENAME)
        .format(RandomUtil.randomString(8), fallback, NAME);
    Assertions.assertEquals(ZH_CN_FALLBACK, zhCnActual);

    String enActual = I18nUtil.formatter(BASENAME)
        .locale(Locale.ENGLISH)
        .format(RandomUtil.randomString(8), fallback, NAME);
    Assertions.assertEquals(fallback, enActual);

    // 没有加载到指定语言包时，会加载默认语言包
    String unknownExcept = I18nUtil.formatter(BASENAME)
        .format(KEY, fallback, NAME);
    String unknownActual = I18nUtil.formatter(BASENAME)
        .locale(Locale.JAPAN)
        .format(KEY, fallback, NAME);
    Assertions.assertEquals(unknownExcept, unknownActual);
  }
}
