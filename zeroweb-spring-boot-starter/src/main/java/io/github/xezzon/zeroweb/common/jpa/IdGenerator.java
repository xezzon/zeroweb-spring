package io.github.xezzon.zeroweb.common.jpa;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.hibernate.annotations.IdGeneratorType;

/**
 * 标记实体类的 id 以自定义的方式生成
 * @author xezzon
 * @see HibernateIdGenerator
 */
@IdGeneratorType(HibernateIdGenerator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface IdGenerator {

}
