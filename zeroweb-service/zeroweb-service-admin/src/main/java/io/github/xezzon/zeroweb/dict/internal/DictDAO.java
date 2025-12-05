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

package io.github.xezzon.zeroweb.dict.internal;

import io.github.xezzon.zeroweb.common.jpa.BaseDAO;
import io.github.xezzon.zeroweb.core.odata.ODataQueryOption;
import io.github.xezzon.zeroweb.dict.Dict;
import io.github.xezzon.zeroweb.dict.Dict_;
import io.github.xezzon.zeroweb.dict.repository.DictRepository;
import io.github.xezzon.zeroweb.dict.repository.DictSpecs;
import jakarta.transaction.Transactional;
import java.util.Collection;
import java.util.Optional;
import org.jspecify.annotations.NullMarked;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

/// 字典数据访问对象
///
/// 继承自 [BaseDAO]，提供字典数据的增删改查功能。
/// 实现了分页查询、状态更新等业务方法。
///
/// @author xezzon
@Repository
@NullMarked
public class DictDAO extends BaseDAO<Dict, String, DictRepository> {

  DictDAO(DictRepository repository) {
    super(repository, Dict.class);
  }

  /// 数据复制器实例
  ///
  /// @return 字典数据复制器
  @Override
  public ICopier<Dict> getCopier() {
    return Copier.INSTANCE;
  }

  /// 分页查询
  ///
  /// @param odata 前端查询参数
  /// @return 字典列表
  @Override
  public Page<Dict> findAll(final ODataQueryOption odata) {
    Specification<Dict> specification = DictSpecs.isDictTag();
    Sort sort = Sort.by(Order.asc(Dict_.CODE));
    return this.findAll(odata, specification, sort);
  }

  /// 插入或更新字典数据
  ///
  /// 根据 tag、code 判断，如果字典存在，则跳过；否则保存。
  ///
  /// @param dict 字典信息
  public void upsert(final Dict dict) {
    Optional<Dict> exist = this.get().findByTagAndCode(dict.getTag(), dict.getCode());
    if (exist.isPresent()) {
      return;
    }
    this.get().save(dict);
  }

  /// 更新字典项的状态
  ///
  /// @param ids 需要更新的字典项ID集合
  /// @param enabled 更新后的启用状态，true为启用，false为禁用
  /// @return 更新影响的行数
  @Transactional
  public long updateStatus(Collection<String> ids, Boolean enabled) {
    return super.update((root, criteriaUpdate, _) -> criteriaUpdate
        .set(Dict_.enabled, enabled)
        .where(root.get(Dict_.id).in(ids))
    );
  }

  @Mapper
  interface Copier extends ICopier<Dict> {

    Copier INSTANCE = Mappers.getMapper(Copier.class);
  }
}
