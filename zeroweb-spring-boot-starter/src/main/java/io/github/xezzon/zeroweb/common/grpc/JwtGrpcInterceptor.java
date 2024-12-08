package io.github.xezzon.zeroweb.common.grpc;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;

/**
 * @author xezzon
 */
public class JwtGrpcInterceptor implements ClientInterceptor, ServerInterceptor {

  @Override
  public <Q, A> ClientCall<Q, A> interceptCall(
      MethodDescriptor<Q, A> method,
      CallOptions callOptions,
      Channel next
  ) {
    return next.newCall(method, callOptions);
  }

  @Override
  public <Q, A> Listener<Q> interceptCall(
      ServerCall<Q, A> call,
      Metadata headers,
      ServerCallHandler<Q, A> next
  ) {
    return next.startCall(call, headers);
  }
}
