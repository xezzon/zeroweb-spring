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

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Longs;
import feign.Feign;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import java.security.Security;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * ZeroWeb 开放接口请求构建器。
 * 继承自 Feign.Builder，用于构建和配置 Feign 客户端，以便与 ZeroWeb 开放接口进行安全通信。
 * 自动处理请求签名，包括添加应用访问凭据、时间戳和摘要。
 */
public class ZerowebOpenRequestBuilder extends Feign.Builder {

  static {
    Security.addProvider(new BouncyCastleProvider());
  }

  /**
   * 应用访问凭据（Access Key）。
   * 用于唯一标识和认证调用开放接口的客户端应用。
   */
  private final String accessKey;
  /**
   * 应用密钥（Secret Key）。
   * 用于生成请求摘要，确保请求的完整性和安全性。
   */
  private final byte[] secretKey;

  /**
   * 使用指定的应用访问凭据和应用密钥构造 ZeroWeb 开放接口请求构建器。
   * @param accessKey 用于身份验证的应用访问凭据。
   * @param secretKey 用于签名生成的应用密钥。
   */
  public ZerowebOpenRequestBuilder(String accessKey, String secretKey) {
    this.accessKey = accessKey;
    this.secretKey = Base64.getDecoder().decode(secretKey);
    this.requestInterceptor(new ZerowebOpenRequestInterceptor());
  }

  /**
   * <p>OpenFeign 请求拦截器实现。</p>
   * <p>负责在每个请求发送前，向请求头中添加 ZeroWeb 开放接口所需的认证信息，
   * 包括应用访问凭据（X-Access-Key）、时间戳（X-Timestamp）和请求摘要（X-Signature）。</p>
   */
  class ZerowebOpenRequestInterceptor implements RequestInterceptor {

    /**
     * <p>应用请求拦截逻辑。</p>
     * <p>在此处为每个 Feign 请求添加 `X-Access-Key`、`X-Timestamp` 和 `X-Signature` 请求头。</p>
     * <ul>
     *   <li>`X-Access-Key`：客户端的应用访问凭据。</li>
     *   <li>`X-Timestamp`：当前请求时间戳，用于签名盐值和防止重放攻击。</li>
     *   <li>`X-Signature`：请求体和时间戳的 HMAC-SHA256 摘要，使用应用密钥进行签名。</li>
     * </ul>
     * @param requestTemplate HTTP 请求客户端。
     */
    @Override
    public void apply(RequestTemplate requestTemplate) {
      // 应用访问凭据
      requestTemplate.header(ZerowebOpenConstant.ACCESS_KEY_HEADER, accessKey);
      // 时间戳
      final long timestamp = Instant.now().toEpochMilli();
      requestTemplate.header(ZerowebOpenConstant.TIMESTAMP_HEADER, String.valueOf(timestamp));
      // 摘要
      try {
        final Mac mac = Mac.getInstance(ZerowebOpenConstant.DIGEST_ALGORITHM);
        mac.init(new SecretKeySpec(secretKey, ZerowebOpenConstant.DIGEST_ALGORITHM));
        final byte[] input = Optional.ofNullable(requestTemplate.body())
            .orElseGet(() -> new byte[0]);
        final byte[] salt = Longs.toByteArray(timestamp);
        mac.update(
            Bytes.concat(input, salt)
        );
        final String signature = Base64.getEncoder().encodeToString(mac.doFinal());
        requestTemplate.header(ZerowebOpenConstant.SIGNATURE_HEADER, signature);
      } catch (Exception e) {
        throw new ZerowebOpenException(e);
      }
    }
  }
}
