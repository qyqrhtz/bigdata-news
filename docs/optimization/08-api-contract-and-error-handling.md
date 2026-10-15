# Step 08 - API Contract and Error Handling

## Before

- RAG search returned `List<Map<String, Object>>`.
- RAG chat accepted a generic `Map<String, String>`.
- Empty questions and invalid `topK` values were not validated at the controller boundary.
- Errors returned by Spring MVC could have inconsistent shapes.

## Changes

- Added DTO records:
  - `RagSearchResult`
  - `RagChatRequest`
  - `RagChatResponse`
  - `ApiErrorResponse`
- Replaced raw RAG result maps with `List<RagSearchResult>`.
- Used `RestTemplate.exchange` with `ParameterizedTypeReference<List<RagSearchResult>>` to remove unchecked raw-list deserialization.
- Added `@NotBlank` validation for chat questions.
- Added `@Min(1)` and `@Max(20)` validation for `topK`.
- Added `GlobalExceptionHandler` to normalize validation and server error responses.

## Why It Matters

This turns the backend API from a loosely typed course-project style into a clearer service contract:

> 重构 RAG 检索接口契约，使用 DTO 和参数校验替代裸 `Map/List`，并增加全局异常处理，提升接口可维护性和前后端协作稳定性。

## API Examples

Semantic search:

```text
GET /api/news/search?query=climate&topK=5
```

Response:

```json
[
  {
    "rank": 1,
    "uniquekey": "xxx",
    "title": "News title",
    "url": "https://example.com/news",
    "score": 0.42
  }
]
```

RAG chat:

```text
POST /api/news/rag/chat
Content-Type: application/json

{
  "question": "What are the latest climate topics?"
}
```

Validation error:

```json
{
  "timestamp": "2026-06-17T23:20:00",
  "status": 400,
  "error": "Bad Request",
  "message": "question: question must not be blank",
  "path": "/api/news/rag/chat"
}
```

## Verification

```powershell
.\mvnw.cmd test
```

Expected:

- Backend compiles with DTO-based RAG service.
- Spring context test passes using H2.
