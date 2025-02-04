package io.github.xezzon.zeroweb.common.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

/**
 * 国际化相关方法
 * @author xezzon
 * @see org.springframework.context.i18n.LocaleContextHolder#getLocale()
 * @see org.springframework.context.MessageSource#getMessage(String, Object[], Locale)
 */
@Slf4j
public class I18nUtil {

  /**
   * 国际化资源前缀
   */
  private static final String BASE_DIR = "i18n/";

  private I18nUtil() {
    super();
  }

  /**
   * 读取指定语言包
   * 注意：指定语言包不存在时，会加载默认语言包
   * @param basename 命名空间
   * @param locale 语言
   * @return 语言包
   */
  public static ResourceBundle readResourceBundle(@NotNull String basename, @NotNull Locale locale)
      throws MissingResourceException {
    return ResourceBundle.getBundle(BASE_DIR + basename, locale, new MergedResourceBundleControl());
  }

  /**
   * 消息格式化的构造器
   * @param basename 命名空间
   * @return 消息格式化器
   */
  public static I18nMessageFormatter formatter(@NotNull String basename) {
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
      ResourceBundle resourceBundle;
      try {
        resourceBundle = readResourceBundle(basename, locale);
      } catch (MissingResourceException e) {
        log.warn("The resource bundle {} does not exist", resourceBundleName());
        return null;
      }
      String notNullKey = Optional.ofNullable(key).orElse("");
      if (!resourceBundle.containsKey(notNullKey)) {
        log.warn("The key {} of resource bundle {} does not exist", key, resourceBundleName());
        return null;
      }
      return resourceBundle.getString(notNullKey);
    }

    /**
     * `i18n/${命名空间}_${语言标签}.properties`
     * @return 语言包名称
     */
    private String resourceBundleName() {
      return BASE_DIR + basename + "_" + locale + ".properties";
    }
  }
}

/**
 * 从多个classpath读取同名的资源文件，对相同的key进行合并。 classpath顺序越靠前，优先级越高。
 */
class MergedResourceBundleControl extends ResourceBundle.Control {

  @Override
  public ResourceBundle newBundle(final String baseName, final Locale locale, final String format,
      final ClassLoader loader, final boolean reload) throws IOException {
    final String bundleName = toBundleName(baseName, locale);
    final String resourceName = toResourceName(bundleName, "properties");
    final List<URL> resources = Collections.list(loader.getResources(resourceName));
    if (resources.isEmpty()) {
      throw new MissingResourceException(
          "Can't find bundle for base name " + baseName + ", locale " + locale, // message
          baseName + "_" + locale, // className
          "" // key
      );
    }
    final int initialMapCapacity = Math.max(16, (int) (resources.size() * 20 / 0.75));
    final Map<String, String> lookup = new HashMap<>(initialMapCapacity);
    final Properties properties = new Properties();
    for (final URL url : resources) {
      final URLConnection urlConnection = url.openConnection();
      urlConnection.setUseCaches(!reload);
      try (InputStream inputStream = urlConnection.getInputStream();
          InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)
      ) {
        properties.load(reader);
        properties.forEach((key, value) -> lookup.putIfAbsent(key.toString(), value.toString()));
      }
    }
    return new ResourceBundle() {

      @Override
      protected Object handleGetObject(@NotNull final String key) {
        return lookup.get(key);
      }

      @Override
      public @NotNull Enumeration<String> getKeys() {
        return Collections.enumeration(lookup.keySet());
      }
    };
  }
}
