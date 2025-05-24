package io.github.xezzon.zeroweb.common.grpc;

import static com.google.common.net.HttpHeaders.AUTHORIZATION;

import com.google.protobuf.InvalidProtocolBufferException;
import io.github.xezzon.zeroweb.auth.JwtAuth;
import io.github.xezzon.zeroweb.auth.JwtClaim;
import io.github.xezzon.zeroweb.auth.JwtClaimWrapper;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall.SimpleForwardingClientCall;
import io.grpc.ForwardingServerCall.SimpleForwardingServerCall;
import io.grpc.Metadata;
import io.grpc.Metadata.Key;
import io.grpc.MethodDescriptor;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.client.GlobalClientInterceptor;
import org.springframework.grpc.server.GlobalServerInterceptor;
import org.springframework.stereotype.Component;

/**
 * 服务间调用传递认证信息
 * @author xezzon
 */
@GlobalServerInterceptor
@GlobalClientInterceptor
@Component
@Slf4j
public class GrpcJwtInterceptor implements ServerInterceptor, ClientInterceptor {

  private static final Key<byte[]> BEARER = Key.of(
      AUTHORIZATION + Metadata.BINARY_HEADER_SUFFIX,
      Metadata.BINARY_BYTE_MARSHALLER
  );

  /**
   * 服务端拦截器
   */
  @Override
  public <ReqT, RespT> Listener<ReqT> interceptCall(
      final ServerCall<ReqT, RespT> call,
      final Metadata requestHeaders,
      final ServerCallHandler<ReqT, RespT> next
  ) {
    try {
      final byte[] jwtClaimBytes = requestHeaders.get(BEARER);
      if (jwtClaimBytes != null) {
        final JwtClaim claim = JwtClaim.parseFrom(jwtClaimBytes);
        JwtAuth.save(new JwtClaimWrapper(claim));
      }
    } catch (RuntimeException | InvalidProtocolBufferException e) {
      log.error("Parse JWT failed.", e);
    }
    return next.startCall(new SimpleForwardingServerCall<>(call) {
      @Override
      public void close(Status status, Metadata trailers) {
        super.close(status, trailers);
        JwtAuth.clear();
      }
    }, requestHeaders);
  }

  /**
   * 客户端拦截器
   */
  @Override
  public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
      final MethodDescriptor<ReqT, RespT> method,
      final CallOptions callOptions,
      final Channel next
  ) {
    return new SimpleForwardingClientCall<>(next.newCall(method, callOptions)) {
      @Override
      public void start(Listener<RespT> responseListener, Metadata headers) {
        JwtAuth.get().ifPresent(claimWrapper ->
            headers.put(BEARER, claimWrapper.jwtClaim().toByteArray())
        );
        super.start(responseListener, headers);
      }
    };
  }
}
