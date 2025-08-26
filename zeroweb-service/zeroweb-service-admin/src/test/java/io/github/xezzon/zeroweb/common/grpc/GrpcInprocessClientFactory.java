package io.github.xezzon.zeroweb.common.grpc;

import io.github.xezzon.zeroweb.dict.DictGrpc.DictBlockingStub;
import io.github.xezzon.zeroweb.dict.DictGrpc.DictStub;
import io.github.xezzon.zeroweb.user.UserGrpc.UserBlockingStub;
import org.springframework.grpc.client.ImportGrpcClients;
import org.springframework.stereotype.Component;

/**
 * @author xezzon
 */
@ImportGrpcClients(
    target = "in-process:admin",
    types = {
        DictBlockingStub.class,
        DictStub.class,
        UserBlockingStub.class
    }
)
@Component
public class GrpcInprocessClientFactory {

}
