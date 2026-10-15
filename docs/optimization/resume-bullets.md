# Resume Bullets

可以按真实完成程度选择使用：

- 设计并实现新闻大数据分析平台，基于 Spring Boot + MySQL 提供新闻分页、统计分析、RAG 检索等 REST API，支撑 Vue 前端可视化展示。
- 打磨 Maven 项目元信息、启动日志和文档入口，使项目定位、运行入口和交付材料保持一致。
- 使用 Vue 3 + Vite + Element Plus + ECharts 构建新闻可视化前端，封装统一 Axios 客户端，通过环境变量管理后端地址并支撑多页面联调。
- 使用 Spark/Hadoop 完成新闻 JSON 数据清洗、去重、Parquet 存储与 MySQL 写入，构建从数据采集到分析展示的端到端处理链路。
- 设计 MySQL 初始化建表脚本和数据库说明文档，明确新闻主表、分析缓存表、Spark 写入路径和 RAG 读取路径，提升环境复现性。
- 基于 SentenceTransformer + FAISS 构建新闻标题向量索引，将语义检索封装为 Flask 微服务，并接入 Spring Boot 实现 RAG 问答数据源召回。
- 对项目进行工程化优化，移除硬编码密钥和服务地址，使用环境变量、`.env.example` 与 Docker Compose 提升部署安全性和可复现性。
- 重构后端 RAG API 契约，使用 Java record DTO、参数校验和全局异常处理替代裸 `Map/List`，提升接口可维护性与前后端协作稳定性。
- 为新闻分页、RAG 检索和 RAG 问答接口补充参数边界校验与统一错误响应，降低异常请求对服务稳定性的影响。
- 集成 Springdoc OpenAPI 自动生成 Swagger 接口文档，并通过 MockMvc 覆盖 RAG 参数校验和文档端点，增强项目可演示性和测试可信度。
- 编写 API Reference 并补充契约测试，覆盖接口分组、统一错误响应和未知路径 404 行为，提升前后端联调规范性。
- 新增服务健康检查与依赖诊断接口，暴露应用存活状态、RAG 地址和 Coze 配置状态，提升答辩演示与部署排查效率。
- 治理调试接口暴露范围，移除临时根路径和 Coze 测试控制器，使默认环境只暴露正式业务 API。
- 将新闻采集与 Spark 清洗分析脚本参数化，移除硬编码密钥、HDFS 路径和 MySQL 配置，并修复 Parquet/JSON 处理链路不一致问题。
- 将 Flask/FAISS RAG 服务容器化，使用 Gunicorn + Docker Compose profile 支持可选部署，并通过 volume 挂载模型和索引文件避免镜像膨胀。
- 编写一键验证脚本，串联后端测试、Python 语法检查、Docker Compose 校验和前端构建，提升项目交付与环境迁移的可复现性。
- 配置 GitHub Actions CI，自动执行后端 clean test、Python 脚本语法检查、Docker Compose 配置校验和前端生产构建，提升多模块回归验证能力。
- 沉淀系统架构、数据流、模块职责、演示顺序和答辩问答文档，将技术实现转化为可复用的项目讲解材料。
- 整理提交验收清单，明确源码、文档、Docker 配置、RAG 服务和前端项目的交付范围，并排除模型、索引和构建产物等大文件。
- 编写优化过程文档，沉淀配置治理、RAG 模块化、Docker 编排等改造步骤，形成可用于答辩和简历展示的项目材料。
