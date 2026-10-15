# Step 01 - Configuration and Secret Management

## Before

- `application.properties` 中直接写入数据库用户名、密码和 Coze token。
- 后端调用 RAG 服务时硬编码 `http://127.0.0.1:5000`。
- JPA SQL 日志默认打开，答辩或演示时容易输出过多数据库细节。

## Changes

- 使用环境变量外置数据库配置：
  - `DB_URL`
  - `DB_USERNAME`
  - `DB_PASSWORD`
- 使用环境变量外置 Coze 配置：
  - `COZE_TOKEN`
  - `COZE_WORKFLOW_ID`
- 使用 `RAG_SERVICE_URL` 配置 RAG 服务地址。
- 增加 `.env.example`，给出可复现的本地运行模板。
- Coze 未配置时返回清晰提示，不再直接用空 token 调外部接口。

## Why It Matters

这一步把项目从“只能在自己电脑上跑”改成“可以在不同机器、不同环境部署”。简历中可以表达为：

> 对后端配置进行环境变量化改造，移除硬编码密钥与服务地址，提升项目部署安全性和环境迁移能力。

## Verification

- `src/main/resources/application.properties` 不再包含真实 Coze token。
- 后端默认仍可连接本地 MySQL 和本地 RAG 服务。
- 未配置 Coze 时，RAG chat 接口会返回配置提示，而不是抛出认证错误。
