package io.github.xezzon.zeroweb.openapi;

import io.github.xezzon.zeroweb.common.exception.PublishedOpenapiCannotBeModifyException;
import io.github.xezzon.zeroweb.common.exception.RepeatDataException;
import io.github.xezzon.zeroweb.core.odata.ODataQueryOption;
import io.github.xezzon.zeroweb.openapi.domain.Openapi;
import io.github.xezzon.zeroweb.openapi.domain.OpenapiStatus;
import io.github.xezzon.zeroweb.openapi.service.IOpenapiService4Subscription;
import java.util.Objects;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * @author xezzon
 */
@Service
public class OpenapiService implements IOpenapiService4Subscription {

  private final OpenapiDAO openapiDAO;

  public OpenapiService(OpenapiDAO openapiDAO) {
    this.openapiDAO = openapiDAO;
  }

  /**
   * 添加一个新的对外接口对象到数据库
   * @param openapi 要添加的对外接口对象
   * @throws RepeatDataException 如果要添加的对外接口编码重复，则抛出异常
   */
  protected void addOpenapi(Openapi openapi) {
    this.checkRepeat(openapi);
    openapiDAO.get().save(openapi);
  }

  /**
   * 根据OData查询选项分页查询对外接口列表
   * @param odata OData查询选项
   * @return 分页查询结果，包含符合条件的对外接口列表
   */
  protected Page<Openapi> pageList(ODataQueryOption odata) {
    return openapiDAO.findAll(odata);
  }

  /**
   * 修改指定的对外接口对象
   * @param openapi 需要修改的对外接口对象
   * @throws RepeatDataException 如果要修改的对外接口编码重复，则抛出异常
   * @throws PublishedOpenapiCannotBeModifyException 如果要修改的Openapi已经发布且编码（即对外的路径）被修改，则抛出异常
   */
  protected void modifyOpenapi(Openapi openapi) {
    this.checkRepeat(openapi);
    Openapi entity = openapiDAO.get().getReferenceById(openapi.getId());
    if (entity.isPublished()
        && openapi.getCode() != null
        && !Objects.equals(entity.getCode(), openapi.getCode())
    ) {
      // 已发布的接口不能修改编码（即对外的路径）
      throw new PublishedOpenapiCannotBeModifyException();
    }
    openapiDAO.partialUpdate(openapi);
  }

  /**
   * 发布指定的对外接口
   * 如果指定接口已发布，则不做处理
   * @param id 要发布的对外接口的ID
   */
  protected void publishOpenapi(String id) {
    Openapi entity = openapiDAO.get().getReferenceById(id);
    entity.setStatus(OpenapiStatus.PUBLISHED);
    openapiDAO.get().save(entity);
  }

  private void checkRepeat(Openapi openapi) {
    Openapi exist = openapiDAO.get().findByCode(openapi.getCode());
    if (exist != null && !Objects.equals(exist.getId(), openapi.getId())) {
      throw new RepeatDataException("`" + openapi.getCode() + "`");
    }
  }

  @Override
  public Page<Openapi> listPublishedOpenapi(ODataQueryOption odata) {
    return openapiDAO.listPublishedOpenapi(odata);
  }

  @Override
  public @Nullable Openapi getByCode(String openapiCode) {
    return openapiDAO.get().findByCode(openapiCode);
  }
}
