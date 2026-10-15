# Optimization Roadmap

目标：把原本分散的课程项目整理成可以写进简历、可以答辩展示、可以复现运行的新闻大数据分析平台。

## Project Positioning

项目可以描述为：

> 基于 Spring Boot + Spark/Hadoop + Vue + FAISS 的新闻大数据分析与语义检索平台，支持新闻数据采集、HDFS 清洗、MySQL 持久化、统计分析可视化、向量检索和 RAG 问答。

## Optimization Steps

1. 配置与安全治理
   - 移除硬编码 token、数据库连接和 RAG 地址。
   - 使用环境变量和 `.env.example` 管理部署参数。

2. RAG 服务模块化
   - 将原本独立的 Python RAG 脚本整理为 `rag-service/`。
   - 增加 `requirements.txt`、`/health`、环境变量配置和运行说明。

3. Docker 化本地基础设施
   - 把 WSL2 中实际使用的 `docker-compose.yaml` 收敛到项目根目录。
   - 用变量化端口和密码支持本地/答辩环境切换。

4. 前后端联调规范
   - 前端 API 默认指向 `http://localhost:8081/api`。
   - 后端 RAG 默认指向 `http://127.0.0.1:5000`。
   - 前端已迁入 `news-frontend/`，并通过 `VITE_API_BASE_URL` 外置 API 地址。

5. 简历与答辩材料沉淀
   - 每次优化记录“问题、改法、价值、验证方式”。
   - 输出可直接复用的简历 bullet 和答辩讲解点。

6. API 契约治理
   - RAG 检索结果从裸 `Map/List` 改为 DTO。
   - 对空问题、`topK` 范围等输入增加控制器层校验。
   - 用全局异常处理统一错误响应结构。

7. 接口文档与 API 测试
   - 使用 Springdoc OpenAPI 生成 Swagger 文档。
   - 为核心新闻/RAG/分析接口补充说明。
   - 使用 MockMvc 验证文档端点和参数校验行为。

8. Spark 作业参数化
   - 新闻采集、HDFS 清洗、MySQL 写入和分析作业支持命令行参数。
   - 移除 Python 脚本中的硬编码密钥、路径和数据库配置。
   - 修复 processed 数据写 Parquet、分析脚本读 JSON 的链路不一致问题。

9. RAG 服务容器化
   - 为 Flask/FAISS RAG 服务增加 Dockerfile 和 Gunicorn WSGI 入口。
   - 通过 Compose profile 可选启动 RAG 微服务。
   - 模型与 FAISS 索引通过 volume 挂载，避免大文件进入镜像。

10. 一键验证脚本
   - 用 `scripts/verify.ps1` 串联后端测试、Python 语法检查、Compose 校验和前端构建。
   - 将提交前检查从零散命令整理成可复现的工程脚本。

11. 健康检查与诊断
   - 新增 `/api/health` 返回服务存活状态和非敏感依赖配置。
   - 用 MockMvc 覆盖健康检查接口，方便答辩演示和部署排查。

12. 调试接口治理
   - 删除默认暴露的根路径测试接口。
   - 删除 Coze 临时测试控制器，统一通过正式 RAG 问答链路验证 Coze 调用。
   - 用测试保证默认环境不暴露 `/api/test/coze/*`。

13. API 参考与契约测试
   - 新增 `docs/API_REFERENCE.md` 汇总正式后端接口。
   - 用测试覆盖 OpenAPI 标签、404 错误体和调试接口关闭状态。

14. 架构与答辩材料
   - 新增 `docs/ARCHITECTURE_AND_DEFENSE.md` 汇总系统架构、数据流和模块职责。
   - 整理演示顺序、答辩问答和可直接放进简历的项目描述。

15. 前端联调指南
   - 新增 `docs/FRONTEND_GUIDE.md` 汇总页面路由、API 映射和构建命令。
   - 说明 `VITE_API_BASE_URL` 如何管理前后端联调地址。

16. 上交验收清单
   - 新增 `docs/SUBMISSION_CHECKLIST.md` 明确提交范围和不应提交的大文件。
   - 整理上交前验证命令和老师查看项目的推荐路径。

17. 数据库结构可复现
   - 新增 `mysql/init/01-schema.sql` 为 MySQL 首次启动创建核心表。
   - 新增 `docs/DATABASE_SCHEMA.md` 说明表结构、Spark 写入路径和 RAG 读取路径。

18. CI 自动化验证
   - 新增 `.github/workflows/ci.yml` 覆盖后端、前端、Python 和 Docker Compose 检查。
   - 新增 `docs/CI.md` 说明 CI 步骤和本地等价命令。

19. 分页参数校验
   - 对新闻分页接口增加 `page >= 0` 和 `1 <= size <= 100` 的边界约束。
   - 用 MockMvc 覆盖非法分页参数的 400 错误响应。

20. 项目元信息打磨
   - 将 Maven 默认描述从模板文案改为正式项目定位。
   - 优化启动类注释和启动日志，展示 Health、Swagger 和 RAG 服务入口。

## Next High-Value Tasks

- 为前端补充构建截图、页面说明和接口联调截图。
