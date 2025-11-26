package io.github.xezzon.zeroweb.setting;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author xezzon
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TestEntity {

  private String value;
  private List<Child> children;

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Child {

    private BigDecimal child;
  }
}
