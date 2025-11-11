package io.github.xezzon.zeroweb.common.grpc;

import io.github.xezzon.zeroweb.attachment.AttachmentGrpc.AttachmentBlockingStub;
import io.github.xezzon.zeroweb.attachment.AttachmentGrpc.AttachmentStub;
import org.springframework.grpc.client.ImportGrpcClients;
import org.springframework.stereotype.Component;

/// @author xezzon
@ImportGrpcClients(
    target = "in-process:file",
    types = {
        AttachmentBlockingStub.class,
        AttachmentStub.class
    }
)
@Component
public class GrpcInprocessClientFactory {

}
