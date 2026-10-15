# RAG Service

This module exposes a lightweight Flask service for FAISS-based semantic news retrieval.

## What It Does

- Reads news titles from MySQL when no local FAISS cache exists.
- Encodes titles with `sentence-transformers/all-MiniLM-L6-v2` or a compatible local model.
- Stores `news_index.faiss` and `news_data.pkl` for fast startup.
- Provides `/rag` for semantic retrieval and `/health` for readiness checks.

## Run

```powershell
cd rag-service
python -m venv .venv
.venv\Scripts\activate
pip install -r requirements.txt
$env:RAG_MODEL_PATH="D:\models\all-MiniLM-L6-v2"
python app.py
```

Linux or WSL:

```bash
cd rag-service
python -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
export RAG_MODEL_PATH=/mnt/d/models/all-MiniLM-L6-v2
python app.py
```

## API

```text
GET /health
GET /rag?query=climate&top_k=5
```

The Spring Boot backend calls this service through `RAG_SERVICE_URL`.

## Docker

The image does not include model or FAISS cache files. Mount them as volumes:

```bash
docker build -t bigdata-news-rag ./rag-service
docker run --rm -p 5000:5000 \
  -e RAG_MODEL_PATH=/models/all-MiniLM-L6-v2 \
  -e RAG_INDEX_FILE=/data/news_index.faiss \
  -e RAG_DATA_FILE=/data/news_data.pkl \
  -v /path/to/all-MiniLM-L6-v2:/models/all-MiniLM-L6-v2:ro \
  -v /path/to/rag-data:/data \
  bigdata-news-rag
```
