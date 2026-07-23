# studyChat - RAG Chatbot with Distributed Web Crawler

A full-stack, AI-powered Chatbot system (`studyChat`) designed for university students to query course regulations, class schedules, and university information. The project features a distributed web crawler that gathers information directly from university websites (e.g., Heilbronn University / HHN) and feeds it into a **Retrieval-Augmented Generation (RAG)** pipeline.

## System Architecture & Flow

1. **Distributed Crawling:** A custom **Apache Storm** topology crawls targeted web domains. Text content is extracted, cleaned, and structured.
2. **JSON Export & RAG Indexing:** Crawled resources are written to structured JSON documents. These documents are processed by **LangChain4j**, which generates text embeddings locally (`all-minilm-l6-v2`) and indexes them for vector search.
3. **Conversational Interface:** A **Spring Boot** web server hosts an interactive chat client. When a student asks a question, the system retrieves semantically relevant information from the vector store and uses an LLM to generate context-aware, factual responses.

## Key Features

* **Real-Time Web Crawler (Apache Storm & Storm Crawler):**
  * Configured via `CrawlTopology.java`.
  * Custom Storm Bolts:
    * `HHNStructuredDataBolt.java`: Custom HTML parser that extracts structured university data.
    * `DepthControlBolt.java`: Prevents crawler runaway by controlling traversal depth.
    * `URLExtractorBolt.java`: Parses and filters URLs for queueing.
    * `RAGJSONFileWriterBolt.java`: Serializes crawled pages into RAG-compliant JSON files.
* **Semantic Search & RAG (LangChain4j):**
  * Local embedding generation using the `all-minilm-l6-v2` model.
  * Contextual query matching using vector distance.
  * Prompt templates to limit LLM hallucinations by forcing responses to be grounded in retrieved document context (`RAGService.java`).
* **Interactive Web Server (Spring Boot):**
  * Live chat system powered by **Spring WebSockets**.
  * Admin dashboard to trigger, monitor, and configure crawl jobs (`CrawlerController.java`).
  * Responsive Thymeleaf UI templates.

## Technology Stack

* **Language:** Java 17
* **Web Framework:** Spring Boot 3.4.5 (Web, WebSockets, Thymeleaf, Log4j2)
* **Distributed Processing:** Apache Storm 2.4.0
* **Web Scraping:** Storm Crawler 2.4 & JSoup 1.16.1
* **AI & LLM Integration:** LangChain4j 0.27.1

## Getting Started

### Prerequisites

* Java 17 JDK
* Maven 3.x
* An LLM API token (configured in `AppConfig.java` / environment variables)

### Running the Application

1. Clone this repository.
2. Build the Maven project:
   ```bash
   mvn clean install
   ```
3. Run the Spring Boot application:
   ```bash
   mvn spring-boot:run
   ```
4. Access the web interface at `http://localhost:8080`.