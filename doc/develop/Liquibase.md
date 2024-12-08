## Liquibase

版本演进时，如果有数据库表项或数据的调整，应该将对应的SQL语句写在 resource 文件夹的 db/changelog 目录的SQL文件中。

单元测试及线上环境会由[Liquibase](https://www.liquibase.com/)执行。

本项目遵循以下规则维护SQL文件：

- Liquibase 版本控制文件的格式是 SQL。
- 采用[共享的、面向对象的](https://docs.liquibase.com/start/design-liquibase-project.html)设计范式。即数据库的每一个表对应一个SQL文件。
- 每一段SQL之前应该有如下内容: `-- changeset ${contributor}:${issue} labels:${milestone}`。

以下是Liquibase的一个示例：

```postgres-sql
-- db/changelog/user.sql
-- changeset xezzon:10 labels:0.1
CREATE TABLE zeroweb_user (
  id VARCHAR(64) NOT NULL,
  username VARCHAR(255) NOT NULL,
  nickname VARCHAR(255),
  cipher VARCHAR(255) NOT NULL,
  create_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  update_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  CONSTRAINT pk_zeroweb_user PRIMARY KEY (id)
);
ALTER TABLE zeroweb_user ADD CONSTRAINT uc_zeroweb_user_username UNIQUE (username);

INSERT INTO zeroweb_user(id, username, nickname, cipher, create_time, update_time)
VALUES ('1', 'root', '超级管理员', '', '1970-01-01 08:00:00', '1970-01-01 08:00:00');
```
