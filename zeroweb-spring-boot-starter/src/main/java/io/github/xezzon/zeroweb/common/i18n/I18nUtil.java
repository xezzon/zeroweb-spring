package io.github.xezzon.zeroweb.common.i18n;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import lombok.extern.slf4j.Slf4j;

/**
 * 国际化相关方法
 * @author xezzon
 */
@Slf4j
public class I18nUtil {

  /**
   * 国际化资源前缀
   */
  private static final String BASE_DIR = "i18n/";
  /**
   * 当前语言
   */
  private static final ThreadLocal<Locale> CURRENT_LOCALE = new InheritableThreadLocal<>();

  private I18nUtil() {
    super();
  }

  /**
   * 设置当前线程的语言
   * @param locale 语言
   */
  public static void setCurrentLocale(Locale locale) {
    if (locale != null) {
      CURRENT_LOCALE.set(locale);
    } else {
      CURRENT_LOCALE.remove();
    }
  }

  /**
   * @return 当前线程的语言配置
   */
  public static Locale getCurrentLocale() {
    return Optional.ofNullable(CURRENT_LOCALE.get())
        .orElseGet(Locale::getDefault);
  }

  /**
   * 读取指定语言包
   * 注意：指定语言包不存在时，会加载默认语言包
   * @param basename 命名空间
   * @param locale 语言
   * @return 语言包
   */
  public static ResourceBundle readResourceBundle(String basename, Locale locale) {
    return ResourceBundle.getBundle(BASE_DIR + basename, locale);
  }

  /**
   * 消息格式化的构造器
   * @param basename 命名空间
   * @return 消息格式化器
   */
  public static I18nMessageFormatter formatter(String basename) {
    return new I18nMessageFormatter(basename);
  }

  /**
   * 消息格式化器
   */
  public static class I18nMessageFormatter {

    private final String basename;
    private Locale locale = Locale.getDefault();

    /**
     * @param basename 命名空间
     */
    public I18nMessageFormatter(String basename) {
      this.basename = basename;
    }

    /**
     * @param locale 目标语言
     */
    public I18nMessageFormatter locale(Locale locale) {
      this.locale = locale;
      return this;
    }

    /**
     * @param key 国际化文本在 resource bundle 中的键值
     * @param fallback 兜底内容。注意，兜底内容不能包含参数。
     * @param args 消息格式化参数
     * @return 国际化消息
     */
    public String format(String key, String fallback, Object... args) {
      String messageTemplate = this.getMessageTemplate(key);
      if (messageTemplate != null) {
        // 有对应的国际化文本
        return MessageFormat.format(messageTemplate, args);
      }
      messageTemplate = this.getMessageTemplate(fallback);
      if (messageTemplate != null) {
        // 如果没找到，则找兜底的国际化文本
        return messageTemplate;
      }
      // 如果兜底都没找到，直接返回兜底内容
      return fallback;
    }

    /**
     * @param key 国际化文本在 resource bundle 中的键值
     * @return 对应的国际化文本。如果没有找到语言包或对应的键值则返回 null
     */
    private String getMessageTemplate(String key) {
      ResourceBundle resourceBundle = readResourceBundle(basename, locale);
      if (!resourceBundle.containsKey(key)) {
        log.warn("The key {} of resource bundle {} does not exist", key, getResourceBundleName());
        return null;
      }
      return resourceBundle.getString(key);
    }

    /**
     * `i18n/${命名空间}_${语言标签}.properties`
     * @return 语言包名称
     */
    private String getResourceBundleName() {
      return BASE_DIR + basename + "_" + locale + ".properties";
    }
  }
}
