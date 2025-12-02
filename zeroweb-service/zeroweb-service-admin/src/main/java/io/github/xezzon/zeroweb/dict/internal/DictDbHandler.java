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

import io.github.xezzon.zeroweb.common.marker.DbTrait;
import io.github.xezzon.zeroweb.common.marker.RpcTrait;
import io.github.xezzon.zeroweb.dict.Dict;
import io.github.xezzon.zeroweb.dict.DictImportReqList;
import io.github.xezzon.zeroweb.dict.DictImporter;
import io.github.xezzon.zeroweb.dict.DictRpcHandler;
import io.github.xezzon.zeroweb.dict.converter.DictImportReqConverter;
import java.util.List;
import org.springframework.stereotype.Service;

/// 字典管理在本服务实现，所以不需要以 RPC 调用的方式导入字典。
///
/// 实现比 [RpcTrait] 优先级更高的 [DbTrait] 接口，以覆盖 [DictRpcHandler]。
///
/// @author xezzon
@Service
public class DictDbHandler implements DictImporter, DbTrait {

  private final DictService dictService;

  public DictDbHandler(DictService dictService) {
    this.dictService = dictService;
  }

  @Override
  public void importDict(DictImportReqList reqList) {
    List<Dict> dictList = reqList.getDataList().parallelStream()
        .map(DictImportReqConverter.INSTANCE::from)
        .toList();
    dictService.importDict(dictList);
  }
}
