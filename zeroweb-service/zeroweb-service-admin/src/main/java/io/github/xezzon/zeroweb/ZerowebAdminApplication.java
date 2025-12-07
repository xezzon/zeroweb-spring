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

package io.github.xezzon.zeroweb;

import io.github.xezzon.zeroweb.dict.EnableDictScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode;

/// `ZerowebAdminApplication` 是 ZeroWeb 系统管理服务的入口点。
///
/// 这个应用程序提供了系统管理相关的功能，例如用户管理、角色管理、字典管理和系统设置等。
///
/// @author xezzon
@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = PageSerializationMode.VIA_DTO)
@EnableDictScan
@EnableJpaAuditing
public class ZerowebAdminApplication {

  ZerowebAdminApplication() {
  }

  /// 系统管理服务的主方法。
  ///
  /// 这是 Spring Boot 应用程序的入口，负责启动整个服务。
  ///
  /// @param args 命令行参数，用于配置应用程序。
  static void main(String[] args) {
    SpringApplication.run(ZerowebAdminApplication.class, args);
  }
}
