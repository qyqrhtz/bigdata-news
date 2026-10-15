# Step 09 - OpenAPI Documentation and API Tests

## Before

- Backend APIs had no generated interface documentation.
- RAG validation rules were documented in code but not covered by tests.
- The project could pass a context smoke test without proving that API validation and documentation endpoints worked.

## Changes

- Added `springdoc-openapi-starter-webmvc-ui`.
- Added `OpenApiConfig` with project title, version and description.
- Added Swagger annotations to core news and analytics APIs.
- Added schema annotations to RAG and error DTOs.
- Added MockMvc tests for:
  - `/v3/api-docs`
  - blank RAG chat question
  - invalid `topK`

## Why It Matters

This makes the backend easier to present, test and collaborate on:

> 集成 Springdoc OpenAPI 自动生成接口文档，并使用 MockMvc 覆盖 RAG 参数校验和文档端点，提升项目可演示性和接口质量保障。

## How to View

After starting the backend:

```text
http://localhost:8081/swagger-ui.html
http://localhost:8081/v3/api-docs
```

## Verification

```powershell
.\mvnw.cmd test
```

Expected:

- Context loads.
- OpenAPI JSON is available.
- Invalid RAG requests return unified 400 responses.
