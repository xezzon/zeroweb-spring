package io.github.xezzon.zeroweb.dict.converter;

import io.github.xezzon.tao.trait.From;
import io.github.xezzon.zeroweb.dict.DictResp;
import io.github.xezzon.zeroweb.dict.domain.Dict;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @author xezzon
 */
@Mapper
public interface DictRespConverter extends From<Dict, DictResp> {

  DictRespConverter INSTANCE = Mappers.getMapper(DictRespConverter.class);

  @Mapping(target = "unknownFields", ignore = true)
  @Mapping(target = "tagBytes", ignore = true)
  @Mapping(target = "parentIdBytes", ignore = true)
  @Mapping(target = "mergeUnknownFields", ignore = true)
  @Mapping(target = "mergeFrom", ignore = true)
  @Mapping(target = "labelBytes", ignore = true)
  @Mapping(target = "idBytes", ignore = true)
  @Mapping(target = "defaultInstanceForType", ignore = true)
  @Mapping(target = "codeBytes", ignore = true)
  @Mapping(target = "clearOneof", ignore = true)
  @Mapping(target = "clearField", ignore = true)
  @Mapping(target = "allFields", ignore = true)
  @Override
  DictResp from(Dict dict);
}
