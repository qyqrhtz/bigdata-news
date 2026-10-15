import os
import pickle
from pathlib import Path
from threading import Lock

import faiss
import pandas as pd
import torch
from flask import Flask, jsonify, request
from sentence_transformers import SentenceTransformer
from sqlalchemy import create_engine


MODEL_PATH = os.getenv("RAG_MODEL_PATH", "sentence-transformers/all-MiniLM-L6-v2")
DB_URL = os.getenv("RAG_DB_URL", "mysql+pymysql://root:123456@localhost/mydb")
INDEX_FILE = Path(os.getenv("RAG_INDEX_FILE", "news_index.faiss"))
DATA_FILE = Path(os.getenv("RAG_DATA_FILE", "news_data.pkl"))
BATCH_SIZE = int(os.getenv("RAG_BATCH_SIZE", "32"))

app = Flask(__name__)
index = None
news_metadata = []
model = None
runtime_lock = Lock()


def build_vector_index():
    print("Loading news data from MySQL...")
    engine = create_engine(DB_URL)
    df = pd.read_sql("SELECT uniquekey, title, url FROM news WHERE title IS NOT NULL", engine)

    titles = df["title"].fillna("").tolist()
    metadata = df.to_dict("records")

    print(f"Loading sentence-transformer model: {MODEL_PATH}")
    device = "cuda" if torch.cuda.is_available() else "cpu"
    local_model = SentenceTransformer(MODEL_PATH, device=device)

    print(f"Encoding {len(titles)} titles on {device}...")
    title_vectors = local_model.encode(
        titles,
        batch_size=BATCH_SIZE,
        show_progress_bar=True,
        convert_to_numpy=True,
    )

    vector_index = faiss.IndexFlatL2(title_vectors.shape[1])
    vector_index.add(title_vectors)

    INDEX_FILE.parent.mkdir(parents=True, exist_ok=True)
    DATA_FILE.parent.mkdir(parents=True, exist_ok=True)
    faiss.write_index(vector_index, str(INDEX_FILE))
    with DATA_FILE.open("wb") as f:
        pickle.dump(metadata, f)

    if device == "cuda":
        torch.cuda.empty_cache()

    return vector_index, metadata


def load_runtime():
    global index, news_metadata, model
    if index is not None and model is not None:
        return

    with runtime_lock:
        if index is not None and model is not None:
            return

        if INDEX_FILE.exists() and DATA_FILE.exists():
            print("Loading local FAISS index and metadata...")
            index = faiss.read_index(str(INDEX_FILE))
            with DATA_FILE.open("rb") as f:
                news_metadata = pickle.load(f)
        else:
            print("FAISS cache not found. Building vector index from MySQL...")
            index, news_metadata = build_vector_index()

        model = SentenceTransformer(MODEL_PATH, device="cpu")


@app.get("/health")
def health():
    load_runtime()
    return jsonify({
        "status": "ok",
        "index_size": 0 if index is None else index.ntotal,
        "metadata_size": len(news_metadata),
        "model_path": MODEL_PATH,
    })


@app.get("/rag")
def rag_search():
    load_runtime()
    query = request.args.get("query", "").strip()
    top_k = min(max(int(request.args.get("top_k", 5)), 1), 20)

    if not query:
        return jsonify({"error": "query parameter is required"}), 400
    if index is None or model is None:
        return jsonify({"error": "RAG service is not ready"}), 503

    query_vector = model.encode([query], convert_to_numpy=True)
    distances, indices = index.search(query_vector, top_k)

    results = []
    for rank, idx in enumerate(indices[0], start=1):
        if 0 <= idx < len(news_metadata):
            item = news_metadata[idx]
            results.append({
                "rank": rank,
                "uniquekey": item.get("uniquekey"),
                "title": item.get("title"),
                "url": item.get("url"),
                "score": float(distances[0][rank - 1]),
            })

    return jsonify(results)


if __name__ == "__main__":
    load_runtime()
    app.run(
        host=os.getenv("RAG_HOST", "0.0.0.0"),
        port=int(os.getenv("RAG_PORT", "5000")),
        debug=False,
    )
