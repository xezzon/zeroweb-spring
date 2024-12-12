package io.github.xezzon.zeroweb.call;

import static com.google.auth.http.AuthHttpConstants.BEARER;
import static org.springframework.web.bind.annotation.RequestMethod.*;

import com.auth0.jwt.JWT;
import io.github.xezzon.zeroweb.ZerowebOpenConstant;
import io.github.xezzon.zeroweb.auth.JwtFilter;
import io.github.xezzon.zeroweb.subscription.domain.Subscription;
import io.github.xezzon.zeroweb.subscription.service.ISubscriptionService4Call;
import io.github.xezzon.zeroweb.third_party_app.service.IThirdPartyAppService4Call;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
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

/**
 * 订阅服务调用记录
 * @author xezzon
 */
@RestController
@RequestMapping("/call")
public class SubscriptionCallController {

  private final ISubscriptionService4Call subscriptionService;
  private final IThirdPartyAppService4Call thirdPartyAppService;

  public SubscriptionCallController(
      ISubscriptionService4Call subscriptionService,
      IThirdPartyAppService4Call thirdPartyAppService
  ) {
    this.subscriptionService = subscriptionService;
    this.thirdPartyAppService = thirdPartyAppService;
  }

  /**
   * 转发 GET 请求
   */
  @GetMapping(value = "/{openapiCode}")
  public ResponseEntity<byte[]> forwardForSafe(
      @PathVariable String openapiCode,
      @RequestBody(required = false) byte[] body,
      @RequestHeader(ZerowebOpenConstant.ACCESS_KEY_HEADER) String accessKey,
      @RequestHeader(ZerowebOpenConstant.TIMESTAMP_HEADER) Instant timestamp,
      @RequestHeader(ZerowebOpenConstant.SIGNATURE_HEADER) String signature,
      @RequestHeader HttpHeaders headers,
      HttpServletRequest request
  ) {
    Map<String, String[]> parameterMap = request.getParameterMap();
    return forward(openapiCode, body, accessKey, timestamp, signature, headers, parameterMap);
  }

  /**
   * 转发非 GET 请求
   */
  @RequestMapping(value = "/{openapiCode}", method = {POST, PUT, DELETE, PATCH})
  public ResponseEntity<byte[]> forwardForUnsafe(
      @PathVariable String openapiCode,
      @RequestBody(required = false) byte[] body,
      @RequestHeader(ZerowebOpenConstant.ACCESS_KEY_HEADER) String accessKey,
      @RequestHeader(ZerowebOpenConstant.TIMESTAMP_HEADER) Instant timestamp,
      @RequestHeader(ZerowebOpenConstant.SIGNATURE_HEADER) String signature,
      @RequestHeader HttpHeaders headers,
      HttpServletRequest request
  ) {
    Map<String, String[]> parameterMap = request.getParameterMap();
    return forward(openapiCode, body, accessKey, timestamp, signature, headers, parameterMap);
  }

  /**
   * 转发请求
   * @param openapiCode 对外路径
   * @param body 请求体
   * @param originalHeaders 请求头
   * @param accessKey AccessKey（请求头）
   * @param timestamp 时间戳（请求头）
   * @param signature 签名（请求头）
   * @param parameterMap 原始请求参数
   * @return 响应体
   */
  private ResponseEntity<byte[]> forward(
      String openapiCode, byte[] body, String accessKey, Instant timestamp, String signature,
      HttpHeaders originalHeaders, Map<String, String[]> parameterMap
  ) {
    /* 签发JWT */
    String jwt = thirdPartyAppService.signJwt(accessKey, body, signature, timestamp);
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
