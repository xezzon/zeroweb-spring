package io.github.xezzon.geom.call;

import static com.google.auth.http.AuthHttpConstants.BEARER;

import io.github.xezzon.geom.GeomOpenConstant;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Instant;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订阅服务调用记录
 * @author xezzon
 */
@RestController
@RequestMapping("/subscription-call")
public class SubscriptionCallController {

  private final SubscriptionCallService subscriptionCallService;

  public SubscriptionCallController(SubscriptionCallService subscriptionCallService) {
    this.subscriptionCallService = subscriptionCallService;
  }

  @PostMapping("/validate")
  public void validateCall(
      @RequestBody byte[] body,
      @RequestParam("path") String path,
      @RequestHeader(GeomOpenConstant.ACCESS_KEY_HEADER) String accessKey,
      @RequestHeader(GeomOpenConstant.TIMESTAMP_HEADER) Instant timestamp,
      @RequestHeader(GeomOpenConstant.SIGNATURE_HEADER) String signature,
      HttpServletResponse response
  ) {
    /* 所有校验通过后发放令牌 */
    String jwt = subscriptionCallService.signJwt(accessKey, path, body, signature, timestamp);
    response.setHeader(HttpHeaders.AUTHORIZATION, BEARER + " " + jwt);
  }
}
