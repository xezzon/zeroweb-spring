package io.github.xezzon.zeroweb.subscription.domain;

import io.github.xezzon.tao.trait.From;
import io.github.xezzon.tao.trait.Into;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @author xezzon
 */
public record AddSubscriptionReq(
    String appId,
    String openapiCode
) implements Into<Subscription> {

  @Override
  public Subscription into() {
    return Converter.INSTANCE.from(this);
  }

  @Mapper
  interface Converter extends From<AddSubscriptionReq, Subscription> {

    Converter INSTANCE = Mappers.getMapper(Converter.class);

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "openapi", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Override
    Subscription from(AddSubscriptionReq source);
  }
}