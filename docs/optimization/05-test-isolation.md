# Step 05 - Test Isolation

## Before

- `mvn test` tried to connect to the developer machine's local MySQL instance.
- If the local MySQL password or service state was different, the smoke test could fail even when the Java code compiled.

## Changes

- Added H2 as a test-scoped dependency.
- Added `src/test/resources/application.properties`.
- Test profile now uses an in-memory H2 database in MySQL compatibility mode.
- Coze and RAG configuration are empty/defaulted during tests, avoiding external service requirements.

## Why It Matters

This makes the project easier to verify on another machine and is a meaningful engineering improvement for a resume project:

> 为后端测试环境增加 H2 内存数据库隔离，解除单元测试对本机 MySQL 和外部服务的依赖，提升项目可复现性。

## Verification

```powershell
.\mvnw.cmd test
```

Expected:

- Build success.
- Spring context smoke test passes without requiring a running MySQL server.
