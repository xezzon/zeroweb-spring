/*
 * SPDX-FileCopyrightText: Copyright (C) 2025 xezzon
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * This file is part of ZeroWeb.
 *
 * ZeroWeb is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * ZeroWeb is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with ZeroWeb. If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.xezzon.zeroweb.core.util;

import io.github.xezzon.zeroweb.common.exception.NoValidClasspathException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 资源工具类，用于从classpath中获取资源。
 *
 * @author xezzon
 */
public class ResourceUtil {

  /// 文件URL协议名称
  public static final String URL_PROTOCOL_FILE = "file";

  /**
   * 私有构造函数，防止实例化。
   */
  private ResourceUtil() {
  }

  /**
   * 从所有文件系统的classpath中获取指定资源的所有路径。
   *
   * @param resourceName 资源名称，例如 "application.yml"
   * @return 资源路径列表，如果找不到则返回空列表
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
    return paths.getFirst();
  }
}
