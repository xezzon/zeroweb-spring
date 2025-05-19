package io.github.xezzon.zeroweb.auth.event;

import io.github.xezzon.zeroweb.user.User;
import lombok.Builder;
import lombok.Getter;

/**
 * 用户登录事件 登录后，将用户信息、授权信息加载到会话中
 * @author xezzon
 */
@Getter
@Builder
public class UserLoginEvent {

  private User user;
}
