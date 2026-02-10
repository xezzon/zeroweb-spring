package io.github.xezzon.zeroweb.user;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cn.hutool.core.util.RandomUtil;
import com.google.protobuf.Empty;
import io.github.xezzon.zeroweb.user.UserGrpc.UserBlockingStub;
import io.github.xezzon.zeroweb.user.UserGrpc.UserStub;
import io.github.xezzon.zeroweb.user.event.UserAddEvent;
import io.github.xezzon.zeroweb.user.repository.UserRepository;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.ApplicationEventPublisher;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@Slf4j
class UserGrpcTest {

  @Resource
  private UserBlockingStub userBlockingStub;
  @Resource
  private UserStub userStub;
  @Resource
  private UserRepository repository;
  @Resource
  private ApplicationEventPublisher eventPublisher;

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

  @Test
  void onUserCreated() throws InterruptedException {
    final CountDownLatch latch = new CountDownLatch(2);
    final List<UserCreatedEvent> receivedEvents1 = new ArrayList<>();
    final List<UserCreatedEvent> receivedEvents2 = new ArrayList<>();

    // First subscriber
    userStub.onUserCreated(Empty.getDefaultInstance(), new StreamObserver<>() {
      @Override
      public void onNext(UserCreatedEvent value) {
        receivedEvents1.add(value);
        latch.countDown();
      }

      @Override
      public void onError(Throwable t) {
        log.error("Error in first subscriber stream.", t);
        latch.countDown();
      }

      @Override
      public void onCompleted() {
        latch.countDown();
      }
    });

    // Second subscriber
    userStub.onUserCreated(Empty.getDefaultInstance(), new StreamObserver<>() {
      @Override
      public void onNext(UserCreatedEvent value) {
        receivedEvents2.add(value);
        latch.countDown();
      }

      @Override
      public void onError(Throwable t) {
        log.error("Error in second subscriber stream.", t);
        latch.countDown();
      }

      @Override
      public void onCompleted() {
        latch.countDown();
      }
    });

    // 等待订阅建立
    await().atMost(1, TimeUnit.SECONDS).until(() -> true);

    // Publish a UserAddEvent (Spring ApplicationEvent)
    String userId = RandomUtil.randomString(8);
    User user = new User();
    user.setId(userId);
    user.setUsername("test_user_multi");
    user.setCreateTime(Instant.now());
    eventPublisher.publishEvent(new UserAddEvent(this, user));

    // Wait for event to be received by both subscribers
    assertTrue(latch.await(5, TimeUnit.SECONDS));
    assertEquals(1, receivedEvents1.size());
    assertEquals(1, receivedEvents2.size());
    assertEquals(userId, receivedEvents1.getFirst().getUserId());
    assertEquals(userId, receivedEvents2.getFirst().getUserId());
  }
}
