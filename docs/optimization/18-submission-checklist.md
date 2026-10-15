# Step 18 - Submission Checklist

## Problem

项目最初的需求包含“整理打包上交”，但后续明确不再生成 zip。项目中又包含模型、索引、前端依赖、构建产物、Docker 数据目录等大文件或可生成文件，如果没有明确清单，最终上交时容易出现两类问题：

- 把 `node_modules`、模型、索引、`target`、`dist` 等大文件一起提交，导致体积过大。
- 漏掉 README、Docker Compose、RAG 服务、前端源码或优化文档，导致老师无法复现。

## Change

新增提交验收清单：

```text
docs/SUBMISSION_CHECKLIST.md
```

文档内容包括：

- 应提交的源码和文档范围。
- 不应提交的大文件、缓存、构建产物和本地配置。
- 上交前验证命令。
- 老师查看项目的推荐顺序。
- RAG 模型与 FAISS 索引的说明模板。
- 最终提交前检查项。

同时在 README 和架构答辩文档中加入清单入口。

## Value

- 明确“不再生成 zip”的交付方式。
- 降低项目体积再次膨胀的风险。
- 让老师能按固定路径理解项目价值和复现方式。
- 把“上交”从临时操作变成可验证的交付流程。

## Verification

检查以下内容：

- `docs/SUBMISSION_CHECKLIST.md` 存在。
- README 链接到提交清单。
- `.gitignore` 已忽略模型、索引、依赖、构建产物、旧提交包和本地配置。
- 提交清单包含验证命令和老师查看路径。
