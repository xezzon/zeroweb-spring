# 开发者手册

## 项目结构

## 本地运行

## 工程规范

Project：需求、Bug 管理的各种视图。每一个功能对应一个 Project。可关联多个仓库。

Milestone：构件的版本号。按语义化版本，取`主版本号.次版本号`，修订版本号不参与命名。

Issue：需求或线上Bug。

Discussion：使用过程中的疑问或对新功能的可行性讨论。

### 提需求

需求包括新功能、原有功能的新需求、原有需求变更等。

角色：所有人

视图：[New issue](https://github.com/xezzon/geom-spring-boot/issues/new?assignees=&labels=feature&projects=&template=feature_request.md)

操作：撰写系统需求规格说明书。

### 提Bug

角色：所有人

视图：[New issue](https://github.com/xezzon/geom-spring-boot/issues/new?assignees=&labels=bug&projects=&template=bug_report.yml)

操作：填写运行环境、复现步骤等表单项。

### issue评审

角色：需求管理员

视图：[Issues](https://github.com/xezzon/geom-spring-boot/issues)

#### 评审通过

关联Milestone，关联Project，选择优先级。
完善需求规格说明书，将其中的术语定义与概览图复制到Project的README。

#### 驳回

选择标签`duplicate`/`invalid`/`wontfix`。 关闭issue `Close as not planned`。

### 任务分配

角色：软件开发人员

视图：[Projects](https://github.com/xezzon/geom-spring-boot/projects)/[Project]/Overview

操作：分配人员Assignees。将状态改为`In Progress`。

### Pull Request

角色：软件开发人员

视图：[New pull request](https://github.com/xezzon/geom-spring-boot/pulls)

操作：完成 [PR Checklist](.github/pull_request_template.md)。

### 代码评审

角色：所有人

视图：[Projects](https://github.com/xezzon/geom-spring-boot/projects)/[Project]/Overview

操作：评审代码。合并代码。

### 软件发布

角色：项目管理员

视图：[Create a new release](https://github.com/xezzon/geom-spring-boot/releases/new)

操作：新建标签，标签命名规则遵循语义化版本。
`Generate release note`，生成`feature`、`bug`标签的PR的更新日志。如果有其他说明如破坏性变更，则将其完善。
Target选择`main`，`Publish release`。
归档Project中非`feature`标签的issue。

## 设计规范

## 代码规范

## 配置规范
