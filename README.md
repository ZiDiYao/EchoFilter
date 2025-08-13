# EchoFilter

EchoFilter is a **Spring Boot–based comment analysis service** that classifies, annotates, and scores the trustworthiness of user comments from social media platforms such as Reddit or YouTube.  
It detects misinformation, extreme speech, personal opinions, and more, returning a **structured JSON** output for UI highlighting and filtering.

## Features
- **Overall Classification** – Classifies comments using `OverallType` enum (OPINION / FACT / MISINFORMATION / TOXICITY / OTHER)  
- **Granular Annotations** – Tags specific text spans with `Category` and `Topic` enums  
- **Trust Score** – Provides a `trustScore` (0.0 ~ 1.0) representing factual reliability  
- **Facts & Evidence** – Supplies concise fact points and scientific evidence for FACT / MISINFORMATION  
- **Counterexamples for Extremes** – Generates balanced counterexamples for extreme or harmful statements  
- **Multi-Platform Support** – Can process comments from multiple platforms (currently tested with Reddit)
## Tech Stack

- **Backend Framework**:  
  - Spring Boot 3.x 
  - Spring Web for HTTP endpoint handling and JSON serialization  

- **Programming Language**:  
  - Java 17+ 

- **Build Tool**:  
  - Maven for dependency management, build automation

- **Databases & Caching**:  
  - **MySQL** as the primary relational database for storing analysis results, logs, and platform metadata  
  - **Redis** for low-latency caching, rate limiting, session storage, and distributed locking in high-concurrency scenarios  

- **Messaging & Asynchronous Processing**:  
  - Message Queue (Kafka) for asynchronous event processing, offloading heavy AI calls, and decoupled service communication  

- **AI & NLP Integration**:  
  - Multiple LLM APIs (e.g., OpenAI, DeepSeek) for comment classification and annotation  
  - Custom prompt templates with strict JSON schema enforcement  

- **Web Scraping & Data Enrichment**:  
  - **BeautifulSoup** (Python integration) for fetching and preprocessing web content as additional context for analysis  

