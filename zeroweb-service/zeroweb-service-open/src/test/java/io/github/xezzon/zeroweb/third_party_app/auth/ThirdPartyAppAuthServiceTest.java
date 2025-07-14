package io.github.xezzon.zeroweb.third_party_app.auth;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.annotation.Resource;
import java.util.Collections;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class ThirdPartyAppAuthServiceTest {

  @Resource
  private ThirdPartyAppAuthService thirdPartyAppAuthService;
  @MockBean
  private ThirdPartyAppRoleRepository thirdPartyAppRoleRepository;
  @MockBean
  private ThirdPartyAppMemberRepository thirdPartyAppMemberRepository;

  @Test
  void deleteGroupRole() {
    String roleId = "test-role-id";
    thirdPartyAppAuthService.deleteGroupRole(roleId);
    verify(thirdPartyAppRoleRepository).deleteById(roleId);
    verify(thirdPartyAppMemberRepository).deleteByRoleId(roleId);
  }

  @Test
  void addGroupMember() {
    ThirdPartyAppMember member = new ThirdPartyAppMember();
    thirdPartyAppAuthService.addGroupMember(member);
    verify(thirdPartyAppMemberRepository).save(member);
  }
  @Test
  void listGroupMemberWithRole() {
    String roleId = "test-role-id";
    thirdPartyAppAuthService.listGroupMemberWithRole(roleId);
    verify(thirdPartyAppMemberRepository).findByRoleId(roleId);
  }

  @Test
  void listGroupMember() {
    String groupId = "test-group-id";
    thirdPartyAppAuthService.listGroupMember(groupId);
    verify(thirdPartyAppMemberRepository).findByGroupId(groupId);
  }

  @Test
  void listGroupRoleWithUser() {
    String userId = "test-user-id";
    thirdPartyAppAuthService.listGroupRoleWithUser(userId);
    verify(thirdPartyAppMemberRepository).findByUserId(userId);
  }

  @Test
  void releaseMember() {
    ThirdPartyAppMember member = new ThirdPartyAppMember();
    thirdPartyAppAuthService.releaseMember(member);
    verify(thirdPartyAppMemberRepository).delete(member);
  }

  @Test
  void removeMember() {
    String groupId = "test-group-id";
    String userId = "test-user-id";
    thirdPartyAppAuthService.removeMember(groupId, userId);
    verify(thirdPartyAppMemberRepository).deleteByGroupIdAndUserId(groupId, userId);
  }

  @Test
  void listAllPermission() {
    String userId = "test-user-id";
    thirdPartyAppAuthService.listAllPermission(userId);
    verify(thirdPartyAppMemberRepository).findByUserId(userId);
  }

  @Test
  void listPermissionByRoles() {
    ThirdPartyAppRole role = new ThirdPartyAppRole();
    role.setId("test-role-id");
    List<ThirdPartyAppRole> roles = Collections.singletonList(role);
    List<String> roleIds = Collections.singletonList("test-role-id");
    when(thirdPartyAppRoleRepository.findPermissionsByRoleIds(roleIds)).thenReturn(
        Collections.singletonList("test-permission"));
    List<String> permissions = thirdPartyAppAuthService.listPermissionByRoles(roles);
    Assertions.assertThat(permissions).containsExactly("test-permission");
    verify(thirdPartyAppRoleRepository).findPermissionsByRoleIds(roleIds);
  }
  @Test
  void bindRolePermission() {
    String roleId = "test-role-id";
    List<String> permissions = Collections.singletonList("test-permission");
    ThirdPartyAppRole role = new ThirdPartyAppRole();
    when(thirdPartyAppRoleRepository.findById(roleId)).thenReturn(java.util.Optional.of(role));
    thirdPartyAppAuthService.bindRolePermission(roleId, permissions);
    Assertions.assertThat(role.getPermissions()).containsAll(permissions);
    verify(thirdPartyAppRoleRepository).save(role);
  }
  @Test
  void revokeRolePermission() {
    String roleId = "test-role-id";
    List<String> permissionsToRevoke = Collections.singletonList("test-permission-1");
    ThirdPartyAppRole role = new ThirdPartyAppRole();
    role.getPermissions().add("test-permission-1");
    role.getPermissions().add("test-permission-2");
    when(thirdPartyAppRoleRepository.findById(roleId)).thenReturn(java.util.Optional.of(role));
    thirdPartyAppAuthService.revokeRolePermission(roleId, permissionsToRevoke);
    Assertions.assertThat(role.getPermissions()).doesNotContainAnyElementsOf(permissionsToRevoke);
    Assertions.assertThat(role.getPermissions()).contains("test-permission-2");
    verify(thirdPartyAppRoleRepository).save(role);
  }

  @Test
  void checkPermission_shouldPass_whenUserHasPermission() {
    String userId = "test-user-id";
    String permission = "test-permission";
    ThirdPartyAppMember member = new ThirdPartyAppMember();
    member.setRoleId("test-role-id");
    when(thirdPartyAppMemberRepository.findByUserId(userId)).thenReturn(Collections.singletonList(member));
    when(thirdPartyAppRoleRepository.findPermissionsByRoleIds(Collections.singletonList("test-role-id"))).thenReturn(Collections.singletonList(permission));
    Assertions.assertThatCode(() -> thirdPartyAppAuthService.checkPermission(userId, permission)).doesNotThrowAnyException();
  }

  @Test
  void checkPermission_shouldThrowException_whenUserHasNoPermission() {
    String userId = "test-user-id";
    String permission = "test-permission";
    ThirdPartyAppMember member = new ThirdPartyAppMember();
    member.setRoleId("test-role-id");
    when(thirdPartyAppMemberRepository.findByUserId(userId)).thenReturn(Collections.singletonList(member));
    when(thirdPartyAppRoleRepository.findPermissionsByRoleIds(Collections.singletonList("test-role-id"))).thenReturn(Collections.emptyList());
    Assertions.assertThatThrownBy(() -> thirdPartyAppAuthService.checkPermission(userId, permission)).isInstanceOf(IllegalStateException.class).hasMessage("Permission denied");
  }

  @Test
  void checkPermission_shouldThrowException_whenUserNotInAnyGroup() {
    String userId = "test-user-id";
    String permission = "test-permission";
    when(thirdPartyAppMemberRepository.findByUserId(userId)).thenReturn(Collections.emptyList());
    Assertions.assertThatThrownBy(() -> thirdPartyAppAuthService.checkPermission(userId, permission)).isInstanceOf(IllegalStateException.class).hasMessage("User not found in any group");
  }
}