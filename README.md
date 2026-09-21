# Pavi AI — Android Chatbot

Native Android Java chatbot with a Spring Boot Java backend, PostgreSQL persistence, and the OpenAI Responses API.

## Architecture
Android Java app → Spring Boot API → PostgreSQL + OpenAI

The OpenAI API key stays on the backend and must never be embedded in the APK.

## Run backend locally
Requirements: Java 21, Maven 3.9+, Docker.

1. Copy `.env.example` to `.env` and set `OPENAI_API_KEY`.
2. Run `docker compose up --build`.
3. Health check: `http://localhost:8080/api/health`.

## Android
Open the `android/` folder in Android Studio. The debug build uses `http://10.0.2.2:8080/` for the Android emulator. For a physical device, point `API_BASE_URL` at your computer's LAN address. For production, use an HTTPS backend URL.

## Security
Never commit `.env` or API keys. Production CORS should be restricted to known origins. Use HTTPS for release builds.
