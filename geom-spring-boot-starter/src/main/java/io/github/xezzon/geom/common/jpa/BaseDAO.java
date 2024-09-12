package io.github.xezzon.geom.common.jpa;

import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.github.xezzon.geom.common.odata.ODataQueryOption;
import io.github.xezzon.tao.trait.NewType;
import jakarta.persistence.EntityManager;
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
  private JPAQueryFactory queryFactory;

  protected BaseDAO(M repository) {
    this.repository = repository;
  }

  public abstract ICopier<T> getCopier();

  /**
   * 获取DO对象
   * @return DO对象
   */
  protected abstract EntityPathBase<T> getQuery();

  protected JPAQueryFactory getQueryFactory() {
    return this.queryFactory;
  }

  @Autowired
  private void setQueryFactory(EntityManager em) {
    this.queryFactory = new JPAQueryFactory(em);
  }

  @Override
  public M get() {
    return this.repository;
  }

  public T partialUpdate(T target) {
    T entity = this.get().findById(target.getId())
        .orElseThrow();
    this.getCopier().copy(target, entity);
    this.get().save(entity);
    return entity;
  }

  public Page<T> findAll(ODataQueryOption odata) {
    return this.findAll(odata, null, null);
  }

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

  public interface ICopier<T> {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void copy(T target, @MappingTarget T entity);
  }
}
