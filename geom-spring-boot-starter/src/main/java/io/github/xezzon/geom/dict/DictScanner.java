package io.github.xezzon.geom.dict;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import io.github.xezzon.geom.dict.DictImportReqList.Builder;
import io.github.xezzon.tao.dict.IDict;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.stereotype.Component;

/**
 * @author xezzon
 */
@Component
public class DictScanner implements ImportBeanDefinitionRegistrar {

  @Override
  public void registerBeanDefinitions(
      @NotNull AnnotationMetadata metadata,
      @NotNull BeanDefinitionRegistry registry
  ) {
    final Builder dictList = DictImportReqList.newBuilder();
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
      DictImporter dictImporter = ((DefaultListableBeanFactory) registry)
          .getBean(DictImporter.class);
      dictImporter.importDict(dictList.build());
    } catch (IOException e) {
      throw new RuntimeException(e);
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
      throw new RuntimeException(e);
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
