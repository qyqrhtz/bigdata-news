# Step 06 - Frontend Integration

## Before

- Vue frontend lived outside the main project at `C:\Users\lenovo\news-frontend`.
- API base URL was hard-coded as `http://localhost:8081/api`.
- The backend repository did not contain the UI source, making the project harder to present as an integrated full-stack system.

## Changes

- Copied frontend source into `news-frontend/`.
- Excluded local-only folders:
  - `node_modules/`
  - `dist/`
  - `.vscode/`
  - `.trae/`
- Added `news-frontend/.env.example`.
- Updated Axios base URL to use `VITE_API_BASE_URL`.

## Why It Matters

The project can now be presented as a full-stack system instead of separate scattered folders:

> 整合 Vue 3 前端子项目，并通过 `VITE_API_BASE_URL` 外置后端接口地址，支持本地开发、答辩演示和后续部署环境切换。

## Verification

```bash
cd news-frontend
npm install
npm run dev
```

Expected:

- Vite starts on port `8085`.
- Frontend requests backend APIs through `VITE_API_BASE_URL`.
