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

import com.google.protobuf.Empty;
import io.github.xezzon.zeroweb.dict.Dict;
import io.github.xezzon.zeroweb.dict.DictGrpc.DictImplBase;
import io.github.xezzon.zeroweb.dict.DictImportReqList;
import io.github.xezzon.zeroweb.dict.DictListResp;
import io.github.xezzon.zeroweb.dict.DictReq;
import io.github.xezzon.zeroweb.dict.DictResp;
import io.github.xezzon.zeroweb.dict.converter.DictImportReqConverter;
import io.grpc.stub.StreamObserver;
import java.util.List;
import org.springframework.grpc.server.service.GrpcService;

/// @author xezzon
@GrpcService
public class DictGrpcEndpoint extends DictImplBase {

  private final DictService dictService;

  public DictGrpcEndpoint(final DictService dictService) {
    this.dictService = dictService;
  }

  /// 查询指定字典目下所有字典项的列表（服务间接口）
  @Override
  public void getDictListByTag(DictReq request, StreamObserver<DictListResp> responseObserver) {
    List<Dict> dictItemList = dictService.getDictItemList(request.getTag());
    List<DictResp> dictRespList = dictItemList.parallelStream()
        .map(dict -> DictResp.newBuilder()
            .setId(dict.getId())
            .setTag(dict.getTag())
            .setCode(dict.getCode())
            .setLabel(dict.getLabel())
            .setOrdinal(dict.getOrdinal())
            .setParentId(dict.getParentId())
            .build()
        )
        .toList();
    responseObserver.onNext(DictListResp.newBuilder()
        .addAllData(dictRespList)
        .build()
    );
    responseObserver.onCompleted();
  }

  /// 导入字典数据（服务间接口）
  @Override
  public void importDict(DictImportReqList request, StreamObserver<Empty> responseObserver) {
    List<Dict> dictList = request.getDataList().parallelStream()
        .map(DictImportReqConverter.INSTANCE::from)
        .toList();
    dictService.importDict(dictList);
    responseObserver.onNext(Empty.getDefaultInstance());
    responseObserver.onCompleted();
  }
}
