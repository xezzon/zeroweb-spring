package io.github.xezzon.geom.dict;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import io.github.xezzon.geom.common.exception.ErrorCode;
import io.github.xezzon.geom.common.exception.GeomRuntimeException;
import io.github.xezzon.geom.dict.DictImportReqList.Builder;
import io.github.xezzon.tao.dict.IDict;
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
 * @author xezzon
 */
@Component
@Slf4j
public class DictScanner implements ImportBeanDefinitionRegistrar, CommandLineRunner {

  @Resource
  private DictImporter dictImporter;

  private static final Builder dictList = DictImportReqList.newBuilder();

  @Override
  public void registerBeanDefinitions(
      @NotNull AnnotationMetadata metadata,
      @NotNull BeanDefinitionRegistry registry
  ) {
    AnnotationDictConfiguration configuration =
        new AnnotationDictConfiguration(metadata, EnableDictScan.class);
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

  @Override
  public void run(String... args) throws Exception {
    try {
      dictImporter.importDict(dictList.build());
    } catch (Exception e) {
      log.warn("Import Dict failed.", e);
    }
  }
}

class AnnotationDictConfiguration {

  private final Class<?> applicationClass;
  private final AnnotationAttributes attributes;

  AnnotationDictConfiguration(
      @NotNull AnnotationMetadata metadata,
      @NotNull Class<? extends Annotation> annotation
  ) {
    Map<String, Object> attributesSource = metadata.getAnnotationAttributes(annotation.getName());
    if (attributesSource == null) {
      throw new IllegalArgumentException(
          String.format("Couldn't find annotation attributes for %s in %s", annotation, metadata)
      );
    }
    try {
      this.applicationClass = Class.forName(metadata.getClassName());
    } catch (ClassNotFoundException e) {
      throw new GeomRuntimeException(ErrorCode.UNKNOWN, e);
    }
    this.attributes = new AnnotationAttributes(attributesSource);
  }

  public String getValue() {
    String classpath = this.attributes.getString("value");
    if (classpath.isEmpty()) {
      classpath = this.applicationClass.getPackageName();
    }
    return classpath;
  }
}
