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

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.github.xezzon.zeroweb.common.constant.DatabaseConstant;
import io.github.xezzon.zeroweb.common.domain.Id;
import io.github.xezzon.zeroweb.common.metadata.PermissionConstant;
import io.github.xezzon.zeroweb.core.odata.ODataRequestParam;
import io.github.xezzon.zeroweb.core.tree.TreeList;
import io.github.xezzon.zeroweb.dict.Dict;
import io.github.xezzon.zeroweb.dict.entity.AddDictReq;
import io.github.xezzon.zeroweb.dict.entity.ModifyDictReq;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/// 字典管理
///
/// @author xezzon
@RestController
@RequestMapping("/dict")
public class DictHttpEndpoint {

  private final DictService dictService;

  /// 依赖注入
  /// @param dictService 字典管理服务
  public DictHttpEndpoint(final DictService dictService) {
    this.dictService = dictService;
  }

  /// 新增字典目/字典项
  ///
  /// @param req 对于字典项，字典目、上级ID不能为空
  /// @return 字典ID
  @SaCheckPermission({PermissionConstant.DICT_WRITE})
  @PostMapping()
  public Id addDict(@RequestBody @Valid final AddDictReq req) {
    Dict dict = req.into();
    if (dict.getTag() == null || dict.getParentId() == null) {
      // 新增字典目时，由后端设置以下属性的值
      dict.setTag(Dict.DICT_TAG);
      dict.setParentId(DatabaseConstant.ROOT_ID);
    }
    dictService.addDict(dict);
    return Id.of(dict.getId());
  }

  /// 分页查询字典目列表
  ///
  /// @param odata OData 查询参数
  /// @return 字典目列表
  @SaCheckPermission({PermissionConstant.DICT_READ})
  @GetMapping()
  public Page<@NonNull Dict> getDictTagList(final ODataRequestParam odata) {
    return dictService.pagedList(odata.into());
  }

  /// 查询指定字典目下所有字典项的列表
  ///
  /// @param tag 字典目编码
  /// @return 字典项列表（树形结构）
  @GetMapping("/tag/{tag}")
  public List<Dict> getDictTreeByTag(@PathVariable @NotBlank final String tag) {
    List<Dict> dictItemList = dictService.getDictItemList(tag);
    return TreeList.from(dictItemList);
  }

  /// 更新字典目/字典项
  ///
  /// @param req 字典修改请求对象
  @SaCheckPermission({PermissionConstant.DICT_WRITE})
  @PutMapping()
  public void modifyDict(@RequestBody @Valid final ModifyDictReq req) {
    Dict dict = req.into();
    dictService.modifyDict(dict);
  }

  /// 批量更新字典状态
  ///
  /// @param ids 字典ID集合
  /// @param enabled 更新后的字典启用状态
  @SaCheckPermission({PermissionConstant.DICT_WRITE})
  @PutMapping("/update-status")
  public void updateDictStatus(
      @RequestBody @NotNull final Collection<String> ids,
      @RequestParam final Boolean enabled
  ) {
    dictService.updateDictStatus(ids, enabled);
  }

  /// 批量删除字典目/字典项
  ///
  /// @param ids 字典ID集合
  @SaCheckPermission({PermissionConstant.DICT_WRITE})
  @DeleteMapping()
  public void removeDict(@RequestBody @NotNull final Collection<String> ids) {
    dictService.remove(ids);
  }
}
