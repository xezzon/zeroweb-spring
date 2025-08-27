package io.github.xezzon.zeroweb.third_party_app.authn;

import com.auth0.jwt.exceptions.JWTVerificationException;
import io.github.xezzon.zeroweb.common.exception.ZerowebBusinessException;
import io.netty.handler.codec.http.HttpResponseStatus;

/// 邀请码已过期，或者不允许被当前用户使用
///
/// @author xezzon
public class InvalidInvitationCodeException extends ZerowebBusinessException {

  public static final String ERROR_CODE = "CFE05";

  public InvalidInvitationCodeException() {
    super("This invitation code is invalid for you.");
  }

  public InvalidInvitationCodeException(JWTVerificationException e) {
    super("This is an invalid or expired invitation code.");
    this.initCause(e);
  }

  @Override
  public String getCode() {
    return ERROR_CODE;
  }

  @Override
  public int getHttpStatus() {
    return HttpResponseStatus.FORBIDDEN.code();
  }
}
