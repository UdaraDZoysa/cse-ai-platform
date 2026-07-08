# CSE AI Platform

Event-driven microservices platform that turns live Colombo Stock Exchange (CSE) price data into AI-generated investment insights, delivered as Telegram alerts.

```
CSE API → market-data → analysis → strategy  ─┐
                                              ├→ investment-intelligence → notification → Telegram
   news/announcements → market-intelligence  ─┘
                                                          ↑
                                    frontend (Next.js) → bff
```

## How it works

1. **market-data-service** polls the CSE API every 8s, filters to a user watchlist, and publishes only *changed* ticks/snapshots to Kafka.
2. **analysis-service** maintains a rolling window per symbol and computes statistical features (trend, momentum, volatility, moving averages).
3. **strategy-service** normalizes features against per-symbol statistics (z-scores), detects the market regime, fuses four weighted detector signals into a confidence score, and tracks an opportunity lifecycle.
4. **market-intelligence-service** ingests CSE announcements and web narratives (Exa search), then synthesizes scored market insights with an LLM (Groq).
5. **investment-intelligence-service** materializes all events into local history, and every 15 minutes builds a prioritized, bounded context per symbol, runs it through a DB-backed AI reasoning job queue (Groq), and publishes validated investment insights.
6. **notification-service** diffs each insight against the last known state and sends priority-scored Telegram alerts, only when something actually changed.
7. **bff-service** aggregates read APIs for the **Next.js frontend**.

## Tech stack

Java 17 / Spring Boot, Apache Kafka, Spring Data JPA, Groq (LLM), Exa (web search), Telegram Bot API, Next.js + TypeScript. Shared `contracts` module for events, DTOs, and topic definitions.

## Reliability patterns

Transactional outbox & inbox, idempotent consumers, dead-letter topics with retry/backoff + jitter, `FOR UPDATE SKIP LOCKED` job queues, rate-limit circuit breaker around LLM calls, warm-up gates before emitting signals.

## Running locally

```bash
# 1. Start Kafka
cd infra && docker compose up -d

# 2. Start each service (separate terminals)
cd services/<service-name> && ./mvnw spring-boot:run

# 3. Start the frontend
cd frontend && npm install && npm run dev   # http://localhost:3000
```

Required configuration (application properties / env): Groq API key, Exa API key, Telegram bot token + chat IDs.

Set a watchlist to start the pipeline:

```bash
curl -X POST http://localhost:8080/api/watchlist \
  -H "Content-Type: application/json" \
  -d '["LOLC.N0000","SAMP.N0000"]'
```

## Project structure

```
contracts/   shared events, DTOs, Kafka topics
services/    7 Spring Boot microservices
frontend/    Next.js app
infra/       docker-compose (Kafka)
```
