## 接口设计规范

### 前后端接口

- 前后端交互的接口采用HTTP协议，遵顼RESTful命名规范。
- 路径命名法采用Kebab case。
- 路径参数都要放在第奇数位。
- 不能简单地表示为增删查改的复杂指令，非幂等的可以用`POST`方法，幂等的使用`PUT`方法。
- 更新、删除接口无需返回值（即返回类型为void）。标准新增接口的返回值为：`{"id":""}`，将新增后对象的ID返回，便于进行自动化测试。查询接口的返回值应为对象/对象列表/分页对象。响应码为`200`。
- 客户端异常（即需要用户处理的异常）响应码为`400`，服务端异常或第三方服务异常响应码为`500`。响应体结构为`{"code":"","message":"","data":{}}`。

以下是合适的接口示例：

- `GET /dict/id/{id}` 根据ID查询字典
- `GET /dict/page?filter=&pageNum=&pageSize=` 查询字典列表（分页）
- `GET /dict/tag/{tag}/code/{code}` 根据字典目、字典码查询字典
- `GET /dict/tag-code?tag=&code=` 根据字典目、字典目查询字典（风格二）
- `DELETE /dict/id/{id}` 根据ID删除字典
- `POST /dict/add` 新增字典
- `PUT /dict/update` 整体更新字典
- `POST /openapi/audit` 审核接口（审核接口会发起流程，所以是非幂等的）
- `PUT /openapi/cancel` 作废接口

### 服务间接口

- 服务间交互的接口（包括geom的后端服务之间、其他服务与geom之间）采用 gRPC 协议。

---

## 参考 

- [Microsoft REST API Guidelines](https://github.com/microsoft/api-guidelines)
