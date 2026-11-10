# Issue tracker: GitHub

本仓库的 Issue 与规格说明（spec / PRD）以 GitHub issue 形式存放，所有操作通过 `gh` CLI 完成。

## 约定

- **创建**：`gh issue create --title "..." --body "..."`。多行正文使用 heredoc。
- **读取**：`gh issue view <number> --json number,title,body,labels,comments`。
- **列出**：`gh issue list --state open --json number,title,body,labels,comments --jq '[.[] | {number, title, body, labels: [.labels[].name], comments: [.comments[].body]}]'`，按需追加 `--label`、`--state` 过滤。
- **把 issue 挂为子 issue**：`gh issue create --parent <parent> ...`，或事后 `gh issue edit <parent> --add-sub-issue <child>`（`gh` 2.94+）。老版本 `gh`：`gh api --method POST repos/<owner>/<repo>/issues/<parent>/sub_issues -F sub_issue_id=<child-db-id>`（数据库 id，见下方**阻塞**）。若 sub-issue 不可用，在子 issue 正文顶部写 `Part of #<parent>`。
- **阻塞**：优先使用 GitHub 原生 issue 依赖（UI 可见的规范表示）。`gh issue create --blocked-by <n>,<n> ...` 或 `gh issue edit <child> --add-blocked-by <blocker>`（`gh` 2.94+）。老版本：`gh api --method POST repos/<owner>/<repo>/issues/<child>/dependencies/blocked_by -F issue_id=<blocker-db-id>`，其中 `<blocker-db-id>` 是阻塞方的数字**数据库 id**（`gh api repos/<owner>/<repo>/issues/<n> --jq .id`，**不是** `#number` 或 `node_id`）。依赖功能不可用时退回在子 issue 正文顶部写 `Blocked by: #<n>, #<n>`。所有阻塞方均关闭时，工单视为解锁。
- **统计未关闭的阻塞方**：`gh api repos/<owner>/<repo>/issues/<n> --jq .issue_dependencies_summary.blocked_by`（仅未关闭项；为 0 表示可开工）。`gh issue view <n> --json blockedBy` 会把已关闭的也列出来，需过滤：`--jq '[.blockedBy.nodes[] | select(.state == "OPEN")] | length'`。
- **评论**：`gh issue comment <number> --body "..."`。
- **加 / 去标签**：`gh issue edit <number> --add-label "..."` / `--remove-label "..."`。
- **关闭**：先用 `gh issue comment <number> --body "..."` 说明原因，再 `gh issue close <number>`。对已关闭的 issue，`gh issue close --comment` 会丢弃评论但仍以 0 退出。

仓库由 `git remote -v` 推断；在仓库目录内运行时 `gh` 会自动处理。

只向本仓库（`xezzon/zeroweb-spring`）提交 issue，绝不提交到上游 `mattpocock/skills`。在 fork 中 `gh` 可能默认指向上游父仓库，因此每次会话首次 `gh issue create` 之前，先用 `gh repo view --json nameWithOwner` 确认目标仓库。

## PR 作为分诊入口

**PR 作为请求入口：否。** _（若本仓库把外部 PR 当作特性请求，改为 `是`；`/triage` 会读取此标记。）_

设为 `是` 时，PR 与 issue 走同一套标签与状态，改用 `gh pr` 等价命令：

- **读取 PR**：`gh pr view <number>`，然后 `gh pr view <number> --comments`（非 TTY 下 `--comments` 只打印评论、不含正文），diff 用 `gh pr diff <number>`。
- **列出待分诊的外部 PR**：`gh api --paginate 'repos/{owner}/{repo}/pulls?state=open' --jq '.[] | select(.author_association | IN("OWNER","MEMBER","COLLABORATOR") | not) | {number, title, author: .user.login, author_association, labels: [.labels[].name]}'`。
- **评论 / 标签 / 关闭**：`gh pr comment`、`gh pr edit --add-label`/`--remove-label`、`gh pr close`。

GitHub 的 issue 与 PR 共用一个编号空间，裸写的 `#42` 可能是其中之一：先用 `gh pr view 42` 解析，失败再退回 `gh issue view 42`。

## 当某个技能说"发布到 issue tracker"

创建一个 GitHub issue。

## 当某个技能说"拉取相关工单"

按上面的**读取**方式读取。

## Wayfinding 操作

供 `/wayfinder` 使用。**map** 是单个 issue，**工单**是其下的**子 issue**。

- **Map**：单个 issue，打 `wayfinder:map` 标签，正文存放 Notes / Decisions-so-far / Fog。`gh issue create --label wayfinder:map`。
- **子工单**：以 GitHub sub-issue 形式挂到 map 下（见**把 issue 挂为子 issue**）。sub-issue 未启用时，改为在 map 正文的任务列表中加入该子工单，并在子工单正文顶部写 `Part of #<map>`。标签：`wayfinder:<type>`（`research`/`prototype`/`grilling`/`task`）。一旦领取，工单 assign 给驱动的开发者。
- **阻塞**：见**约定**中的**阻塞**。
- **Frontier 查询**：列出 map 的未关闭子工单（`gh issue list --state open`，范围限定为该 map 的 sub-issue / 任务列表），剔除存在未关闭阻塞方（见**统计未关闭的阻塞方**，或 `Blocked by` 行中有未关闭 issue）或已有 assignee 的；按 map 中的顺序，第一个即为下一张工单。
- **领取（Claim）**：`gh issue edit <n> --add-assignee @me`，本会话的第一次写操作。
- **解决（Resolve）**：`gh issue comment <n> --body "<答案>"`，然后 `gh issue close <n>`，最后把上下文指针（要点 + 链接）追加到 map 的 Decisions-so-far。
