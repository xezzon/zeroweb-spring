package io.github.xezzon.geom.dict.converter;

import io.github.xezzon.geom.dict.domain.Dict;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

/**
 * @author xezzon
 */
@Mapper
public interface DictCopier {

  DictCopier INSTANCE = Mappers.getMapper(DictCopier.class);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void copy(Dict origin, @MappingTarget Dict target);
}
