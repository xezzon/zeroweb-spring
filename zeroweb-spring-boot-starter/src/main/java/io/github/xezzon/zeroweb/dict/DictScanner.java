/*
 * SPDX-FileCopyrightText: Copyright (C) 2025 xezzon
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * This file is part of ZeroWeb.
 *
 * ZeroWeb is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * ZeroWeb is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with ZeroWeb. If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.xezzon.zeroweb.dict;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import io.github.xezzon.zeroweb.common.exception.ZerowebRuntimeException;
import io.github.xezzon.zeroweb.core.trait.IDict;
import io.github.xezzon.zeroweb.dict.DictImportReqList.Builder;
import jakarta.annotation.Resource;
import java.lang.annotation.Annotation;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.stereotype.Component;

/// 字典扫描器，在 Bean 注册阶段扫描 classpath 下所有实现了 [IDict] 接口的枚举类，并在应用启动时将其注册到数据库中。
///
/// @author xezzon
@Component
@Slf4j
public class DictScanner implements ImportBeanDefinitionRegistrar, ApplicationRunner {

  /// 字典导入器
  @Resource
  private DictImporter dictImporter;

  /// 字典导入请求列表构建器，用于收集扫描到的字典数据。
  private static final Builder dictList = DictImportReqList.newBuilder();

  /// 注册 Bean 定义。
  ///
  /// 扫描 classpath 下所有实现了 [IDict] 接口的枚举类，
  /// 并将其添加到待导入的字典列表中。
  ///
  /// @param metadata 注解元数据
  /// @param registry Bean 定义注册器
  @Override
  public void registerBeanDefinitions(
      @NonNull final AnnotationMetadata metadata,
      @NonNull final BeanDefinitionRegistry registry
  ) {
    AnnotationDictConfiguration configuration = new AnnotationDictConfiguration(metadata);
    String classpath = configuration.getValue();
    try {
      final ImmutableSet<@NonNull ClassInfo> classInfos = ClassPath
          .from(ClassLoader.getSystemClassLoader())
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

  /// 应用程序启动阶段回调。
  ///
  /// 将之前扫描到的字典数据导入到数据库中。此操作不影响应用的正常启动流程。
  ///
  /// @param args 应用程序参数
  @Override
  public void run(@NonNull final ApplicationArguments args) {
    try {
      dictImporter.importDict(dictList.build());
    } catch (Exception e) {
      log.warn("Import Dict failed.", e);
    }
  }
}

/// [EnableDictScan] 注解的配置信息封装类。
class AnnotationDictConfiguration {

  /// 被 [EnableDictScan] 注解的类。
  private final Class<?> applicationClass;
  /// [EnableDictScan] 注解的属性。
  private final AnnotationAttributes attributes;

  /// 构造一个新的 `AnnotationDictConfiguration` 实例。
  ///
  /// @param metadata 注解元数据，通常来自 [AnnotationMetadata]。
  /// @throws IllegalArgumentException 如果未找到 [EnableDictScan] 注解属性。
  /// @throws ZerowebRuntimeException  如果无法找到或加载被注解的类。
  AnnotationDictConfiguration(@NonNull final AnnotationMetadata metadata) {
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

  /// 获取 [EnableDictScan#value()] 的值。
  ///
  /// 如果注解的 `value` 属性未设置或为空，则返回被注解类的包路径。
  ///
  /// @return classpath 路径，用于字典扫描。
  public String getValue() {
    String classpath = this.attributes.getString("value");
    if (classpath.isEmpty()) {
      classpath = this.applicationClass.getPackageName();
    }
    return classpath;
  }
}
