package io.github.xezzon.zeroweb.crypto;

import static io.github.xezzon.zeroweb.crypto.constant.ZxcvbnConstant.ZXCVBN;

import cn.hutool.core.util.RandomUtil;
import com.nulabinc.zxcvbn.Strength;
import io.github.xezzon.zeroweb.crypto.entity.PasswordStrength;
import io.github.xezzon.zeroweb.crypto.exception.PasswordStrengthException;
import io.github.xezzon.zeroweb.setting.Setting;
import io.github.xezzon.zeroweb.setting.repository.SettingRepository;
import jakarta.annotation.Resource;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Collections;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author xezzon
 */
@SpringBootTest
class PasswordServiceTest {

  private static String schemaJson;
  private final Setting setting = new Setting();
  @Resource
  private IPasswordService passwordService;
  @Resource
  private SettingRepository settingRepository;

  @BeforeAll
  static void beforeAll() throws IOException {
    Path schemaResource = PasswordServiceTest.class.getClassLoader()
        .resources("schemas/" + PasswordStrength.SETTING_KEY + ".json")
        .map(url -> {
          try {
            return url.toURI();
          } catch (URISyntaxException e) {
            throw new RuntimeException(e);
          }
        })
        .map(Path::of)
        .findFirst().orElseThrow();
    schemaJson = Files.readString(schemaResource);
  }

  @BeforeEach
  void setUp() {
    setting.setCode(PasswordStrength.SETTING_KEY);
    setting.setSchema(schemaJson);
    setting.setValue(Collections.singletonMap("score", 2));
    setting.setUpdateTime(Instant.now());
  }

  @AfterEach
  void tearDown() {
    if (setting.getId() == null) {
      return;
    }
    settingRepository.delete(setting);
  }

  @Test
  void checkStrength_success() {
    final String strongPassword = "asder90-aS&D*TYf9s9FVHQAEORY,ASD";
    settingRepository.save(setting);

    Strength measure = ZXCVBN.measure(strongPassword);
    Assertions.assertDoesNotThrow(() -> passwordService.checkStrength(measure));
  }

  @Test
  void checkStrength_failed() {
    final String weakPassword = "p@ssword";
    settingRepository.save(setting);

    Strength measure = ZXCVBN.measure(weakPassword);
    Assertions.assertThrowsExactly(PasswordStrengthException.class, () ->
        passwordService.checkStrength(measure)
    );
  }

  @Test
  void checkStrength_skip() {
    final String password = RandomUtil.randomString(8);
    Strength measure = ZXCVBN.measure(password);
    Assertions.assertDoesNotThrow(() -> passwordService.checkStrength(measure));
  }
}
