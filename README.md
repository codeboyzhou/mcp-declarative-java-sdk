# Annotation-driven MCP Java SDK

![Java](https://img.shields.io/badge/Java-17+-blue)
[![maven-central](https://img.shields.io/maven-central/v/io.github.codeboyzhou/mcp-declarative-java-sdk?color=blue)](https://mvnrepository.com/artifact/io.github.codeboyzhou/mcp-declarative-java-sdk)
[![coverage](https://img.shields.io/codecov/c/github/codeboyzhou/mcp-declarative-java-sdk?logo=codecov&color=brightgreen)](https://app.codecov.io/github/codeboyzhou/mcp-declarative-java-sdk)
[![GitHub Action](https://github.com/codeboyzhou/mcp-declarative-java-sdk/actions/workflows/maven-build.yml/badge.svg)](https://github.com/codeboyzhou/mcp-declarative-java-sdk/actions/workflows/maven-build.yml)

Declarative [MCP Java SDK](https://github.com/modelcontextprotocol/java-sdk) Development with Java Annotations.

Easily and Quickly Define, Develop and Integrate your MCP Resources/Prompts/Tools, but no Spring required.

Documentation site is here: https://codeboyzhou.github.io/mcp-declarative-java-sdk.

Please feel free to feed back what's helpful and what's not.

## Advantages

- 🚫 No Spring Framework Required.
- ⚡  Instant MCP Java server in 1 LOC.
- 🎉 No need to write more SDK low-level code.
- 👏 Get rid of complex and lengthy JSON schema definitions.
- 🎯 Just focus on your core logic (resources/prompts/tools).
- 🔌 Configuration file compatible with the Spring AI Framework.
- 🌍 Built-in multi-languages support for MCP components (resources/prompts/tools).

## Showcase

Just put this one line code in your `main` method:

```java
// You can use this annotation to specify the base package
// to scan for MCP resources, prompts, tools, but it's optional.
// If not specified, it will scan the package where the main method is located.
@McpServerApplication(basePackage = "com.github.codeboyzhou.mcp.server.examples")
// Use this annotation to enable multi-languages support for MCP server components.
@McpI18nEnabled
public class MyMcpServer {

  public static void main(String[] args) {
    McpServers servers = McpServers.run(MyMcpServer.class, args);

    // Start a STDIO MCP server
    servers.startStdioServer(McpServerInfo.builder().name("mcp-server").version("1.0.0").build());

    // or a HTTP SSE MCP server
    servers.startSseServer(McpSseServerInfo.builder().name("mcp-server").version("1.0.0").port(8080).build());

    // or a Streamable HTTP MCP server
    servers.startStreamableServer(McpStreamableServerInfo.builder().name("mcp-server").version("1.0.0").port(8080).build());

    // or start with yaml config file (compatible with Spring AI)
    servers.startServer();

    // or start with a custom config file (compatible with Spring AI)
    servers.startServer("my-mcp-server.yml");
  }

}
```

This is a yaml configuration file example (named `mcp-server.yml` by default) only if you are using `startServer()` method:

```yaml
enabled: true
mode: STREAMABLE
name: mcp-server
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

No need to care about the low-level details of native MCP Java SDK and how to create the MCP resources, prompts, and tools. Just annotate them like this:

```java
public class McpResources {

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
public class McpPrompts {

  // This method defines a MCP prompt to read a file
  @McpPrompt(description = "A simple prompt to read a file")
  public String readFile(@McpPromptParam(name = "path", description = "filepath", required = true) String path) {
    // Just put your logic code here, forget about the MCP SDK details.
    return String.format("What is the complete contents of the file: %s", path);
  }

}
```

```java
public class McpTools {

  // This method defines a MCP tool to read a file
  @McpTool(description = "Read complete file contents with UTF-8 encoding")
  public String readFile(@McpToolParam(name = "path", description = "filepath", required = true) String path) {
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
<!-- Internally relies on native MCP Java SDK 0.13.0 -->
<dependency>
    <groupId>io.github.codeboyzhou</groupId>
    <artifactId>mcp-declarative-java-sdk</artifactId>
    <version>0.8.3-SNAPSHOT</version>
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
