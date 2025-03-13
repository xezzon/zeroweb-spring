package io.github.xezzon.zeroweb;

import feign.jackson.JacksonEncoder;

/**
 * @author xezzon
 */
public class RequestBuilder extends ZerowebOpenRequestBuilder {

  public RequestBuilder(String accessKey, String secretKey) {
    super(accessKey, secretKey);
    this.encoder(new JacksonEncoder());
  }
}
