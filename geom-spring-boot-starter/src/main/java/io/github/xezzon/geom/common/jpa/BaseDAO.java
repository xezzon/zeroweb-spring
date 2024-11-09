package io.github.xezzon.geom.common.jpa;

import io.github.xezzon.geom.core.odata.ODataQueryOption;
import io.github.xezzon.tao.trait.NewType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;
import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @param <T> 实体类型
 * @param <I> ID类型
 * @param <M> 实体操作类类型
 * @author xezzon
 */
public abstract class BaseDAO<T extends IEntity<I>, I, M extends JpaRepository<T, I> & JpaSpecificationExecutor<T>>
    implements
    NewType<M> {

  private final M repository;
  private final Class<T> typeToken;
  private EntityManager em;

  protected BaseDAO(M repository, Class<T> typeToken) {
    this.repository = repository;
    this.typeToken = typeToken;
  }

  public abstract ICopier<T> getCopier();

  @Autowired
  private void setEntityManager(EntityManager em) {
    this.em = em;
  }

  @Override
  public M get() {
    return this.repository;
  }

  /**
   * 局部更新实体（仅更新非空字段）
   * @param target 目标实体
   * @return 更新后的实体
   */
  public T partialUpdate(T target) {
    T entity = this.get().findById(target.getId())
        .orElseThrow();
    this.getCopier().copy(target, entity);
    this.get().save(entity);
    return entity;
  }

  /**
   * 分页查询数据
   * @param odata OData查询条件
   * @return 分页数据
   */
  public Page<T> findAll(ODataQueryOption odata) {
    return this.findAll(odata, null, null);
  }

  /**
   * 分页查询数据
   * @param odata OData查询条件
   * @param innerSpecification 服务端组装查询条件
   * @param innerSort 服务端组装排序条件
   * @return 分页数据
   */
  protected Page<T> findAll(
      ODataQueryOption odata,
      Specification<T> innerSpecification,
      Sort innerSort
  ) {
    if (innerSpecification == null) {
      innerSpecification = BaseSpecs.TRUE();
    }
    Specification<T> specification = Specification.allOf(innerSpecification);
    if (innerSort == null) {
      innerSort = Sort.unsorted();
    }
    Sort sort = Sort.unsorted().and(innerSort);
    Pageable pageable = Pageable.unpaged(sort);
    if (odata.getTop() != null) {
      pageable = PageRequest
          .ofSize(odata.getTop())
          .withPage(odata.getPageNumber())
          .withSort(sort)
      ;
    }
    return this.get().findAll(specification, pageable);
  }

  /**
   * 更新实体
   * @param predicate 组装更新条件
   * @return 更新影响的行数
   */
  protected int update(UpdateCriteriaBuilder<T> predicate) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaUpdate<T> criteriaUpdate = cb.createCriteriaUpdate(typeToken);
    Root<T> root = criteriaUpdate.from(typeToken);
    predicate.accept(root, criteriaUpdate, cb);
    return em.createQuery(criteriaUpdate).executeUpdate();
  }

  /**
   * 实体属性复制器
   * @param <T> 实体类型
   */
  public interface ICopier<T> {

    /**
     * 复制实体属性，如果源实体属性为null，则不复制
     * @param target 目标实体
     * @param entity 源实体
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void copy(T target, @MappingTarget T entity);
  }

  /**
   * 更新条件组装器
   * @param <T> 实体类型
   */
  @FunctionalInterface
  public interface UpdateCriteriaBuilder<T> {

    void accept(Root<T> root, CriteriaUpdate<T> query, CriteriaBuilder criteriaBuilder);
  }
}
