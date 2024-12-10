package io.github.xezzon.zeroweb.call;

import static com.google.auth.http.AuthHttpConstants.BEARER;

import com.auth0.jwt.JWT;
import io.github.xezzon.zeroweb.ZerowebOpenConstant;
import io.github.xezzon.zeroweb.auth.JwtFilter;
import io.github.xezzon.zeroweb.subscription.domain.Subscription;
import io.github.xezzon.zeroweb.subscription.service.ISubscriptionService4Call;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

/**
 * 订阅服务调用记录
 * @author xezzon
 */
@Slf4j
@RestController
@RequestMapping("/call")
public class SubscriptionCallController {

  private final SubscriptionCallService subscriptionCallService;
  private final ISubscriptionService4Call subscriptionService;

  public SubscriptionCallController(
      SubscriptionCallService subscriptionCallService,
      ISubscriptionService4Call subscriptionService
  ) {
    this.subscriptionCallService = subscriptionCallService;
    this.subscriptionService = subscriptionService;
  }

  /**
   * 转发请求
   * @param openapiCode 对外路径
   * @param body 请求体
   * @param originalHeaders 请求头
   * @param accessKey AccessKey（请求头）
   * @param timestamp 时间戳（请求头）
   * @param signature 签名（请求头）
   * @param request 原始请求
   * @return 响应体
   */
  @RequestMapping("/{openapiCode}")
  public ResponseEntity<byte[]> redirect(
      @PathVariable String openapiCode,
      @RequestBody(required = false) byte[] body,
      @RequestHeader(ZerowebOpenConstant.ACCESS_KEY_HEADER) String accessKey,
      @RequestHeader(ZerowebOpenConstant.TIMESTAMP_HEADER) Instant timestamp,
      @RequestHeader(ZerowebOpenConstant.SIGNATURE_HEADER) String signature,
      @RequestHeader HttpHeaders originalHeaders,
      HttpServletRequest request
  ) {
    /* 签发JWT */
    String jwt = subscriptionCallService.signJwt(accessKey, body, signature, timestamp);
    /* 获取相应的后端地址 */
    String appId = JWT.decode(jwt).getSubject();
    Subscription subscription = subscriptionService.getSubscription(appId, openapiCode);
    /* 转发请求 */
    originalHeaders.remove(ZerowebOpenConstant.TIMESTAMP_HEADER.toLowerCase());
    originalHeaders.remove(ZerowebOpenConstant.SIGNATURE_HEADER.toLowerCase());
    originalHeaders.remove(JwtFilter.PUBLIC_KEY_HEADER.toLowerCase());
    return RestClient.builder()
        .defaultStatusHandler(HttpStatusCode::isError, (req, resp) -> {
        })
        .build()
        // 请求方法由对外接口定义
        .method(HttpMethod.valueOf(subscription.getOpenapi().getHttpMethod().getCode()))
        // 请求路径由目标地址定义
        .uri(subscription.getOpenapi().getDestination(), uri -> {
          // 请求参数与路径参数都由原始请求的请求参数提供
          Map<String, String[]> parameterMap = request.getParameterMap();
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
        // 认证头由本系统签发的JWT提供
        .header(HttpHeaders.AUTHORIZATION, BEARER + " " + jwt)
        // 请求体由原始请求的请求体提供
        .body(body)
        .retrieve()
        .toEntity(byte[].class);
  }
}
