# Step 21 - Pagination Validation

## Problem

新闻分页接口原本只提供默认值：

```text
GET /api/news/page?page=0&size=10
```

但没有限制参数范围。调用方可以传入负数页码或非常大的 `size`，这会带来几个问题：

- 负数页码会造成不稳定或难理解的分页行为。
- 超大 `size` 可能导致一次查询返回过多数据，影响接口性能。
- API Reference 中没有明确分页边界，前端联调时容易误用。

## Change

在 `NewsController#getNewsPage` 中增加控制器层校验：

- `page >= 0`
- `1 <= size <= 100`

并补充 OpenAPI 参数说明：

- `page`：zero-based page index。
- `size`：page size, limited to 1-100。

新增 MockMvc 测试：

- `page=-1` 返回 HTTP 400。
- `size=500` 返回 HTTP 400。

同步更新：

- `docs/API_REFERENCE.md`

## Value

- 防止分页接口被异常参数拖垮。
- 让前端和后端对分页边界有一致约定。
- API 参数校验从 RAG 扩展到新闻列表，接口契约更完整。

## Verification

```powershell
.\mvnw.cmd clean test
```

预期结果：

- 后端测试通过。
- 分页参数非法时返回统一 `ApiErrorResponse`，状态码为 400。
