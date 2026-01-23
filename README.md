# Annotation-driven MCP Java SDK

![Java](https://img.shields.io/badge/Java-17+-blue)
[![maven-central](https://img.shields.io/maven-central/v/io.github.thought2code/mcp-annotated-java-sdk?color=blue)](https://central.sonatype.com/artifact/io.github.thought2code/mcp-annotated-java-sdk)
[![coverage](https://img.shields.io/codecov/c/github/thought2code/mcp-annotated-java-sdk?logo=codecov&color=brightgreen)](https://app.codecov.io/github/thought2code/mcp-annotated-java-sdk)
[![GitHub Action](https://github.com/thought2code/mcp-annotated-java-sdk/actions/workflows/maven-build.yml/badge.svg)](https://github.com/thought2code/mcp-annotated-java-sdk/actions/workflows/maven-build.yml)

> Annotation-driven MCP dev 🚀 No Spring, Zero Boilerplate, Pure Java.

This SDK is a lightweight, annotation-based framework that simplifies MCP server development in Java. Define, develop and integrate your MCP Resources / Prompts / Tools with minimal code - No Spring Framework Required.

[📖 Documentation](https://thought2code.github.io/mcp-annotated-java-sdk-docs) | [💡 Examples](https://github.com/thought2code/mcp-java-sdk-examples/tree/main/mcp-server-filesystem/mcp-server-filesystem-annotated-sdk-implementation) | [🐛 Report Issues](https://github.com/thought2code/mcp-annotated-java-sdk/issues)

## ✨ Why This SDK?

### Key Advantages

- 🚫 **No Spring Framework Required** - Pure Java, lightweight and fast
- ⚡ **Instant MCP Server** - Get your server running with just 1 line of code
- 🎉 **Zero Boilerplate** - No need to write low-level MCP SDK code
- 👏 **No JSON Schema** - Forget about complex and lengthy JSON definitions
- 🎯 **Focus on Logic** - Concentrate on your core business logic
- 🔌 **Spring AI Compatible** - Configuration file compatible with Spring AI Framework
- 🌍 **Multilingual Support** - Built-in i18n support for MCP components
- 📦 **Type-Safe** - Leverage Java's type system for compile-time safety

### Comparison with [Official MCP Java SDK](https://github.com/modelcontextprotocol/java-sdk)

| Feature        | Official MCP SDK | This SDK        |
|----------------|------------------|-----------------|
| Code Required  | ~50-100 lines    | ~5-10 lines     |
| JSON Schema    | Hand-coded JSON  | No need to care |
| Type Safety    | Limited          | Full            |
| Learning Curve | Steep            | Gentle          |
| Multilingual   | Unsupported      | Supported       |

## 🎯 Quick Start

### Prerequisites

- **Java 17 or later** (required by official MCP Java SDK)
- **Maven 3.6+** or **Gradle 7+**

### 5-Minutes Tutorial

#### Step 1: Add Dependency

**Maven:**
```xml
<dependency>
    <groupId>io.github.thought2code</groupId>
    <artifactId>mcp-annotated-java-sdk</artifactId>
    <version>0.12.1</version>
</dependency>
```

**Gradle:**
```gradle
implementation 'io.github.thought2code:mcp-annotated-java-sdk:0.12.1'
```

#### Step 2: Create Your First MCP Server

```java
@McpServerApplication
public class MyFirstMcpServer {
    /**
     * Main method to start the MCP server.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        McpServerConfiguration.Builder configuration =
            McpServerConfiguration.builder().name("my-first-mcp-server").version("1.0.0");
        McpServers.run(MyFirstMcpServer.class, args).startStdioServer(configuration);
    }
}
```

#### Step 3: Define MCP Resources (if needed)

```java
public class MyResources {
    @McpResource(uri = "system://info", description = "System information")
    public Map<String, String> getSystemInfo() {
        Map<String, String> info = new HashMap<>();
        info.put("os", System.getProperty("os.name"));
        info.put("java", System.getProperty("java.version"));
        info.put("cores", String.valueOf(Runtime.getRuntime().availableProcessors()));
        return info;
    }
}
```

#### Step 4: Define MCP Tools

```java
public class MyTools {
    @McpTool(description = "Calculate the sum of two numbers")
    public int add(
        @McpToolParam(name = "a", description = "First number", required = true) int a,
        @McpToolParam(name = "b", description = "Second number", required = true) int b
    ) {
        return a + b;
    }
}
```

#### Step 5: Define MCP Prompts (if needed)

```java
public class MyPrompts {
    @McpPrompt(description = "Generate code for a given task")
    public String generateCode(
        @McpPromptParam(name = "language", description = "Programming language", required = true) String language,
        @McpPromptParam(name = "task", description = "Task description", required = true) String task
    ) {
        return String.format("Write %s code to: %s", language, task);
    }
}
```

#### Step 6: Run Your Server

```bash
# Compile and run
mvn clean package
java -jar target/your-app.jar
```

That's it! Your MCP server is now ready to serve resources, tools, and prompts!

## 📚 Core Concepts

### What is MCP?

The [Model Context Protocol (MCP)](https://modelcontextprotocol.io) is a standardized protocol for building servers that expose data and functionality to LLM applications. Think of it like a web API, but specifically designed for LLM interactions.

### MCP Components

| Component     | Purpose            | Analogy        |
|---------------|--------------------|----------------|
| **Resources** | Expose data to LLM | GET endpoints  |
| **Tools**     | Execute actions    | POST endpoints |
| **Prompts**   | Reusable templates | Form templates |

### Supported Server Modes

This SDK supports three MCP server modes:

1. **STDIO** - Standard input/output communication (default for CLI tools)
2. **SSE (Server-Sent Events)** - HTTP-based real-time communication
3. **Streamable HTTP** - HTTP streaming for web applications

## 🔧 Advanced Usage

### Configuration File

Create `mcp-server.yml` in your classpath:

```yaml
enabled: true
mode: STREAMABLE
name: my-mcp-server
version: 1.0.0
type: SYNC
request-timeout: 20000
capabilities:
  resource: true
  prompt: true
  tool: true
change-notification:
  resource: true
  prompt: true
  tool: true
streamable:
  mcp-endpoint: /mcp/message
  disallow-delete: true
  keep-alive-interval: 30000
  port: 8080
```

Then start your server:

```java
McpServers servers = McpServers.run(MyMcpServer.class, args);
// Uses default mcp-server.yml
servers.startServer();
// or
servers.startServer("custom-config.yml");
```

### Multilingual Support

Enable i18n for your MCP components:

```java
@McpServerApplication
@McpI18nEnabled(resourceBundleBaseName = "messages")
public class I18nMcpServer {
    /**
     * Main method to start the MCP server with i18n support.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        McpServerConfiguration.Builder configuration =
            McpServerConfiguration.builder().name("i18n-mcp-server").version("1.0.0");
        McpServers.run(I18nMcpServer.class, args).startStdioServer(configuration);
    }
}

// Create messages.properties
# messages.properties
tool.calculate.description=Calculate the sum of two numbers
tool.calculate.param.a.description=First number
tool.calculate.param.b.description=Second number

// Create messages_zh_CN.properties
# messages_zh_CN.properties
tool.calculate.description=计算两个数字的和
tool.calculate.param.a.description=第一个数字
tool.calculate.param.b.description=第二个数字

// Then use the i18n messages in your MCP components, like this:
@McpTool(description = "tool.calculate.description")
public int add(
    @McpToolParam(name = "a", description = "tool.calculate.param.a.description") int a,
    @McpToolParam(name = "b", description = "tool.calculate.param.b.description") int b
) {
    return a + b;
}
```

## 🏗️ Project Structure

A typical project structure:

```
your-mcp-project/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           ├── MyMcpServer.java          # Main entry point
│   │   │           ├── components/
│   │   │           │   ├── MyResources.java     # MCP Resources
│   │   │           │   ├── MyTools.java         # MCP Tools
│   │   │           │   └── MyPrompts.java       # MCP Prompts
│   │   │           └── service/
│   │   │               └── BusinessLogic.java    # Your business logic
│   │   └── resources/
│   │       ├── mcp-server.yml                    # MCP configuration
│   │       └── messages.properties               # i18n messages
│   └── test/
│       └── java/
│           └── com/
│               └── example/
│                   └── McpServerTest.java         # Unit tests
```

## 🧪 Testing

Run the test suite:

```bash
mvn clean test
```

Run tests with coverage:

```bash
mvn clean test jacoco:report
```

## ❓ FAQ

### Q: Do I need Spring Framework?

**A:** No! This SDK is completely independent of Spring Framework. However, the configuration file format is compatible with Spring AI if you want to migrate.

### Q: Can I use this in production?

**A:** This project is currently in active development. While it's stable for development and testing, we recommend thorough testing before production use.

### Q: What Java version is required?

**A:** Java 17 or later is required, as this is a constraint of the underlying MCP Java SDK.

### Q: How do I debug my MCP server?

**A:** You can use the [inspector](https://github.com/modelcontextprotocol/inspector) and set Java breakpoint to debug your MCP server.

## 🤝 Contributing

We welcome and appreciate contributions! Please follow these steps to contribute:

1. **Fork the repository**
2. **Create a new branch** for your feature or bug fix
3. **Submit a pull request** with a clear description of your changes

### Development Setup

```bash
# Clone the repository
git clone https://github.com/thought2code/mcp-annotated-java-sdk.git
cd mcp-annotated-java-sdk

# Build the project
mvn clean install

# Run tests
mvn clean test
```

## 📖 Documentation

- [Official Documentation](https://thought2code.github.io/mcp-annotated-java-sdk-docs)
- [Examples Repository](https://github.com/thought2code/mcp-java-sdk-examples/tree/main/mcp-server-filesystem/mcp-server-filesystem-annotated-sdk-implementation)
- [MCP Official Site](https://modelcontextprotocol.io)

## 📄 License

This project is licensed under the [MIT License](LICENSE).

## 🙏 Acknowledgments

- [MCP Java SDK](https://github.com/modelcontextprotocol/java-sdk) - The underlying MCP implementation
- [Model Context Protocol](https://modelcontextprotocol.io) - The protocol specification

> [!NOTE]
> This project is under active development. We appreciate your feedback and contributions!
