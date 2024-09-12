package io.github.xezzon.geom.common.jpa;

import io.github.xezzon.geom.common.constant.DatabaseConstant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author xezzon
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "test_entity")
public class TestEntity implements IEntity<String> {

  @Id
  @Column(name = "id", nullable = false, updatable = false, length = DatabaseConstant.ID_LENGTH)
  @IdGenerator
  private String id;
  @Column(name = "field1")
  private String field1;
  @Column(name = "field2")
  private String field2;
}
