# Dementia Memory Assistant

## Backend

Requires Python 3.14 or another currently supported Python 3 release.

```bash
cd backend
python -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
uvicorn main:app --reload
```

Set `MONGODB_URI` and `GROQ_API_KEY` in `backend/.env`. On first startup,
OpenCV downloads the YuNet detector and SFace recognition models into
`backend/models/`. Enrollments created with the previous DeepFace backend must
be enrolled again because the embedding formats are incompatible.

## Frontend

```bash
cd frontend
npm install
npm run dev
```

Open `http://localhost:3000`. Camera access requires localhost or HTTPS.
