# Java MCP Projects – LangChain4j

A hands-on Java repository exploring the **Model Context Protocol (MCP)** using Java, LangChain4j, and AI models. This repository contains practical implementations of MCP clients, custom MCP servers, tool integration, and multi-server communication.

The goal is to understand how AI applications can interact with external tools, services, and systems through the Model Context Protocol.

---

## Projects Overview

This repository currently contains two independent Maven projects.

### 1. MCP FileSystem & Multi-Server Integration

**Folder:** `Mcp-FileSystem`

An implementation demonstrating how a Java AI application can connect to external MCP servers and use their tools through LangChain4j.

The project explores connecting multiple MCP servers to a single AI assistant.

**Features:**

- Connect Java applications to external MCP servers.
- Integrate MCP servers using `StdioMcpTransport`.
- Create MCP clients using `DefaultMcpClient`.
- Discover available tools using `listTools()`.
- Integrate MCP tools with LangChain4j using `McpToolProvider`.
- Build AI assistants using `AiServices`.
- Handle tool argument errors and tool execution errors.
- Connect multiple MCP servers to a single AI assistant.
- Integrate GitHub, Telegram, Filesystem, and other MCP-based tools.
- Use environment variables to manage API keys and credentials.

**MCP concepts explored:**

- `McpClient`
- `DefaultMcpClient`
- `StdioMcpTransport`
- `McpToolProvider`
- `AiServices`
- `ToolArgumentsErrorHandler`
- `ToolExecutionErrorHandler`
- MCP tool discovery and execution

#### Telegram AI Assistant

One of the implementations connects a Telegram MCP server and DuckDuckGo MCP server with Google Gemini through LangChain4j.

**Architecture:**

```text
             Telegram User
                   |
                   v
             Telegram MCP
                   |
                   v
           Incoming Messages
                   |
                   v
          Java Application
                   |
                   v
          LangChain4j AI Services
                   |
             Google Gemini
                   |
             McpToolProvider
                   |
           +-------+-------+
           |               |
           v               v
      Telegram MCP    DuckDuckGo MCP
       Send Message      Web Search
           |               |
           +-------+-------+
                   |
                   v
             AI Response
                   |
                   v
             Telegram User
```

**Technologies used:**

- Java
- LangChain4j
- Google Gemini
- Telegram MCP Server
- DuckDuckGo MCP Server
- Jackson
- Maven
- Node.js / npx
- Python uv / uvx

---

### 2. Custom MCP Server

**Folder:** `Custom-Mcp`

A custom MCP server implementation using Java and LangChain4j.

This project explores how to expose application functionality as MCP tools so that AI applications can discover and invoke them.

**Features and concepts:**

- Building custom MCP server functionality.
- Creating service classes for application operations.
- Exposing Java methods as tools.
- Understanding MCP server and client communication.
- Separating service logic from the application entry point.
- Connecting AI applications with custom tools.

**Project structure:**

```text
Custom-Mcp/
│
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── learning/
│   │               ├── services/
│   │               │   └── CourseService.java
│   │               │
│   │               └── Main.java
│   │
│   └── test/
│
├── pom.xml
└── .mvn/
```

---

## Repository Structure

```text
MCP-java-langchain4j/
│
├── Mcp-FileSystem/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   └── resources/
│   │   └── test/
│   ├── pom.xml
│   └── .mvn/
│
├── Custom-Mcp/
│   ├── src/
│   │   ├── main/
│   │   │   └── java/
│   │   └── test/
│   ├── pom.xml
│   └── .mvn/
│
├── .gitignore
└── README.md
```

Each project is maintained as an independent Maven application with its own dependencies and configuration.

---

## Technologies

| Technology | Purpose |
|---|---|
| Java | Core programming language |
| Maven | Dependency and build management |
| LangChain4j | Java AI application framework |
| MCP | Standardized tool integration |
| Google Gemini | AI language model |
| Telegram MCP | Telegram integration |
| DuckDuckGo MCP | Web search integration |
| Jackson | JSON processing |
| Node.js / npx | Running external MCP servers |
| Python uv / uvx | Running DuckDuckGo MCP server |
| Git & GitHub | Version control |

