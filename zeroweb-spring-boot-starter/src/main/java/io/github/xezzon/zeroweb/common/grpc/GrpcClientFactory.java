package io.github.xezzon.zeroweb.common.grpc;

import io.github.xezzon.zeroweb.attachment.AttachmentGrpc.AttachmentBlockingStub;
import io.github.xezzon.zeroweb.attachment.AttachmentGrpc.AttachmentStub;
import io.github.xezzon.zeroweb.dict.DictGrpc.DictBlockingStub;
import io.github.xezzon.zeroweb.dict.DictGrpc.DictStub;
import io.github.xezzon.zeroweb.user.UserGrpc.UserBlockingStub;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.grpc.client.GrpcChannelBuilderCustomizer;
import org.springframework.grpc.client.ImportGrpcClients;
import org.springframework.stereotype.Component;

/**
 * 构造 gRPC 客户端 Bean。
 * @author xezzon
 */
@ImportGrpcClients(
    prefix = "default",
    target = "admin",
    types = {
        DictBlockingStub.class,
        DictStub.class,
        UserBlockingStub.class
    }
)
@ImportGrpcClients(
    prefix = "default",
    target = "file",
    types = {
        AttachmentBlockingStub.class,
        AttachmentStub.class
    }
)
@Component
public class GrpcClientFactory {

  /// gRPC 全局配置
  @Bean
  @Order(200)
  <T extends ManagedChannelBuilder<T>> GrpcChannelBuilderCustomizer<T> retryChannelCustomizer() {
    return (_, builder) -> builder.enableRetry().maxRetryAttempts(3);
  }
}
