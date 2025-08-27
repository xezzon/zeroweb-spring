package io.github.xezzon.zeroweb.role.entity;

import io.github.xezzon.tao.trait.From;
import io.github.xezzon.tao.trait.Into;
import io.github.xezzon.zeroweb.role.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/// 新增角色
///
/// @param code 角色简码
/// @param name 角色名称
/// @param inheritable 是否允许该角色新建其下级角色
/// @param parentId 上级角色
/// @author xezzon
public record AddRoleReq(
    String code,
    String name,
    Boolean inheritable,
    String parentId
) implements Into<Role> {

  @Override
  public Role into() {
    return Converter.INSTANCE.from(this);
  }

  @Mapper
  interface Converter extends From<AddRoleReq, Role> {

    Converter INSTANCE = Mappers.getMapper(Converter.class);

    @Mapping(target = "value", ignore = true)
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Override
    Role from(AddRoleReq source);
  }
}
