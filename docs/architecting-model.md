# Model architektoniczny

## 1. Styl architektoniczny

Projekt opiera się na połączeniu czterech wzorców:

- **Modular Monolith** – system działa jako jeden artefakt, ale dzieli się na odizolowane logicznie moduły (bounded contexty), które nie współdzielą kodu ani modeli.
- **Hexagonal Architecture (Ports and Adapters)** – każdy moduł posiada zewnętrzne porty wejściowe (driven) i wyjściowe (driving). Komunikacja przebiega przez interfejsy.
- **Clean Architecture** – warstwowy podział logiczny: `web → adapter → application → domain`, z jednoznacznym kierunkiem zależności (na zewnątrz → do środka).
- **Domain-Driven Design (DDD)** – logika oparta na modelu domenowym. Każdy moduł zawiera własną domenę, bez dzielenia modeli z innymi.

---

## 2. Warstwy logiczne

```
┌──────────────────────────────────────────────────────────────┐
│                        Web (Controllers)                     │
│  └─ adapter-in: REST, WebSocket                              │
├──────────────────────────────────────────────────────────────┤
│                    Application (Use Cases)                   │
│  └─ service, orchestracja, koordynacja, bez logiki domenowej │
├──────────────────────────────────────────────────────────────┤
│                      Domain (Model)                          │
│  └─ encje, agregaty, serwisy domenowe, polityki              │
├──────────────────────────────────────────────────────────────┤
│                 Adapter-out (Persistence, Kafka, etc.)       │
│  └─ adapter-out: Mongo, Kafka, Redis, HTTP                   │
└──────────────────────────────────────────────────────────────┘
```

Każdy moduł trzyma ten sam układ warstw i nie udostępnia kodu innym modułom.

---

## 3. Podział katalogów w projekcie

```
application/                        ← punkt wejścia do systemu (Spring Boot)
modules/
├── workflows/                      ← silnik workflowów
│   └── src/main/java/.../
│       ├── domain/                 ← logika wykonania workflowów
│       ├── application/           ← use case'y: start, wykonaj, przerwij
│       ├── adapter/               ← Kafka, Mongo
│       └── web/                   ← kontrolery REST
├── triggers/                      ← wyzwalacze workflowów (webhook, scheduler)
├── actions/                       ← rejestracja akcji + healthcheck
ui/frontend/                       ← frontend React (layout, sidebar, auth)
action/send-email/                 ← osobna aplikacja agenta wykonującego akcję "send-email"
agent-gateway/                     ← osobna aplikacja komunikująca się z agentami przez WebSocket
```

---

## 4. Reguły modularności

- Brak współdzielenia kodu między modułami
- Brak wspólnych helperów, DTO, modeli
- Komunikacja między modułami wyłącznie przez:
    - porty i adaptery (interfejsy)
    - REST z autoryzacją (policy-based)
    - kolejki (Kafka, RabbitMQ)
- Moduły mają osobne konteksty MongoDB (oddzielne kolekcje, indeksy)
- Testy jednostkowe tylko w module, którego dotyczą

---

## 5. Konwencje wewnętrzne

- Brak użycia Lomboka (`@Data`, `@Builder` itd.)
- Własne konfiguracje per moduł (np. `AgentGatewayConfig`)
- JWT HMAC z kluczami w `monify.jwt.keys.*`
- Profil `local` = tylko bazy, `dev` = całość
- Reverse proxy wspierane: aplikacja działa pod `/app`, `/gateway` itd.

---

## 6. Diagram C4 (poziom Container)
**Kontenery:**

- **Frontend** (`React`, typ SPA)
- **Backend** (Spring Boot Modular Monolith)
- **MongoDB** (dokumentowa baza danych)
- **RabbitMQ** (kolejka do komunikacji agentów)
- **Reverse Proxy** (np. Traefik, Nginx)
- **Agent Gateway** (WebSocket bridge z agentami)
- **Send Email Agent** (osobna aplikacja wykonująca akcje)

Komunikacja:
- Frontend → Backend REST (autoryzowany JWT)
- Agent → Agent Gateway WebSocket (JWT + ping/register/result)
- Agent Gateway → Backend przez Kafka (execution-request/result)
- Backend → MongoDB (per moduł)
- Backend ↔ RabbitMQ (wymiana danych między aplikacjami)

---

## 7. Status
Model architektoniczny jest obowiązującym dokumentem referencyjnym. Każda zmiana wymaga aktualizacji tej dokumentacji oraz decyzji w formie ADR.

