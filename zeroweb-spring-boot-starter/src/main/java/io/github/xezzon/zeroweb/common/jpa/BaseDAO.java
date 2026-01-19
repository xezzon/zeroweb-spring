/*
 * SPDX-FileCopyrightText: Copyright (C) 2025-2026 xezzon
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

package io.github.xezzon.zeroweb.common.jpa;

import io.github.xezzon.zeroweb.core.odata.ODataQueryOption;
import io.github.xezzon.zeroweb.core.trait.NewType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/// 持久层基类，封装了常用的数据访问操作，支持 OData 查询。
///
/// @param <T> 实体类型
/// @param <I> ID类型
/// @param <M> 实体操作类类型
/// @author xezzon
public abstract class BaseDAO<T extends IEntity<I>, I, M extends JpaRepository<@NonNull T, @NonNull I> & JpaSpecificationExecutor<@NonNull T>>
    implements NewType<M> {

  private final M repository;
  private final Class<T> typeToken;
  private EntityManager em;

  /// 构造函数。
  ///
  /// @param repository 实体仓库
  /// @param typeToken 实体类型标记
  protected BaseDAO(M repository, Class<T> typeToken) {
    this.repository = repository;
    this.typeToken = typeToken;
  }

  /// 设置实体管理器。
  ///
  /// @param em 实体管理器
  @SuppressWarnings("unused")
  @Autowired
  private void setEntityManager(EntityManager em) {
    this.em = em;
  }

  /// 获取底层的 JpaRepository 实例。
  ///
  /// @return JpaRepository 实例
  @Override
  public M get() {
    return this.repository;
  }

  /// 分页查询数据
  /// @param odata OData 查询条件
  /// @return 分页数据
  public Page<@NonNull T> findAll(@NonNull final ODataQueryOption odata) {
    return this.findAll(odata, null, null);
  }

  /// 分页查询数据
  /// @param odata OData查询条件
  /// @param innerSpecification 服务端组装查询条件
  /// @param innerSort 服务端组装排序条件
  /// @return 分页数据
  protected Page<@NonNull T> findAll(
      @NonNull final ODataQueryOption odata,
      @Nullable Specification<@NonNull T> innerSpecification,
      @Nullable Sort innerSort
  ) {
    if (innerSpecification == null) {
      innerSpecification = BaseSpecs.identicallyEqual();
    }
    Specification<@NonNull T> specification = Specification.allOf(innerSpecification);
    if (innerSort == null) {
      innerSort = Sort.unsorted();
    }
    Sort sort = Sort.unsorted().and(innerSort);
    Pageable pageable = Pageable.unpaged(sort);
    if (odata.getTop() != null) {
      pageable = PageRequest
          .ofSize(odata.getTop())
          .withPage(odata.getPageNumber())
          .withSort(sort)
      ;
    }
    return this.get().findAll(specification, pageable);
  }

  /// 更新实体
  /// @param predicate 组装更新条件
  /// @return 更新影响的行数
  protected int update(UpdateCriteriaBuilder<T> predicate) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaUpdate<T> criteriaUpdate = cb.createCriteriaUpdate(typeToken);
    Root<T> root = criteriaUpdate.from(typeToken);
    predicate.accept(root, criteriaUpdate, cb);
    return em.createQuery(criteriaUpdate).executeUpdate();
  }

  /// 更新条件组装器接口。
  ///
  /// @param <T> 实体类型
  @FunctionalInterface
  public interface UpdateCriteriaBuilder<T> {

    /// 应用更新条件。
    ///
    /// @param root JPA Criteria API 的根对象
    /// @param query JPA Criteria API 的更新查询
    /// @param criteriaBuilder JPA Criteria API 的构建器
    void accept(Root<T> root, CriteriaUpdate<T> query, CriteriaBuilder criteriaBuilder);
  }
}
