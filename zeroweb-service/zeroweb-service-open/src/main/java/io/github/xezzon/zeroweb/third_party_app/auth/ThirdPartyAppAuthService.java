package io.github.xezzon.zeroweb.third_party_app.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.xezzon.zeroweb.third_party_app.AccessSecret;
import io.github.xezzon.zeroweb.third_party_app.ThirdPartyApp;
import io.github.xezzon.zeroweb.third_party_app.repository.AccessSecretRepository;
import jakarta.annotation.Resource;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * @author xezzon
 */
@Service
public class ThirdPartyAppAuthService {

  private static final String GROUP_ID_CLAIM = "groupId";
  private static final String ROLE_ID_CLAIM = "roleId";
  @Resource
  private ThirdPartyAppRoleRepository thirdPartyAppRoleRepository;
  @Resource
  private ThirdPartyAppMemberRepository thirdPartyAppMemberRepository;
  @Resource
  private AccessSecretRepository accessSecretRepository;

  /**
   * 添加用户组角色
   */
  public void addGroupRole(ThirdPartyAppRole role) {
    thirdPartyAppRoleRepository.save(role);
  }

  /**
   * 列出用户组角色
   */
  public List<ThirdPartyAppRole> listGroupRole(String groupId) {
    return thirdPartyAppRoleRepository.findByGroupId(groupId);
  }

  /**
   * 删除指定角色，并解绑所有用户
   */
  public void deleteGroupRole(String roleId) {
    thirdPartyAppRoleRepository.deleteById(roleId);
    thirdPartyAppMemberRepository.deleteByRoleId(roleId);
  }

  /**
   * 生成邀请码。获得邀请码的用户由管理员同意后可以加入对第三方应用的管理。
   * 邀请码的形式是一个 JWT。过期时间即为邀请码有效时间。载荷中包含应用ID。最后使用应用的密钥进行签名。
   * @param app 第三方应用
   * @return 邀请码
   */
  String inviteMember(ThirdPartyApp app) {
    AccessSecret accessSecret = accessSecretRepository.findById(app.getId())
        .orElseThrow();
    return JWT.create()
        .withClaim(GROUP_ID_CLAIM, app.getId())
        .withClaim(ROLE_ID_CLAIM, "0")
        .withIssuedAt(Instant.now())
        .sign(Algorithm.HMAC256(accessSecret.getSecretKey()));
  }

  /**
   * 接收邀请的人，将其添加到用户组成员中，但当前的角色为0，不拥有任何权限。待管理员确认并分配权限后成为正式成员。
   * @param token 邀请码
   */
  void acceptInvitation(String token) {
    DecodedJWT decodedJWT = JWT.decode(token);
    String appId = decodedJWT.getClaim(GROUP_ID_CLAIM).asString();
    AccessSecret accessSecret = accessSecretRepository.findById(appId)
        .orElseThrow();
    JWT.require(Algorithm.HMAC256(accessSecret.getSecretKey()))
        .build()
        .verify(decodedJWT);
    ThirdPartyAppMember thirdPartyAppMember = new ThirdPartyAppMember();
    thirdPartyAppMemberRepository.save(thirdPartyAppMember);
  }

  /**
   * 确认待分配人员的角色，或为现有成员添加新的角色。
   */
  public void addGroupMember(ThirdPartyAppMember member) {
    thirdPartyAppMemberRepository.save(member);
  }

  /**
   * 列出用户组成员（按角色分组）
   */
  public List<ThirdPartyAppMember> listGroupMemberWithRole(String roleId) {
    return thirdPartyAppMemberRepository.findByRoleId(roleId);
  }

  /**
   * 列出用户组成员名单
   */
  public List<String> listGroupMember(String groupId) {
    return thirdPartyAppMemberRepository.findByGroupId(groupId).stream()
        .map(ThirdPartyAppMember::getUserId)
        .toList();
  }

  /**
   * 列出指定用户所在的用户组及角色
   */
  public List<ThirdPartyAppMember> listGroupRoleWithUser(String userId) {
    return thirdPartyAppMemberRepository.findByUserId(userId);
  }

  /**
   * 解绑用户组角色与成员
   */
  public void releaseMember(ThirdPartyAppMember member) {
    thirdPartyAppMemberRepository.delete(member);
  }

  /**
   * 删除用户组成员
   */
  public void removeMember(String groupId, String userId) {
    thirdPartyAppMemberRepository.deleteByGroupIdAndUserId(groupId, userId);
  }

  /**
   * 列出所有资源权限（仅当前用户能看的的）
   */
  public List<String> listAllPermission(String userId) {
    List<ThirdPartyAppMember> members = thirdPartyAppMemberRepository.findByUserId(userId);
    if (members.isEmpty()) {
      return Collections.emptyList();
    }
    List<String> roleIds = members.stream()
        .map(ThirdPartyAppMember::getRoleId)
        .toList();
    List<ThirdPartyAppRole> roles = thirdPartyAppRoleRepository.findAllById(roleIds);
    return listPermissionByRoles(roles);
  }

  /**
   * 批量查询用户组角色对该资源的权限
   */
  public List<String> listPermissionByRoles(Collection<ThirdPartyAppRole> roles) {
    if (roles.isEmpty()) {
      return Collections.emptyList();
    }
    List<String> roleIds = roles.stream()
        .map(ThirdPartyAppRole::getId)
        .toList();
    return thirdPartyAppRoleRepository.findPermissionsByRoleIds(roleIds);
  }

  /**
   * 为指定角色添加权限
   */
  public void bindRolePermission(String roleId, Collection<String> permissions) {
    ThirdPartyAppRole role = thirdPartyAppRoleRepository.findById(roleId)
        .orElseThrow(() -> new IllegalArgumentException("Role not found with id: " + roleId));
    role.getPermissions().addAll(permissions);
    thirdPartyAppRoleRepository.save(role);
  }

  /**
   * 为指定角色撤销权限
   */
  public void revokeRolePermission(String roleId, Collection<String> permissions) {
    ThirdPartyAppRole role = thirdPartyAppRoleRepository.findById(roleId)
        .orElseThrow(() -> new IllegalArgumentException("Role not found with id: " + roleId));
    role.getPermissions().removeAll(permissions);
    thirdPartyAppRoleRepository.save(role);
  }

  /**
   * 校验指定用户是否有指定权限
   */
  public void checkPermission(String userId, String permission) {
    List<ThirdPartyAppMember> members = thirdPartyAppMemberRepository.findByUserId(userId);
    if (members.isEmpty()) {
      throw new IllegalStateException("User not found in any group");
    }
    List<String> roleIds = members.stream()
        .map(ThirdPartyAppMember::getRoleId)
        .toList();
    List<String> permissions = thirdPartyAppRoleRepository.findPermissionsByRoleIds(roleIds);
    if (!permissions.contains(permission)) {
      throw new IllegalStateException("Permission denied");
    }
  }
}
