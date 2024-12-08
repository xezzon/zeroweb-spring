package io.github.xezzon.zeroweb.user.converter;

import io.github.xezzon.tao.trait.From;
import io.github.xezzon.zeroweb.user.AddUserReq;
import io.github.xezzon.zeroweb.user.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @author xezzon
 */
@Mapper
public interface AddUserReqConverter extends From<AddUserReq, User> {

  AddUserReqConverter INSTANCE = Mappers.getMapper(AddUserReqConverter.class);

  @Mapping(target = "updateTime", ignore = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createTime", ignore = true)
  @Mapping(target = "cipher", ignore = true)
  @Override
  User from(AddUserReq req);
}
