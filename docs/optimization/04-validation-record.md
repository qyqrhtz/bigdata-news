# Step 04 - Validation Record

## Commands

```powershell
.\mvnw.cmd test
```

Result:

- Build success.
- Tests run: 1.
- Failures: 0.
- Errors: 0.
- Skipped: 0.

Note:

- Backend tests now use H2 in-memory database through `src/test/resources/application.properties`.
- The test no longer requires a running local MySQL server.
- DTO-based RAG contracts and global exception handler compile into the Spring context successfully.
- OpenAPI endpoint `/v3/api-docs` is covered by MockMvc.
- Blank RAG chat question and invalid `topK` are covered by MockMvc validation tests.

```powershell
docker compose config --quiet
```

Result:

- Docker Compose file parsed successfully.

```bash
wsl.exe -d Ubuntu-22.04 -- sh -lc "cd /mnt/d/bigdata-news && python3 -m py_compile rag-service/app.py"
```

Result:

- RAG service Python syntax check passed.

```bash
wsl.exe -d Ubuntu-22.04 -- sh -lc "cd /mnt/d/bigdata-news && python3 -m py_compile main.py news_to_parquet.py news_analysis.py test.py rag-service/app.py"
```

Result:

- News fetch script syntax check passed.
- Spark JSON-to-Parquet job syntax check passed.
- Spark analytics job syntax check passed.
- Spark MySQL load job syntax check passed.
- RAG service syntax check passed.

```powershell
cd news-frontend
npm run build
```

Result:

- Frontend production build passed.
- Business entry chunk is split from major vendor chunks.
- Chat page now uses the shared `newsApi` client instead of hard-coded Axios URL.

Latest backend result:

- Tests run: 4.
- Failures: 0.
- Errors: 0.
- Skipped: 0.

Latest full verification:

- `.\mvnw.cmd test`: PASS.
- `npm run build`: PASS.
- `python3 -m py_compile main.py news_to_parquet.py news_analysis.py test.py rag-service/app.py rag-service/wsgi.py`: PASS.
- `docker compose config --quiet`: PASS.
- `docker compose --profile rag config --quiet`: PASS.

One-click verification entry:

```powershell
powershell -ExecutionPolicy Bypass -File scripts/verify.ps1
```

This script runs the backend, Python, Docker Compose and frontend checks in one pass.
On this Windows/WSL setup, WSL syntax checks pass when run directly in PowerShell, but may report `WSL_E_DISTRO_NOT_FOUND` when launched from `.ps1` script mode. The verification script prints the direct command and continues unless `-StrictWsl` is provided.

Latest one-click verification:

- `powershell -ExecutionPolicy Bypass -File scripts/verify.ps1`: PASS.
- Backend tests: PASS, tests run: 4.
- Docker Compose config: PASS.
- Docker Compose config with `rag` profile: PASS.
- Frontend production build: PASS.
- WSL syntax check from script mode printed the manual command due local `WSL_E_DISTRO_NOT_FOUND`.

Latest direct WSL syntax verification:

```powershell
wsl.exe -d Ubuntu-22.04 -- sh -lc "python3 -m py_compile /mnt/d/bigdata-news/main.py /mnt/d/bigdata-news/news_to_parquet.py /mnt/d/bigdata-news/news_analysis.py /mnt/d/bigdata-news/test.py /mnt/d/bigdata-news/rag-service/app.py /mnt/d/bigdata-news/rag-service/wsgi.py"
```

Result:

- Python data scripts: PASS.
- RAG service scripts: PASS.

Health diagnostics verification:

```powershell
.\mvnw.cmd test
```

Result:

- Build success.
- Tests run: 5.
- Failures: 0.
- Errors: 0.
- Skipped: 0.
- `/api/health` returns `status=UP`, `service=bigdata-news`, and dependency configuration fields.

Debug endpoint governance verification:

```powershell
.\mvnw.cmd clean test
```

Result:

- Build success.
- Tests run: 6.
- Failures: 0.
- Errors: 0.
- Skipped: 0.
- Deleted `CozeTestController` is no longer present after a clean build.
- `/api/test/coze/simple` returns 404 in the default environment.
- Unknown API/static paths are handled as 404 instead of being wrapped as 500.

Note:

- `scripts/verify.ps1` now runs `mvnw.cmd clean test` so removed controllers cannot remain active through stale classes in `target/classes`.

API reference and contract verification:

```powershell
.\mvnw.cmd clean test
```

Result:

- Build success.
- Tests run: 6.
- Failures: 0.
- Errors: 0.
- Skipped: 0.

