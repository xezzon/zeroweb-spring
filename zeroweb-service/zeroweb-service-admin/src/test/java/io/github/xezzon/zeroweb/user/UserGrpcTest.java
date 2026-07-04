package io.github.xezzon.zeroweb.user;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cn.hutool.core.util.RandomUtil;
import io.github.xezzon.zeroweb.user.UserGrpc.UserBlockingStub;
import io.github.xezzon.zeroweb.user.repository.UserRepository;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.boot.grpc.test.autoconfigure.AutoConfigureTestGrpcTransport;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureTestGrpcTransport
class UserGrpcTest {

  @Resource
  private UserBlockingStub userBlockingStub;
  @Resource
  private UserRepository repository;

  @Test
  @Transactional
  void addUser() {
    AddUserResp resp = userBlockingStub.addUser(AddUserReq.newBuilder()
        .setUsername(RandomUtil.randomString(8))
        .setNickname(RandomUtil.randomString(8))
        .setPassword(RandomUtil.randomString(8))
        .build()
    );
    assertNotNull(resp.getId());
    Optional<User> after = repository.findById(resp.getId());
    assertTrue(after.isPresent());
  }
}
