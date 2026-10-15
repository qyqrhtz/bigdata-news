# Step 12 - One-Click Verification

## Problem

项目已经包含后端、前端、Spark 脚本、RAG 服务和 Docker Compose。若验证命令分散在聊天记录或个人记忆里，换电脑、答辩或交给老师时很容易漏掉某一环。

## Change

新增 `scripts/verify.ps1`，把提交前常用检查收敛成一个 PowerShell 脚本：

- 运行 Spring Boot 测试。
- 在 WSL2 中检查 Python 数据脚本和 RAG 服务语法。
- 校验 Docker Compose 配置。
- 校验带 `rag` profile 的 Docker Compose 配置。
- 构建 Vue 前端生产包。

脚本支持：

- `-WslDistro`：指定 WSL2 发行版，默认 `Ubuntu-22.04`。
- `-SkipFrontend`：跳过前端构建。
- `-SkipFrontendInstall`：前端缺少 `node_modules` 时不自动执行 `npm ci`。
- `-StrictWsl`：WSL 语法检查失败时让脚本直接失败。

## Value

- 将“能不能跑”从手工经验变成可执行脚本。
- 降低上交、答辩、迁移环境时的复现成本。
- 让项目更像工程交付，而不是只包含源码的课程作业。

## Verification

```powershell
powershell -ExecutionPolicy Bypass -File scripts/verify.ps1
```

预期结果：

- 后端测试通过。
- Python 脚本语法检查通过。
- Docker Compose 配置校验通过。
- 前端生产构建通过。

如果当前 Windows/WSL 环境无法从 `.ps1` 脚本中启动指定发行版，脚本会打印一条可直接复制运行的 WSL 检查命令，并继续执行后续检查。需要强制失败时使用 `-StrictWsl`。
