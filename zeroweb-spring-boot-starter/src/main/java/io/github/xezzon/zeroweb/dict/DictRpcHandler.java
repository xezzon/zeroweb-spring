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

package io.github.xezzon.zeroweb.dict;

import com.google.protobuf.Empty;
import io.github.xezzon.zeroweb.common.marker.RpcTrait;
import io.github.xezzon.zeroweb.dict.DictGrpc.DictStub;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.Resource;
import java.util.concurrent.CountDownLatch;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.TestOnly;
import org.springframework.stereotype.Component;

/**
 * 以 RPC 调用的方式实现字典导入功能，针对 zeroweb-service-admin 以外的服务。
 * @author xezzon
 */
@Slf4j
@Component
public class DictRpcHandler implements DictImporter, RpcTrait {

  @TestOnly
  private final CountDownLatch countDownLatch = new CountDownLatch(1);

  @Resource
  private DictStub dictStub;

  @TestOnly
  public CountDownLatch getCountDownLatch() {
    return this.countDownLatch;
  }

  @Override
  public void importDict(DictImportReqList reqList) {
    dictStub.importDict(reqList, new StreamObserver<>() {
      @Override
      public void onNext(Empty value) {
        log.debug("Finish to import dict with RPC.");
      }

      @Override
      public void onError(Throwable t) {
        log.error("Failed to import dict with RPC.", t);
      }

      @Override
      public void onCompleted() {
        countDownLatch.countDown();
      }
    });
  }
}
