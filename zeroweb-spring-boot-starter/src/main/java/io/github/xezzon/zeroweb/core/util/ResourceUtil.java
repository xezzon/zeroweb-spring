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
    return paths.getFirst();
  }
}
