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

package io.github.xezzon.zeroweb.role;

import io.github.xezzon.zeroweb.common.constant.DatabaseConstant;
import io.github.xezzon.zeroweb.common.jpa.IEntity;
import io.github.xezzon.zeroweb.common.jpa.IdGenerator;
import io.github.xezzon.zeroweb.core.tree.ITreeNode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/// 角色实体类
///
/// 角色是权限管理系统的核心概念，用于定义用户的权限范围。
/// 角色采用树形结构管理，支持角色继承和权限聚合。
/// 每个角色都有唯一标识、简码、完整编码、名称等信息。
///
/// @author xezzon
@Getter
@Setter
@ToString
@Entity
@Table(name = "zeroweb_role")
public class Role implements IEntity<String>, ITreeNode<Role, String> {

  /// 角色标识
  ///
  /// 角色的唯一标识符，使用分布式ID生成器生成。
  /// 在整个系统中唯一，不可重复。
  @Id
  @IdGenerator
  @Column(name = "id", nullable = false, updatable = false, length = DatabaseConstant.ID_LENGTH)
  private String id;
  /// 角色简码
  ///
  /// 角色的简短标识符，用于区分同级别角色。
  /// 在同一父角色下唯一，通常为英文标识。
  @Column(name = "code", nullable = false)
  private String code;
  /// 角色编码
  ///
  /// 角色的完整编码，采用路径形式表示（如：ADMIN/SYSTEM/USER）。
  /// 用于唯一标识角色在整个角色树中的位置。
  @Column(name = "value", nullable = false)
  private String value;
  /// 角色名称
  ///
  /// 角色的显示名称，用于界面展示。
  /// 支持中文，可以包含特殊字符。
  @Column(name = "name", nullable = false)
  private String name;
  /// 是否允许该角色创建其下级角色
  ///
  /// 标识该角色是否允许创建子角色。
  /// false 表示该角色为叶子节点，不能再创建下级角色。
  @Column(name = "inheritable", nullable = false)
  private Boolean inheritable;
  /// 上级角色标识
  ///
  /// 该角色的父角色ID，标识角色在树形结构中的位置。
  /// 根节点的父角色ID为系统根节点ID。
  @Column(name = "parent_id", nullable = false)
  private String parentId;
  /// 子角色列表
  ///
  /// 临时字段，用于存储该角色的直接子角色。
  /// 不持久化到数据库，通过查询动态构建。
  @Transient
  private List<Role> children;
}
