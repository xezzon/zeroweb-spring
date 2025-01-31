package io.github.xezzon.zeroweb.user.entity;

import io.github.xezzon.tao.trait.From;
import io.github.xezzon.tao.trait.Into;
import io.github.xezzon.zeroweb.user.domain.User;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @author xezzon
 */
@Getter
@Setter
public class RegisterUserReq implements Into<User> {

  /**
   * 用户名
   */
  @Pattern(
      regexp = "^\\w{3,32}$",
      message = "用户名必须是3~32位 小写字母/数字/下划线 组成的字符串"
  )
  private String username;
  /**
   * 用户昵称
   */
  private String nickname;
  /**
   * 密码
   * 为了防止身份被冒用，由用户设置的、只有用户自己知晓的口令。
   */
  @Pattern(
      regexp = "^(?!^\\d+$)(?!^[a-z]+$)(?!^[A-Z]+$)[\\x21-\\x7E]{8,}$",
      message = "密码由至少8位有效字符构成，且不允许是纯数字、纯小写或者纯大写字母"
  )
  private String password;

  @Override
  public User into() {
    return Converter.INSTANCE.from(this);
  }

  @Mapper
  interface Converter extends From<RegisterUserReq, User> {

    Converter INSTANCE = Mappers.getMapper(Converter.class);

    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cipher", ignore = true)
    @Override
    User from(RegisterUserReq registerUserReq);
  }
}
