# studyChat - RAG Chatbot with Distributed Web Crawler

A full-stack, AI-powered Chatbot system (`studyChat`) designed for university students to query course regulations, class schedules, and university information. The project features a distributed web crawler that gathers information directly from university websites (e.g., Heilbronn University / HHN) and feeds it into a **Retrieval-Augmented Generation (RAG)** pipeline.

## 🌟 Highlights
- **Modern UI:** Glassmorphic dark-mode interface built with custom CSS variables and WebKit optimisations.
- **Resilient AI Fallback:** Soft-fail mechanisms for LLM downtime, falling back to local/in-memory contexts automatically.
- **Crawler Architecture:** Uses Apache Storm to safely and efficiently crawl large amounts of documents.

## 🏗 System Architecture & Flow

1. **Distributed Crawling:** A custom **Apache Storm** topology crawls targeted web domains. Text content is extracted, cleaned, and structured.
2. **JSON Export & RAG Indexing:** Crawled resources are written to structured JSON documents. These documents are processed by **LangChain4j**, which generates text embeddings locally (`all-minilm-l6-v2`) and indexes them for vector search.
3. **Conversational Interface:** A **Spring Boot** web server hosts an interactive chat client. When a student asks a question, the system retrieves semantically relevant information from the vector store and uses an LLM to generate context-aware, factual responses.

## 🚀 Getting Started (For HHN Students / With Open WebUI)

### Prerequisites
* Java 17+ JDK
* Maven 3.x
* An active VPN connection to the HHN network (or Eduroam).
* Docker (for the Qdrant Vector Database).

### 1. Start Qdrant (Vector Database)
The system uses Qdrant to store and search the vector embeddings. Start it via Docker:
```bash
docker run -p 6333:6333 -p 6334:6334 -v qdrant_storage:/qdrant/storage:z qdrant/qdrant
```

### 2. Configure API Key
1. Go to `https://inference.it.hs-heilbronn.de` and log in.
2. Go to **Settings -> Account** and generate an API key.
3. Open `src/main/resources/application.properties` and replace your API key:
   ```properties
   openwebui.api.key=sk-YOUR_API_KEY
   ```

### 3. Build & Run
```bash
.\mvnw.cmd clean install
.\mvnw.cmd spring-boot:run
```
Access the web interface at `http://localhost:8080`.

## 🛠️ Testing without HHN Access (Offline / Local Mode)

If you do not have access to the HHN Open WebUI, you can still test the application locally!

1. Open `src/main/resources/application.properties`.
2. Disable the Qdrant requirement and use the in-memory fallback (no Docker needed!):
   ```properties
   use.inmemory.store=true
   ```
3. Run the application (`.\mvnw.cmd spring-boot:run`).
4. The system will detect that the LLM is unreachable and will automatically activate **Offline Mode**. You can use the web interface to crawl a test website, and the system will preview the exact context segments it *would* have sent to the AI.

## 🛠 Technology Stack

* **Language:** Java 17
* **Web Framework:** Spring Boot (Web, WebSockets, Thymeleaf)
* **Distributed Processing:** Apache Storm 
* **Web Scraping:** Storm Crawler & JSoup
* **AI & LLM Integration:** LangChain4j
* **Database:** Qdrant (Vector DB)