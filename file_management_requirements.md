# 文件管理功能需求说明书

## 1. 新增文件

### 功能描述

客户端向服务端发送请求,包含文件的元数据(文件名、hash、大小、业务类型、业务ID等)
。服务端根据文件元数据判断文件是否已存在,如果存在则直接返回文件信息,否则生成文件ID、预签名上传地址并返回。

### 输入

| 参数名          | 类型     | 描述         | 是否必填 |
|--------------|--------|------------|------|
| fileName     | String | 文件名        | 是    |
| hash         | String | 文件内容的hash值 | 是    |
| size         | Long   | 文件大小,单位字节  | 是    |
| businessType | String | 业务类型       | 是    |
| businessId   | String | 业务ID       | 是    |

### 输出

**成功时**
| 参数名 | 类型 | 描述 |
| ---------- | ------ | ---------- |
| fileId | String | 文件ID |
| uploadUrl | String | 预签名的文件上传地址 |

**失败时**
| 参数名 | 类型 | 描述 |
| ---------- | ------ | ---------- |
| errorCode | String | 错误码 |
| message | String | 错误信息 |

### 处理逻辑

1. 服务端接收到客户端请求后,根据文件名(待定)、hash、大小联合判断文件是否已存在。
2. 如果文件已存在,则直接返回已存在的文件ID和预签名上传地址。
3. 如果文件不存在,则生成新的文件ID。
4. 生成预签名的文件上传地址。
5. 返回文件ID和预签名上传地址。

## 2. 上传文件

### 功能描述

客户端根据文件ID,按预签名的文件上传地址将文件上传,支持分片上传、断点续传。

### 输入

| 参数名 | 类型 | 描述 | 是否必填 |
| ---------- | ------ | ---------- |
| fileId | String | 文件ID | 是 |
| file | File | 文件内容 | 是 |
| chunk | Blob | 文件分片内容 | 否 |
| chunkIndex | Integer| 分片索引 | 否 |
| totalChunks| Integer| 总分片数 | 否 |

### 输出

**成功时**
| 参数名 | 类型 | 描述 |
| ---------- | ------ | ---------- |
| status | String | 上传状态,例如 "success" |

**失败时**
| 参数名 | 类型 | 描述 |
| ---------- | ------ | ---------- |
| errorCode | String | 错误码 |
| message | String | 错误信息 |

### 处理逻辑

1. 客户端根据预签名上传地址上传文件。
2. 服务端接收到文件上传请求,根据文件ID和分片信息处理文件上传。
3. 支持分片上传和断点续传。
4. 上传完成后,返回上传状态。

## 3. 查找文件

### 功能描述

支持按业务类型+业务ID查询文件列表,返回文件的元数据(含下载地址)。

### 输入

| 参数名          | 类型     | 描述   | 是否必填 |
|--------------|--------|------|------|
| businessType | String | 业务类型 | 是    |
| businessId   | String | 业务ID | 是    |

### 输出

**成功时**
| 参数名 | 类型 | 描述 |
| ---------- | ------ | ---------- |
| files | List | 文件列表 |
| files[].fileId | String | 文件ID |
| files[].fileName | String | 文件名 |
| files[].hash | String | 文件内容的hash值 |
| files[].size | Long | 文件大小,单位字节 |
| files[].downloadUrl | String | 文件下载地址 |

**失败时**
| 参数名 | 类型 | 描述 |
| ---------- | ------ | ---------- |
| errorCode | String | 错误码 |
| message | String | 错误信息 |

### 处理逻辑

1. 服务端接收到客户端请求后,根据业务类型和业务ID查询文件列表。
2. 返回文件列表,包含文件元数据和下载地址。

## 4. 文件删除

### 功能描述

按文件ID删除文件。

### 输入

| 参数名    | 类型     | 描述   | 是否必填 |
|--------|--------|------|------|
| fileId | String | 文件ID | 是    |

### 输出

**成功时**
| 参数名 | 类型 | 描述 |
| ---------- | ------ | ---------- |
| status | String | 删除状态,例如 "success" |

**失败时**
| 参数名 | 类型 | 描述 |
| ---------- | ------ | ---------- |
| errorCode | String | 错误码 |
| message | String | 错误信息 |

### 处理逻辑

1. 服务端接收到客户端请求后,根据文件ID删除文件。
2. 返回删除状态。
