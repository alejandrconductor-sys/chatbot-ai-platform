# 🚀 Chatbot AI Platform

> Production-ready AI chatbot platform built with Java, Javalin, React, Groq Cloud AI, Ollama local models, Tavily internet search, and persistent memory architecture.

---

# 🧠 Overview

Chatbot AI Platform is a hybrid AI assistant designed with modern backend engineering principles and real-world AI integration patterns.

The system combines:

* ☁️ Cloud AI execution (Groq)
* 🖥️ Local AI execution (Ollama)
* 🌐 Real-time internet search (Tavily)
* 🧠 Persistent conversational memory
* ⚡ Intelligent routing system
* 🏗️ Layered architecture

This project was designed as a portfolio-grade AI engineering platform focused on scalability, maintainability, and production-oriented software architecture.

---

# 🏗️ System Architecture

The application follows a layered backend architecture:

```text
Frontend (React + Vite)
        ↓
Controllers (Javalin REST API)
        ↓
Services (Business Logic)
        ↓
AI Routing Layer
        ↓
Providers (Groq / Ollama / Tavily)
        ↓
Persistence Layer (DAO + MariaDB)
```

Core architecture principles:

* Separation of concerns
* Dependency isolation
* Provider abstraction
* Service-oriented backend design
* Reusable AI provider strategy
* Modular memory system

---

# ⚡ AI Routing System

The platform includes an intelligent routing engine capable of deciding how requests should be processed.

Available routes:

| Route    | Description                    |
| -------- | ------------------------------ |
| DIRECT   | Mathematical/direct responses  |
| AI ONLY  | Standard AI conversation       |
| WEB + AI | Internet search + AI reasoning |

The router optimizes:

* performance
* token usage
* response quality
* external API consumption

---

# 🌐 Real-Time Internet Search

The system integrates Tavily Search API for live internet access.

Features:

* Real-time web search
* News retrieval
* Current events
* Up-to-date factual information
* AI-enriched web context

Example:

* Current president queries
* Live news
* Real-time information requests

---

# 🧠 Persistent Memory System

The chatbot includes a tested and functional conversational memory system.

Capabilities:

* Conversation persistence
* Context reconstruction
* Memory injection into prompts
* Multi-message contextual reasoning
* Historical interaction tracking

Memory is stored in MariaDB and dynamically injected into AI prompts.

---

# ☁️ Cloud AI Mode

Cloud mode uses Groq as the AI inference provider.

Features:

* Ultra-fast inference
* Internet-enabled AI workflows
* Production-ready cloud execution
* Real-time conversational responses

Configuration:

```env
AI_MODE=cloud
```

---

# 🖥️ Local AI Mode (Ollama)

The platform also supports fully local AI execution using Ollama.

Features:

* Offline AI assistant
* Local LLM execution
* Privacy-focused architecture
* No internet dependency
* Local development/testing mode

Configuration:

```env
AI_MODE=local
```

Example supported models:

* llama3
* mistral
* codellama

---

# 🔄 Dual Execution Architecture

One of the platform's main features is dual execution capability:

| Mode        | Description                   |
| ----------- | ----------------------------- |
| Online Mode | Groq + Tavily internet search |
| Local Mode  | Ollama local inference        |

This allows:

* cloud production deployment
* local/private execution
* AI redundancy
* development flexibility

---

# 🛠️ Technologies Used

## Backend

* Java 17
* Javalin
* Maven
* JDBC
* HikariCP
* MariaDB

## AI / Search

* Groq API
* Ollama
* Tavily Search API

## Frontend

* React
* Vite
* TailwindCSS

## Utilities

* SLF4J
* Jackson
* Dotenv

---

# 📂 Project Structure

```text
src/
 ├── controller/
 ├── service/
 ├── dao/
 ├── model/
 ├── ai/
 │    ├── provider/
 │    ├── router/
 │    ├── search/
 │    └── memory/
 ├── config/
 └── util/

frontend/
target/
```

---

# ⚙️ Installation

## Clone repository

```bash
git clone https://github.com/alejandrconductor-sys/chatbot-ai-platform.git
```

```bash
cd chatbot-ai-platform
```

---

# 🔐 Environment Variables

Create `.env`

```env
# =========================
# LOCAL MODE (OLLAMA), desactivado
# =========================

# DB_HOST=localhost
# DB_PORT=3306
# DB_NAME=chatbot_ai_platform
# DB_USER=chatbot_user
# DB_PASSWORD=


# AI_MODE=local

OLLAMA_BASE_URL=http://localhost:11434
OLLAMA_MODEL=llama3.2:3b

# =========================
# CLOUD MODE (GROQ) activo
# =========================

DB_HOST=
DB_PORT=
DB_NAME=railway
DB_USER=
DB_PASSWORD=

AI_MODE=cloud

GROQ_API_KEY=
GROQ_MODEL=llama-3.3-70b-versatile

PORT=

# =========================
# WEB SEARCH
# =========================
TAVILY_API_KEY=
```

---

# 📦 Maven Build

Compile project:

```bash
mvn clean compile
```

Generate production fat JAR:

```bash
mvn clean package -DskipTests
```

---

# ▶️ Run Application

## Development

```bash
mvn exec:java
```

## Production

```bash
java -jar target/chatbot-ai-platform-1.0-SNAPSHOT.jar
```

Server:

```text
http://localhost:7070
```

---

# ☁️ Deployment

The platform is deployment-ready for:

* Render
* Railway
* VPS servers
* Docker environments
* Linux cloud instances

Recommended deployment:

* Render (Java Web Service)

---

# 📸 Screenshots

## Chat Interface

(Add screenshot here)

## AI Routing Logs

(Add screenshot here)

## Memory System

(Add screenshot here)

## Internet Search Integration

(Add screenshot here)

---

# 🔮 Future Improvements

Planned enhancements:

* JWT Authentication
* Role-based access
* Vector database memory
* RAG architecture
* Streaming responses
* Multi-agent orchestration
* Docker deployment
* Kubernetes support
* AI analytics dashboard
* Voice integration

---

# 👨‍💻 Author

**Rafael Marquez**

Backend Developer | AI Engineering Enthusiast | Java Developer

Technologies:

* Java
* SQL
* AI Integrations
* Backend Architecture
* Cloud + Local AI Systems

GitHub:
https://github.com/alejandrconductor-sys

---

# ⭐ Project Status

✅ Functional
✅ Memory working
✅ Internet search working
✅ Cloud AI working
✅ Local AI working
✅ Production build ready
✅ GitHub ready
✅ Deployment ready

