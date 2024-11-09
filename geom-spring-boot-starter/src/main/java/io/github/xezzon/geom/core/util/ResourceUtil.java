package io.github.xezzon.geom.core.util;

import io.github.xezzon.geom.common.exception.NoValidClasspathException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author xezzon
 */
public class ResourceUtil {

  public static final String URL_PROTOCOL_FILE = "file";

  private ResourceUtil() {
  }

  /**
   * 从所有的文件系统的classpath中获取资源
   * @param resourceName 资源名称
   * @return 资源路径列表
   */
  public static List<Path> getResourcesFromClasspath(String resourceName) {
    try {
      List<Path> paths = new ArrayList<>();
      List<URL> urls = ResourceUtil.class.getClassLoader().resources("")
          .filter(o -> Objects.equals(o.getProtocol(), URL_PROTOCOL_FILE))
          .toList();
      if (urls.isEmpty()) {
        throw new IllegalArgumentException();
      }
      for (URL url : urls) {
        paths.add(Path.of(url.toURI()).resolve(resourceName));
      }
      return paths;
    } catch (URISyntaxException | IllegalArgumentException e) {
      throw new NoValidClasspathException(e);
    }
  }

  /**
   * 从首个文件系统的classpath中获取资源
   * @param resourceName 资源名称
   * @return 资源路径
   */
  public static Path getResourceFromClasspath(String resourceName) {
    List<Path> paths = getResourcesFromClasspath(resourceName);
    return paths.get(0);
  }
}