---

## Prerequisites

Install the following tools before running the projects:

- Java Development Kit (JDK)
- Apache Maven
- IntelliJ IDEA or another Java IDE
- Node.js and npm
- Python and uv (for the DuckDuckGo MCP integration)
- Telegram Bot Token
- Google Gemini API Key

---

## Environment Variables

Never hardcode API keys, passwords, or tokens in Java source code.

Configure the following environment variables before running the Telegram AI assistant.

| Variable | Description |
|---|---|
| `GOOGLE_API_KEY` | Google Gemini API key |
| `TELEGRAM_BOT_TOKEN` | Telegram bot authentication token |
| `OPENAI_API_KEY` | OpenAI API key, if using OpenAI integrations |

### Windows PowerShell

```powershell
setx GOOGLE_API_KEY "your_google_api_key"

setx TELEGRAM_BOT_TOKEN "your_telegram_bot_token"

setx OPENAI_API_KEY "your_openai_api_key"
```

Restart IntelliJ IDEA after setting environment variables.

### Java Example

```java
String googleApiKey = System.getenv("GOOGLE_API_KEY");

String telegramToken = System.getenv("TELEGRAM_BOT_TOKEN");

if (googleApiKey == null || googleApiKey.isBlank()) {
    throw new IllegalStateException("Google API key is missing");
}

if (telegramToken == null || telegramToken.isBlank()) {
    throw new IllegalStateException("Telegram bot token is missing");
}
```

---

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/Golla-Rakesh21/Model-context-protocol-java-langchain4j.git
```

### 2. Navigate to the repository

```bash
cd Model-context-protocol-java-langchain4j
```

### 3. Run the Custom MCP project

```bash
cd Custom-Mcp
```

Build the project:

```bash
mvn clean install
```

Run the application using its configured main class or IDE run configuration.

### 4. Run the MCP FileSystem project

Navigate back to the root:

```bash
cd ..
cd Mcp-FileSystem
```

Build the project:

```bash
mvn clean install
```

Configure the required environment variables and external MCP server prerequisites, then run the appropriate Java main class from IntelliJ IDEA or your configured Maven execution command.

---

## Key Learning Objectives

Through these projects, I am exploring:

1. What is Model Context Protocol (MCP)?
2. Why MCP is needed in AI applications.
3. How MCP clients and servers communicate.
4. How `StdioMcpTransport` works.
5. How to create and configure MCP clients.
6. How to discover and invoke MCP tools.
7. How to integrate MCP tools with LangChain4j.
8. How `McpToolProvider` works.
9. How to build AI assistants using `AiServices`.
10. How to connect multiple MCP servers to one AI application.
11. How to build custom MCP tools.
12. How to integrate AI models with external services.

---

## Security Best Practices

- Never commit API keys or bot tokens.
- Use environment variables for sensitive credentials.
- Keep `.env` files out of Git.
- Do not commit IntelliJ `.idea` directories.
- Exclude Maven `target` directories.
- Revoke and regenerate accidentally exposed credentials.
- Validate tool arguments before executing operations.
- Give MCP tools only the permissions they actually require.

---

## Future Improvements

- Build more custom MCP servers.
- Integrate GitHub MCP tools.
- Integrate database MCP servers.
- Explore multiple MCP transports.
- Implement additional tool execution examples.
- Explore MCP resources and prompts.
- Experiment with different AI model providers.
- Build more advanced multi-server AI applications.

---

## Author

**Golla Venkata Rakesh**

B.Tech – Computer Science and Engineering (AI & ML)

Interested in Java Development, Backend Engineering, Generative AI, LangChain4j, and Model Context Protocol.

- GitHub: [Golla-Rakesh21](https://github.com/Golla-Rakesh21)
- Portfolio: [rakesh21.pages.dev](https://rakesh21.pages.dev/)

---

⭐ If you find these implementations useful for learning Java MCP development, feel free to explore the projects and star the repository.

**Learning by building. Exploring MCP. Connecting AI with the real world.**
