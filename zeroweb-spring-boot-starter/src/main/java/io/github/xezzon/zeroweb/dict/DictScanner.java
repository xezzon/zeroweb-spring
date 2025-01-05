package io.github.xezzon.zeroweb.dict;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import io.github.xezzon.tao.dict.IDict;
import io.github.xezzon.zeroweb.common.exception.ZerowebRuntimeException;
import io.github.xezzon.zeroweb.dict.DictImportReqList.Builder;
import jakarta.annotation.Resource;
import java.lang.annotation.Annotation;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.stereotype.Component;

/**
 * 在 Bean 注册阶段扫描 classpath 下所有实现了 IDict 接口的枚举类，并在用用启动时将其注册到数据库中。
 * @author xezzon
 */
@Component
@Slf4j
public class DictScanner implements ImportBeanDefinitionRegistrar, CommandLineRunner {

  @Resource
  private DictImporter dictImporter;

  private static final Builder dictList = DictImportReqList.newBuilder();

  /**
   * Bean 注册阶段，扫描 classpath 下所有实现了 IDict 接口的枚举类。
   */
  @Override
  public void registerBeanDefinitions(
      @NotNull AnnotationMetadata metadata,
      @NotNull BeanDefinitionRegistry registry
  ) {
    AnnotationDictConfiguration configuration = new AnnotationDictConfiguration(metadata);
    String classpath = configuration.getValue();
    try {
      ImmutableSet<ClassInfo> classInfos = ClassPath.from(ClassLoader.getSystemClassLoader())
          .getTopLevelClassesRecursive(classpath);
      for (ClassInfo classInfo : classInfos) {
        Class<?> clazz = classInfo.load();
        if (!clazz.isEnum()
            || !IDict.class.isAssignableFrom(clazz)
            || clazz.getEnumConstants().length == 0
        ) {
          continue;
        }
        dictList.addData(DictImportReq.newBuilder()
            .setCode(((IDict) clazz.getEnumConstants()[0]).getTag())
            .setOrdinal(0)
        );
        for (Object enumConstant : clazz.getEnumConstants()) {
          IDict dict = (IDict) enumConstant;
          dictList.addData(DictImportReq.newBuilder()
              .setTag(dict.getTag())
              .setCode(dict.getCode())
              .setLabel(dict.getLabel())
              .setOrdinal(dict.getOrdinal())
              .build()
          );
        }
      }
    } catch (Exception e) {
      log.warn("Scan Dict failed.", e);
    }
  }

  /**
   * 应用启动阶段，将扫描到的枚举类注册到数据库中。
   * 不影响应用正常启动。
   */
  @Override
  public void run(String... args) {
    try {
      dictImporter.importDict(dictList.build());
    } catch (Exception e) {
      log.warn("Import Dict failed.", e);
    }
  }
}

/**
 * {@link EnableDictScan} 注解的配置信息
 */
class AnnotationDictConfiguration {

  /**
   * 被注解的类
   */
  private final Class<?> applicationClass;
  /**
   * 注解内容
   */
  private final AnnotationAttributes attributes;

  AnnotationDictConfiguration(@NotNull AnnotationMetadata metadata) {
    Class<? extends Annotation> annotation = EnableDictScan.class;
    Map<String, Object> attributesSource = metadata.getAnnotationAttributes(annotation.getName());
    if (attributesSource == null) {
      throw new IllegalArgumentException(
          String.format("Couldn't find annotation attributes for %s in %s", annotation, metadata)
      );
    }
    try {
      this.applicationClass = Class.forName(metadata.getClassName());
    } catch (ClassNotFoundException e) {
      throw new ZerowebRuntimeException(e);
    }
    this.attributes = new AnnotationAttributes(attributesSource);
  }

  /**
   * 获取 {@link EnableDictScan#value()} 的值，如果没有设置则返回当前类的包路径。
   * @return classpath 路径
   */
  public String getValue() {
    String classpath = this.attributes.getString("value");
    if (classpath.isEmpty()) {
      classpath = this.applicationClass.getPackageName();
    }
    return classpath;
  }
}
