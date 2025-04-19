# Annotation-driven MCP Java SDK

![Java](https://img.shields.io/badge/Java-17+-blue)
[![maven-central](https://img.shields.io/maven-central/v/io.github.codeboyzhou/mcp-declarative-java-sdk?color=blue)](https://mvnrepository.com/artifact/io.github.codeboyzhou/mcp-declarative-java-sdk)
[![coverage](https://img.shields.io/codecov/c/github/codeboyzhou/mcp-declarative-java-sdk?logo=codecov&color=brightgreen)](https://app.codecov.io/github/codeboyzhou/mcp-declarative-java-sdk)
[![GitHub Action](https://github.com/codeboyzhou/mcp-declarative-java-sdk/actions/workflows/maven-build.yml/badge.svg)](https://github.com/codeboyzhou/mcp-declarative-java-sdk/actions/workflows/maven-build.yml)

Declarative [MCP Java SDK](https://github.com/modelcontextprotocol/java-sdk) Development with Java Annotations.

## Advantages

- No Spring Framework Required.
- Instant MCP Java server in 1 LOC.
- No need to write more SDK low-level codes.
- Get rid of complex and lengthy JSON schema definitions.
- Just focus on your core logic (resources/prompts/tools).

## Showcase

Just put this one line code in your `main` method:

```java
// You can use this annotation to specify the base package
// to scan for MCP resources, prompts, tools, but it's optional.
// If not specified, it will scan the package where the main method is located.
@McpComponentScan(basePackage = "com.github.codeboyzhou.mcp.examples")
public class MyMcpServer {

    public static void main(String[] args) {
        // Start a STDIO MCP server
        McpServers.run(MyMcpServer.class, args).startSyncStdioServer("mcp-server", "1.0.0");
        // or a HTTP SSE MCP server
        McpServers.run(MyMcpServer.class, args).startSyncSseServer("mcp-server", "1.0.0");
    }

}
```

No need to care about the low-level details of native MCP Java SDK and how to create the MCP resources, prompts, and tools. Just annotate them like this:

```java
@McpResources
public class MyMcpResources {

    // This method defines a MCP resource to expose the OS env variables
    @McpResource(uri = "env://variables", description = "OS env variables")
    public String getSystemEnv() {
        // Just put your logic code here, forget about the MCP SDK details.
        return System.getenv().toString();
    }

    // Your other MCP resources here...
}
```

```java
@McpTools
public class MyMcpTools {

    // This method defines a MCP tool to read a file
    @McpTool(description = "Read complete file contents with UTF-8 encoding")
    public String readFile(
        @McpToolParam(name = "path", description = "filepath", required = true) String path) {
        // Just put your logic code here, forget about the MCP SDK details.
        return Files.readString(Path.of(path));
    }

    // Your other MCP tools here...
}
```

Now it's all set, run your MCP server, choose one MCP client you like and start your MCP exploration journey.

> [!WARNING]
> Please note that this project is under development and is not ready for production use.

## Getting Started

### Requirements

- Java 17 or later (Restricted by MCP Java SDK)

### Installation

Add the following Maven dependency to your project:

```xml
<!-- Internally relies on native MCP Java SDK 0.9.0 -->
<dependency>
    <groupId>io.github.codeboyzhou</groupId>
    <artifactId>mcp-declarative-java-sdk</artifactId>
    <version>0.2.0</version>
</dependency>
```

### Examples

You can find more examples and usages in this [repository](https://github.com/codeboyzhou/mcp-java-sdk-examples).

## What is MCP?

The [Model Context Protocol (MCP)](https://modelcontextprotocol.io) lets you build servers that expose data and functionality to LLM applications in a secure, standardized way. Think of it like a web API, but specifically designed for LLM interactions. MCP servers can:

- Expose data through **Resources** (think of these sort of like GET endpoints; they are used to load information into the LLM's context)
- Provide functionality through **Tools** (sort of like POST endpoints; they are used to execute code or otherwise produce a side effect)
- Define interaction patterns through **Prompts** (reusable templates for LLM interactions)
- And more!

You can start exploring everything about **MCP** from [here](https://modelcontextprotocol.io).
