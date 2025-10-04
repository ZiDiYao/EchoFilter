# EchoFilter

EchoFilter is a **Spring Boot–based comment analysis service** that classifies, annotates, and scores the trustworthiness of user comments from social media platforms such as Reddit or YouTube.  
It detects misinformation, extreme speech, personal opinions, and more, returning a **structured JSON** output for UI highlighting and filtering.

---

## Features
- **Overall Classification** – Classifies comments using `OverallType` enum (OPINION / FACT / MISINFORMATION / TOXICITY / OTHER)  
- **Granular Annotations** – Tags specific text spans with `Category` and `Topic` enums  
- **Trust Score** – Provides a `trustScore` (0.0 ~ 1.0) representing factual reliability  
- **Facts & Evidence** – Supplies concise fact points and scientific evidence for FACT / MISINFORMATION  
- **Counterexamples for Extremes** – Generates balanced counterexamples for extreme or harmful statements  
- **Multi-Platform Support** – Can process comments from multiple platforms (currently tested with Reddit)

---

## Tech Stack

### Backend Framework
- **Spring Boot 3.x**  
- **Spring Web** for REST endpoints and JSON serialization  
- **Spring Kafka** for distributed messaging and event-driven processing  
- **Spring WebSocket / SSE** for real-time front-end updates  

### Programming Language
- **Java 21+**

### Build Tool
- **Maven** for dependency management and build automation  

### Databases & Caching
- **MySQL** – Primary data store for analysis results, logs, and metadata  
- **Redis** – Low-latency caching, rate limiting, session storage, and distributed locks  

### Messaging & Asynchronous Processing
- **Apache Kafka** –  
  - Used to decouple LLM requests from real-time API calls  
  - `ef.requests.v1` topic receives incoming comment analysis tasks  
  - `ef.results.v1` topic streams back individual analysis results  
  - Enables scalable, fault-tolerant, and asynchronous LLM processing pipelines  

### AI & NLP Integration
- Multiple **LLM APIs** (OpenAI, DeepSeek, etc.) for classification and annotation  
- Custom JSON-based prompt templates with schema validation  
- Supports parallel execution and mixed-model strategies through the factory pattern  

### Concurrency & Performance
- **Virtual Threads (Project Loom)** –  
  - Each comment task is handled in its own lightweight virtual thread  
  - Enables massive concurrency with minimal blocking cost during LLM calls  
  - Combined with a `Semaphore` to control concurrent request limits and protect API quotas  

### Real-Time Updates
- **WebSocket / Server-Sent Events (SSE)** –  
  - Streams `taskId`-based results directly to the front-end as soon as they are available  
  - Allows users to see analysis progress in real-time (e.g., per post, per comment)  
  - Integrates with Kafka consumers for push-based event routing  

### Web Scraping & Data Enrichment
- **BeautifulSoup** (Python integration) for fetching and preprocessing contextual web content and API provided by certain websides.

---

## Architecture Overview


