# 附件内容不流经业务接口

附件服务只登记元数据，文件的字节由客户端直接上传到服务返回的地址：S3 后端返回预签名 URL（`presignPutObject` / `presignUploadPart`），FS 后端返回 `/fs/{id}/upload` 由文件服务直写磁盘。超过一个分片的文件走分片上传，续传时以 `checksum` 与 `fileSize` 校验内容与上次一致。

## Consequences

上传不由服务端登记事务保护，客户端必须在内容传完后显式调用 `PUT /attachment/{id}/status/done` 把状态改为完成，否则附件停留在 `UPLOADING`。附件内容的完整性由客户端提供的 `checksum` 保证，服务端只在写入时复核。
