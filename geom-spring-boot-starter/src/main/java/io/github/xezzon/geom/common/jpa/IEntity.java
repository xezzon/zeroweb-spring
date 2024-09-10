package io.github.xezzon.geom.common.jpa;

import jakarta.persistence.MappedSuperclass;

/**
 * @author xezzon
 */
@MappedSuperclass
public interface IEntity<T> {

  T getId();
}
