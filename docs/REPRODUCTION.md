# Reproduction and Recovery

## Scope

This repository contains the Spring Boot backend, Vue frontend, Python data jobs,
RAG source, SQL initialization schema, tests, and deployment documentation.
It does not contain a complete ready-populated production service.
News datasets, model weights, database contents, indexes, local settings, old
submission packages, and installed dependencies must be restored or regenerated.

Prerequisites: Java 17, Node.js 22.12+ (tested with 22.14), Python, and Docker
Compose for infrastructure. Spark jobs require Spark/Hadoop and the documented
MySQL JDBC driver. The RAG Python dependencies are in rag-service/requirements.txt.

## Clean Checkout Validation

```powershell
git clone https://github.com/qyqrhtz/bigdata-news.git
cd bigdata-news
.\mvnw.cmd clean test
cd news-frontend
npm ci
npm run build
cd ..
docker compose config --quiet
docker compose --profile rag config --quiet
```

The backend tests use an isolated in-memory H2 database, not the real MySQL service.
Passing them and building the frontend does not validate a live Spark cluster,
external news API, Coze workflow, populated database, or end-to-end RAG search.

## Configuration and Model

Copy .env.example to .env for Compose interpolation. Java and Python processes
launched directly do not automatically read this file: export their required
environment variables in the launching shell. For example:

```powershell
$env:DB_PASSWORD = 'set-locally'
$env:RAG_MODEL_PATH = 'sentence-transformers/all-MiniLM-L6-v2'
```

The RAG default model is the Hugging Face model identifier above. First use requires
network access to download weights. For offline use, restore a complete model
directory and set RAG_MODEL_PATH to its absolute path. The Compose RAG profile
uses a separate read-only model-directory mount: download or restore the model to
RAG_MODEL_DIR first. It is intentionally not an incomplete Git submodule.

Initialize MySQL with mysql/init/01-schema.sql. This creates tables only; import
your preserved database dump or ingest news before expecting populated dashboards
and RAG results. Example database passwords are local-development placeholders.
Do not expose this development stack directly to the Internet.

## Local Archive

Before removing the old D: checkout, preserve a full E: copy with .git and all
ignored files, and verify source/copy SHA256. This is separate from the source-only
GitHub repository. Never assume git clone restores your course data or submissions.
Keep the archive and the previously exported MySQL database backups. To resume
work, copy the archived project to a new working directory, or clone GitHub and
restore only the needed private data. Do not run development services from your
only backup copy.
