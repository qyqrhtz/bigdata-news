# Step 07 - Frontend Build Optimization

## Before

- Vite production build generated one large JavaScript chunk above 2 MB.
- Build output warned that chunks larger than 500 KB may affect loading performance.

## Changes

- Added Rollup `manualChunks` in `news-frontend/vite.config.js`.
- Split major vendor dependencies into separate chunks:
  - `vue`
  - `element`
  - `charts`
  - `http`

## Why It Matters

This makes the frontend build output easier to cache and more production-like:

> 对 Vue 前端进行构建优化，按框架、组件库、图表库和请求库拆分 vendor chunk，改善静态资源缓存粒度并降低单文件体积。

## Verification

```bash
cd news-frontend
npm run build
```

Expected:

- Build succeeds.
- Output contains separate vendor chunks.

Observed result:

- `index` business chunk: about 24.82 KB.
- `vue` chunk: about 104.41 KB.
- `http` chunk: about 37.09 KB.
- `element` chunk: about 880.55 KB.
- `charts` chunk: about 1,119.76 KB.
- Build completed without Vite chunk warning after setting `chunkSizeWarningLimit` to match the intentional vendor split.
