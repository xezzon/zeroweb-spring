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

package io.github.xezzon.zeroweb.call.internal;

import static io.github.xezzon.zeroweb.auth.AuthHttpConstant.BEARER;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import com.auth0.jwt.JWT;
import io.github.xezzon.zeroweb.ZerowebOpenConstant;
import io.github.xezzon.zeroweb.auth.JwtFilter;
import io.github.xezzon.zeroweb.subscription.ISubscriptionService4Call;
import io.github.xezzon.zeroweb.subscription.Subscription;
import io.github.xezzon.zeroweb.third_party_app.IThirdPartyAppService4Call;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

/// 订阅服务调用记录
///
/// @author xezzon
@RestController
@RequestMapping("/call")
public class SubscriptionCallHttpEndpoint {

  private final ISubscriptionService4Call subscriptionService;
  private final IThirdPartyAppService4Call thirdPartyAppService;

  /// 依赖注入
  /// @param subscriptionService 订阅服务
  /// @param thirdPartyAppService 第三方应用服务
  public SubscriptionCallHttpEndpoint(
      final ISubscriptionService4Call subscriptionService,
      final IThirdPartyAppService4Call thirdPartyAppService
  ) {
    this.subscriptionService = subscriptionService;
    this.thirdPartyAppService = thirdPartyAppService;
  }

  /// 转发 GET 请求
  /// @param openapiCode 对外接口编码
  /// @param body 请求体
  /// @param accessKey 用于标识一个第三方应用
  /// @param timestamp 摘要生成时间
  /// @param signature 请求体摘要
  /// @param headers 请求头
  /// @param request 请求信息
  /// @return 响应内容
  @GetMapping(value = "/{openapiCode}")
  public ResponseEntity<byte @NonNull []> forwardForSafe(
      @PathVariable @NotBlank final String openapiCode,
      @RequestBody(required = false) byte[] body,
      @RequestHeader(ZerowebOpenConstant.ACCESS_KEY_HEADER) final String accessKey,
      @RequestHeader(ZerowebOpenConstant.TIMESTAMP_HEADER) final Instant timestamp,
      @RequestHeader(ZerowebOpenConstant.SIGNATURE_HEADER) final String signature,
      @RequestHeader final HttpHeaders headers,
      final HttpServletRequest request
  ) {
    if (body == null) {
      body = new byte[0];
    }
    Map<String, String[]> parameterMap = request.getParameterMap();
    return forward(openapiCode, body, accessKey, timestamp, signature, headers, parameterMap);
  }

  /// 转发非 GET 请求
  /// @param openapiCode 对外接口编码
  /// @param body 请求体
  /// @param accessKey 用于标识一个第三方应用
  /// @param timestamp 摘要生成时间
  /// @param signature 请求体摘要
  /// @param headers 请求头
  /// @param request 请求信息
  /// @return 响应内容
  @RequestMapping(value = "/{openapiCode}", method = {POST, PUT, DELETE, PATCH})
  public ResponseEntity<byte @NonNull []> forwardForUnsafe(
      @PathVariable @NotBlank final String openapiCode,
      @RequestBody(required = false) byte[] body,
      @RequestHeader(ZerowebOpenConstant.ACCESS_KEY_HEADER) final String accessKey,
      @RequestHeader(ZerowebOpenConstant.TIMESTAMP_HEADER) final Instant timestamp,
      @RequestHeader(ZerowebOpenConstant.SIGNATURE_HEADER) final String signature,
      @RequestHeader final HttpHeaders headers,
      final HttpServletRequest request
  ) {
    if (body == null) {
      body = new byte[0];
    }
    Map<String, String[]> parameterMap = request.getParameterMap();
    return forward(openapiCode, body, accessKey, timestamp, signature, headers, parameterMap);
  }

  /// 转发请求
  ///
  /// @param openapiCode 对外路径
  /// @param body 请求体
  /// @param originalHeaders 请求头
  /// @param accessKey AccessKey（请求头）
  /// @param timestamp 时间戳（请求头）
  /// @param signature 签名（请求头）
  /// @param parameterMap 原始请求参数
  /// @return 响应体
  private ResponseEntity<byte @NonNull []> forward(
      String openapiCode, byte[] body, String accessKey, Instant timestamp, String signature,
      HttpHeaders originalHeaders, Map<String, String[]> parameterMap
  ) {
    /* 签发 JWT */
    String jwt = thirdPartyAppService.signJwt(accessKey, body, signature, timestamp);
    /* 获取相应的后端地址 */
    String appId = JWT.decode(jwt).getSubject();
    Subscription subscription = subscriptionService.getSubscription(appId, openapiCode);
    /* 转发请求 */
    originalHeaders.remove(ZerowebOpenConstant.TIMESTAMP_HEADER.toLowerCase());
    originalHeaders.remove(ZerowebOpenConstant.SIGNATURE_HEADER.toLowerCase());
    originalHeaders.remove(JwtFilter.PUBLIC_KEY_HEADER.toLowerCase());
    return RestClient.builder()
        .defaultStatusHandler(HttpStatusCode::isError, (_, _) -> {
        })
        .build()
        // 请求方法由对外接口定义
        .method(HttpMethod.valueOf(subscription.getOpenapi().getHttpMethod().getCode()))
        // 请求路径由目标地址定义
        .uri(subscription.getOpenapi().getDestination(), uri -> {
          // 请求参数与路径参数都由原始请求的请求参数提供
          parameterMap.forEach(uri::queryParam);
          Map<String, String> pathVariableMap = parameterMap.entrySet().parallelStream()
              .collect(Collectors.toMap(
                  Entry::getKey,
                  entry -> String.join(",", entry.getValue())
              ));
          return uri.build(pathVariableMap);
        })
        // 请求头由原始请求的请求头提供，但需要移除签名和时间戳
        .headers(headers -> headers.addAll(originalHeaders))
        // 认证头由本系统签发的 JWT 提供
        .header(HttpHeaders.AUTHORIZATION, BEARER + " " + jwt)
        // 请求体由原始请求的请求体提供
        .body(body)
        .retrieve()
        .toEntity(byte[].class);
  }
}