Covered behavior:

- OpenAPI document endpoint is available.
- OpenAPI contains `Health` and `Statistics APIs` tags.
- Removed Coze debug endpoint returns 404.
- 404 response body uses unified `ApiErrorResponse` fields.

Architecture and defense guide verification:

```powershell
Test-Path README.md
Test-Path docs\ARCHITECTURE_AND_DEFENSE.md
Test-Path docs\API_REFERENCE.md
Test-Path docs\optimization\16-architecture-and-defense-guide.md
Test-Path docs\optimization\resume-bullets.md
```

Result:

- Required documents exist.
- README links to API reference, architecture guide, roadmap and resume bullets.
- Architecture guide contains system diagram, demo order, defense talking points and final resume version.

Frontend guide verification:

```powershell
cd news-frontend
npm run build
```

Result:

- Vite production build passed.
- Modules transformed: 2261.
- Frontend documentation maps routes to backend APIs and records the `VITE_API_BASE_URL` configuration.

Submission checklist verification:

```powershell
Test-Path docs\SUBMISSION_CHECKLIST.md
Select-String -Path README.md -Pattern "SUBMISSION_CHECKLIST|Submission Checklist"
Select-String -Path .gitignore -Pattern "all-MiniLM-L6-v2|faiss|pkl|node_modules|dist|target|teacher-submit|\.env|rag-data|mysql/data"
```

Result:

- Submission checklist exists.
- README links to the checklist.
- `.gitignore` covers model files, FAISS indexes, frontend dependencies, build outputs, Maven target, local `.env`, Docker/MySQL data and old submission archives.
- Checklist includes do-not-submit rules, pre-submission commands, teacher review path and model/data note.

Database schema reproducibility verification:

```powershell
docker compose config --quiet
docker compose --profile rag config --quiet
Test-Path mysql\init\01-schema.sql
Test-Path docs\DATABASE_SCHEMA.md
```

Result:

- Docker Compose config passed.
- Docker Compose config with `rag` profile passed.
- MySQL initialization SQL exists.
- Database schema document exists.
- Compose mounts `./mysql/init:/docker-entrypoint-initdb.d:ro`.
- README links to the database schema document.

CI verification:

```powershell
docker compose config --quiet
docker compose --profile rag config --quiet
.\mvnw.cmd clean test
cd news-frontend
npm run build
```

Result:

- CI workflow file exists at `.github/workflows/ci.yml`.
- CI documentation exists at `docs/CI.md`.
- README links to CI documentation.
- Docker Compose config passed.
- Docker Compose config with `rag` profile passed.
- Backend clean test passed: tests run 6, failures 0, errors 0.
- Frontend production build passed: modules transformed 2261.

Pagination validation verification:

```powershell
.\mvnw.cmd clean test
```

Result:

- Build success.
- Tests run: 8.
- Failures: 0.
- Errors: 0.
- Skipped: 0.
- `/api/news/page?page=-1&size=10` returns 400.
- `/api/news/page?page=0&size=500` returns 400.
- API Reference documents `page >= 0` and `1 <= size <= 100`.

Project metadata polish verification:

```powershell
.\mvnw.cmd clean test
```

Result:

- Build success.
- Maven now reports `Building Bigdata News Analytics Platform 0.0.1-SNAPSHOT`.
- Tests run: 8.
- Failures: 0.
- Errors: 0.
- Skipped: 0.

RAG containerization verification:

- `rag-service/app.py` and `rag-service/wsgi.py` pass Python syntax compilation.
- Compose config passes both with and without the optional `rag` profile.
- Full RAG image build is intentionally left as a separate runtime step because it downloads large ML dependencies and requires model/index volume paths.

## Fixed During Validation

- `mvnw.cmd` failed on a normal `.m2` directory because the wrapper script indexed an empty `Target` array.
- Updated the wrapper script to check whether `Target` exists before reading `Target[0]`.
- Removed deprecated explicit Hibernate dialect configuration and disabled Open Session in View.
- Added H2 test isolation after detecting that local MySQL credentials made tests machine-dependent.

## Remaining Risks

- Current automated backend test is only a Spring context smoke test. It does not verify API behavior, Spark jobs, or RAG retrieval quality.
- `NewsServiceImpl.semanticSearch` still uses raw `List.class` deserialization, which compiles with an unchecked warning. A future DTO would make this cleaner.
- npm reports dependency vulnerabilities in the current frontend dependency tree. Avoid running `npm audit fix` blindly; evaluate version upgrades in a separate step.
