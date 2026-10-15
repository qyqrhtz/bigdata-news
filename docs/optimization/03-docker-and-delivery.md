# Step 03 - Docker and Delivery Baseline

## Before

- Docker Compose 文件只存在于 WSL2 的某个目录中，主项目没有部署入口。
- MySQL、Hadoop、Spark 的容器名依赖已经写进代码和 Spark 脚本，但没有集中说明。

## Changes

- 新增根目录 `docker-compose.yaml`。
- 包含以下服务：
  - MySQL 8.0
  - MongoDB 6.0
  - Redis 7.0
  - Hadoop namenode / datanode
  - Spark master / worker
- 端口和密码使用 `${VAR:-default}` 形式，方便按环境覆盖。
- `.gitignore` 排除本地 MySQL 数据目录和提交包产物。

## Why It Matters

这一步让项目具备“可复现部署”的基础。简历中可以表达为：

> 使用 Docker Compose 编排 MySQL、Hadoop 与 Spark 本地大数据环境，统一开发和演示部署流程，降低环境配置成本。

## Verification

```bash
docker compose config
docker compose up -d
docker ps
```

如果是离线答辩环境，再单独使用 `docker save` / `docker load` 迁移镜像。
