package io.github.xezzon.zeroweb.dict;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

/**
 * 启用注解扫描。将指定包下实现了 IDict 接口的枚举类，导入系统的字典配置中。
 * @author xezzon
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(DictScanner.class)
public @interface EnableDictScan {

  /**
   * @return 注解扫描的目标包路径。默认为空，即扫描当前类所在的包及其子包。
   */
  String value() default "";
}
