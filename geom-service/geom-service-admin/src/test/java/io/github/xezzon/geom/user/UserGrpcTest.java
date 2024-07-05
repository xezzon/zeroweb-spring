package io.github.xezzon.geom.user;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cn.hutool.core.util.RandomUtil;
import io.github.xezzon.geom.user.UserGrpc.UserBlockingStub;
import io.github.xezzon.geom.user.domain.User;
import io.github.xezzon.geom.user.repository.UserRepository;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import java.util.Optional;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext
class UserGrpcTest {

  @GrpcClient("inProcess")
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
