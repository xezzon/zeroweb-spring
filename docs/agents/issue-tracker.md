# Issue tracker: 本地 Markdown

本仓库的 Issue 与规格说明（spec / PRD）以 Markdown 文件形式存放在 `.scratch/` 目录下。

## 约定

- 一次特性对应一个目录：`.scratch/<feature-slug>/`
- 规格说明文件为 `.scratch/<feature-slug>/spec.md`
- 实现层面的工单按"一单一文件"组织，路径为 `.scratch/<feature-slug>/issues/<NN>-<slug>.md`，编号从 `01` 起递增；禁止把所有工单合并到一个文件
- 分诊状态以 `Status:` 一行的形式记录在工单文件顶部（角色字符串见 `triage-labels.md`）
- 后续讨论与评论追加到文件末尾的 `## Comments` 小节

## 当某个技能说"发布到 issue tracker"

在 `.scratch/<feature-slug>/` 下新建文件（如目录不存在则一并创建）。

## 当某个技能说"拉取相关工单"

直接读取指定路径对应的文件。用户通常会直接给出文件路径或工单编号。

## Wayfinding 操作

供 `/wayfinder` 使用。**map** 是一个汇总文件，每张**工单**是一个独立文件。

- **Map**：`.scratch/<effort>/map.md` —— 存放 Notes / Decisions-so-far / Fog 等内容
- **子工单**：`.scratch/<effort>/issues/NN-<slug>.md`，编号从 `01` 起递增，正文是待解答的问题。`Type:` 行记录工单类型（`research` / `prototype` / `grilling` / `task`）；`Status:` 行记录 `claimed` / `resolved`
- **阻塞关系**：在文件顶部以 `Blocked by: NN, NN` 一行声明。当所列编号对应的工单均处于 `resolved` 时，本工单方可解锁
- **Frontier（当前可执行集合）**：扫描 `.scratch/<effort>/issues/`，筛选出"打开、未阻塞、无人领取"的文件；按编号升序，首个即为下一张工单
- **领取（Claim）**：先把 `Status: claimed` 设置并保存，再开始任何工作
- **解决（Resolve）**：在 `## Answer` 小节下追加答案，将 `Status` 置为 `resolved`，再将上下文指针追加到 `map.md` 的 Decisions-so-far
