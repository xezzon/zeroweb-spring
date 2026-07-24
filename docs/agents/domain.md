# 领域文档（Domain Docs）

工程类技能在探索本仓库时，应如何消费领域文档。

## 进入探索前，请先阅读以下文件

- 仓库根目录的 **`CONTEXT.md`**，或
- 若根目录存在 **`CONTEXT-MAP.md`**（指向各上下文的 `CONTEXT.md`），请按需阅读相关的上下文文件
- **`docs/adr/`** —— 阅读与即将工作的领域相关的 ADR。在多上下文仓库中，也请检查 `src/<context>/docs/adr/` 以获取上下文级别的决策记录

若上述任一文件尚不存在，**请照常进行探索，不必特别提示**：无需说明这些文件的缺失，也不要主动建议创建。`/domain-modeling` 技能（通过 `/grill-with-docs` 和 `/improve-codebase-architecture` 触发）会在术语或决策真正被落实时按需创建它们。

## 文件结构

单上下文仓库（绝大多数项目）：

```
/
├── CONTEXT.md
├── docs/adr/
│   ├── 0001-event-sourced-orders.md
│   └── 0002-postgres-for-write-model.md
└── src/
```

多上下文仓库（根目录存在 `CONTEXT-MAP.md`）：

```
/
├── CONTEXT-MAP.md
├── docs/adr/                          ← 系统级决策
└── src/
    ├── ordering/
    │   ├── CONTEXT.md
    │   └── docs/adr/                  ← 上下文专属决策
    └── billing/
        ├── CONTEXT.md
        └── docs/adr/
```

## 使用术语表的词汇

当你在输出中提及某个领域概念（issue 标题、重构建议、假设、测试名称等）时，请使用 `CONTEXT.md` 中已经定义过的术语，避免使用术语表明确排除的同义词。

若你所需的概念尚未被术语表收录，这本身就是一个信号：要么你在发明本项目并不使用的语言（请重新考虑），要么确实存在缺口（请记录下来，留给 `/domain-modeling` 处理）。

## 标注 ADR 冲突

若你的输出与已有 ADR 产生冲突，请明确指出，而不是默默覆盖：

> _与 ADR-0007（event-sourced orders）相矛盾 —— 但仍值得重新审视，因为……_
