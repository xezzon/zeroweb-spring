# AGENTS.md

## Agent skills

### Issue tracker

本地 markdown 工单存放在 `.scratch/<feature>/` 下。详见 `docs/agents/issue-tracker.md`。

### Triage labels

默认五项标签：`needs-triage`、`needs-info`、`ready-for-agent`、`ready-for-human`、`wontfix`。详见 `docs/agents/triage-labels.md`。

### Domain docs

单上下文布局：根目录的 `CONTEXT.md` 与 `docs/adr/`。详见 `docs/agents/domain.md`。

## 注释规范

### 应该写怎样的注释？

#### 文档注释

文档注释包括（对类、方法、字段的）描述、标签（`@param`、`@return`、`@throws`等）。

优先使用 [Markdown 文档注释](https://docs.oracle.com/en/java/javase/23/javadoc/using-markdown-documentation-comments.html)，这是以三个斜杆作为前导符号的注释方式。以下是一个例子：

```java
/**
 * 这是一个传统 Javadoc 的示例
 * @author xezzon 
 */
public class Example {
  
  /// 这是一个 Markdown document comment 的示例
  String id;
}

/// Example 管理
/// @author xezzon
@RestController
public class ExampleHttpEndpoint {
  
}

/// 这是一个对 [示例][Example] 进行数据库操作的 JPA 接口
@Repository
public interface ExampleRepository {
  
}
```

#### 多行注释

```java
/// This is a Markdown document comment.
public class HelleWorld {
  static void main(String... args) {
    /*
     * This is a multi-line comment.
     */
  }
}
```

#### 单行注释

```java
/// This is a Markdown document comment.
public class HelleWorld {
  static void main(String... args) {
     // This is a multi-line comment.
  }
}
```

### 哪些地方需要写注释？

通过代码无法表达的信息，都可以通过注释的方式表达。

### 应该写哪些注释？

- **意图注释**: 解释为什么要写这样一段代码，常见于分支逻辑处。
- **警戒注释**: 说明代码的副作用。
- **逻辑分割注释**: 将代码内容分割为若干块，在每一块前使用注释进行总结。
- **用法注释**: SDK 对外暴露的接口提供 example。
- **说明性注释**: 说明代码无法体现的内容。比如字段的单位。 
