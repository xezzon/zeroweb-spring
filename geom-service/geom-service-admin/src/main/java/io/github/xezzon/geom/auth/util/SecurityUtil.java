package io.github.xezzon.geom.auth.util;

import cn.dev33.satoken.stp.StpUtil;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import io.github.xezzon.geom.auth.JwtClaim;
import io.github.xezzon.geom.auth.JwtClaim.Builder;
import io.github.xezzon.tao.exception.ServerException;

/**
 * @author xezzon
 */
public class SecurityUtil {

  public static final String CLAIM_NAME = "claim";

  private SecurityUtil() {
    super();
  }

  /**
   * @return 用户认证信息
   */
  public static JwtClaim loadJwtClaim() {
    Builder builder = JwtClaim.newBuilder();
    String json = StpUtil.getSession()
        .getString(CLAIM_NAME);
    try {
      JsonFormat.parser().merge(json, builder);
      return builder.build();
    } catch (InvalidProtocolBufferException e) {
      throw new ServerException("无效的会话信息", e);
    }
  }

  public static void saveJwtClaim(JwtClaim claim) {
    try {
      StpUtil.getSession()
          .set(SecurityUtil.CLAIM_NAME, JsonFormat.printer().print(claim));
    } catch (InvalidProtocolBufferException e) {
      StpUtil.logout();
      throw new ServerException("无效的用户信息", e);
    }
  }
}
