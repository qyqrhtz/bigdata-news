# Step 20 - CI Verification

## Problem

项目已经有本地验证脚本 `scripts/verify.ps1`，但本地脚本依赖当前机器环境。对于一个多模块项目，仅靠手工验证仍然有几个风险：

- 后端、前端、Python 脚本和 Docker Compose 可能只验证了其中一部分。
- 删除旧控制器后，如果没有 clean build，可能出现 stale class 残留。
- 迁移到 GitHub 或交给别人维护时，没有自动回归入口。

## Change

新增 GitHub Actions 工作流：

```text
.github/workflows/ci.yml
```

CI 在 `push` 和 `pull_request` 时运行，包含：

- Java 17 后端 `./mvnw clean test`。
- Python 脚本 `py_compile`。
- `docker compose config --quiet`。
- `docker compose --profile rag config --quiet`。
- Node.js 22 前端 `npm ci` 和 `npm run build`。

新增 CI 说明文档：

```text
docs/CI.md
```

## Value

- 将本地验证升级为可持续集成流程。
- 覆盖后端、前端、Python 数据脚本和 Docker 编排。
- 提升项目作为简历作品时的工程化可信度。
- 让后续维护者知道每次修改后应该通过哪些检查。

## Verification

本地等价验证：

```powershell
.\mvnw.cmd clean test
docker compose config --quiet
docker compose --profile rag config --quiet
cd news-frontend
npm run build
```

同时检查：

- `.github/workflows/ci.yml` 存在。
- `docs/CI.md` 存在。
- README 已链接 CI 文档。
