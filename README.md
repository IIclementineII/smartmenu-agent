# SmartMenu Agent 🍽️

> Turn any restaurant menu into a conversational AI experience.

SmartMenu Agent is an AI-powered menu management system for **Jade Palace Restaurant**, built for the [Google Cloud Rapid Agent Hackathon 2026](https://rapid-agent.devpost.com/) — MongoDB Partner Track.

**Live Demo:** https://v0-smartmenu.vercel.app/  
**Frontend Repository:** https://github.com/IIclementineII/v0-smartmenu

---

## What It Does

**For customers** — Ask Jade, our AI concierge, natural language questions:
- *"Any dishes without peanuts under $15?"*
- *"What vegetarian options do you have?"*
- *"What does the chef recommend?"*

**For restaurant owners** — Manage the menu with natural language commands:
- *"Update Kung Pao Chicken price to $19.99"* → MongoDB updates instantly

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| AI Agent | Google Cloud Agent Builder + Gemini 2.5 Flash |
| Database | MongoDB Atlas (via MCP Server) |
| Backend | Spring Boot 3 + Java 21 |
| Frontend | Next.js + Tailwind CSS |
| Backend Hosting | Railway |
| Frontend Hosting | Vercel |

---

## Architecture

User → Next.js (Vercel) → Spring Boot (Railway) → Dialogflow CX → Gemini 2.5 Flash → MongoDB Atlas

---

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/dishes` | All menu items |
| GET | `/api/dishes/summary` | Filtered menu (vegetarian, spicy, maxPrice, allergen) |
| GET | `/api/dishes/search` | Search by keyword or allergen |
| PATCH | `/api/dishes/{name}/price` | Update dish price |
| PATCH | `/api/dishes/{name}/stock` | Update stock |
| PATCH | `/api/dishes/{name}/availability` | Toggle availability |
| POST | `/api/chat` | Chat with Jade AI |

---

## Local Development

**Prerequisites:** Java 21, Maven 3.9+, MongoDB Atlas account, Google Cloud service account

```bash
git clone https://github.com/IIclementineII/smartmenu-agent.git
cd smartmenu-agent
# Add src/main/resources/smartmenu-credentials.json (Google service account key)
# Add MongoDB connection string to application.yaml
mvn spring-boot:run
```

---

## License

MIT License — see [LICENSE](LICENSE)
