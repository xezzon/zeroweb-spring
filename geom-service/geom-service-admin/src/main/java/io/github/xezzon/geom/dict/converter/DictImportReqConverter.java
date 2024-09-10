package io.github.xezzon.geom.dict.converter;

import io.github.xezzon.geom.dict.DictImportReq;
import io.github.xezzon.geom.dict.domain.Dict;
import io.github.xezzon.tao.trait.From;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @author xezzon
 */
@Mapper
public interface DictImportReqConverter extends From<DictImportReq, Dict> {

  DictImportReqConverter INSTANCE = Mappers.getMapper(DictImportReqConverter.class);


  @Mapping(target = "parentId", ignore = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "enabled", constant = "true")
  @Mapping(target = "editable", constant = "false")
  @Mapping(target = "children", ignore = true)
  @Mapping(target = "tag", source = "tag", defaultValue = Dict.DICT_TAG)
  @Override
  Dict from(DictImportReq source);
}
