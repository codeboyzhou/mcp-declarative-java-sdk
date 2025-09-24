---
hide:
    - navigation
---

# Getting Started

## Requirements

🔒 Java 17 or later (Restricted by MCP Java SDK)

## Installation

Add the following Maven dependency to your project:

```xml
<!-- Internally relies on native MCP Java SDK 0.13.0 -->
<dependency>
    <groupId>io.github.codeboyzhou</groupId>
    <artifactId>mcp-declarative-java-sdk</artifactId>
    <version>0.8.4-SNAPSHOT</version>
</dependency>
```

## MCP Server

Now you can create a simple MCP server with just one line of core code.

### Stdio Server

#### Quick Start

```java
import com.github.codeboyzhou.mcp.declarative.McpServers;
import com.github.codeboyzhou.mcp.declarative.annotation.McpI18nEnabled;
import com.github.codeboyzhou.mcp.declarative.annotation.McpServerApplication;
import com.github.codeboyzhou.mcp.declarative.server.McpServerInfo;

@McpI18nEnabled
@McpServerApplication
public class McpStdioServer {

  public static void main(String[] args) {
    McpServers.run(McpStdioServer.class, args).startStdioServer(McpServerInfo.builder().build());
  }

}
```

In the sample code above, we created a simple MCP server, which is based on the stdio transport mode.
`@McpServerApplication`
is a convenience annotation that helps to locate the package path of MCP server components, such as resources, prompts,
and tools.

You can also explicitly specify the package path to scan, either of the two ways below is sufficient:

```java
@McpServerApplication(basePackageClass = McpStdioServer.class)
```

```java
@McpServerApplication(basePackage = "com.github.codeboyzhou.mcp.server.examples")
```

If you don't specify the package path, the annotation will scan the package where the main method is located.

#### Server Info

In addition, for the method `startStdioServer`, you need to provide a `McpServerInfo` object, which contains the basic
information of the MCP server, such as name, version, and instructions, etc.

The following is all the field information about class `McpServerInfo`:

| Field            | Type     | Description                           | Default Value  |
|------------------|----------|---------------------------------------|----------------|
| `name`           | String   | The name of the MCP server            | `mcp-server`   |
| `version`        | String   | The version of the MCP server         | `1.0.0`        |
| `instructions`   | String   | The instructions of the MCP server    | (empty string) |
| `requestTimeout` | Duration | The timeout of the MCP server request | `20` seconds   |

#### How to run

For a MCP stdio server to run, you need to package your project into an executable jar file.

There is a Maven plugin that can handle this, just place the following configuration into your root `pom.xml`:

```xml
<plugins>
  <!-- Your other plugins ... -->
  <plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-shade-plugin</artifactId>
    <version>${maven-shade-plugin.version}</version>
    <executions>
      <execution>
        <goals>
          <goal>shade</goal>
        </goals>
        <phase>package</phase>
        <configuration>
          <transformers>
            <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
              <mainClass>com.github.codeboyzhou.mcp.server.examples.McpStdioServer</mainClass>
            </transformer>
          </transformers>
        </configuration>
      </execution>
    </executions>
  </plugin>
</plugins>
```

### HTTP SSE Server (Deprecated)

#### Quick Start

```java
import com.github.codeboyzhou.mcp.declarative.McpServers;
import com.github.codeboyzhou.mcp.declarative.annotation.McpI18nEnabled;
import com.github.codeboyzhou.mcp.declarative.annotation.McpServerApplication;
import com.github.codeboyzhou.mcp.declarative.server.McpSseServerInfo;

@McpI18nEnabled
@McpServerApplication
public class McpSseServer {

  public static void main(String[] args) {
    McpServers.run(McpSseServer.class, args).startSseServer(McpSseServerInfo.builder().build());
  }

}
```

#### Server Info

For the method `startSseServer`, you can specify the server information by using `McpSseServerInfo`:

| Field             | Type     | Description                            | Default Value  |
|-------------------|----------|----------------------------------------|----------------|
| `name`            | String   | The name of the MCP server             | `mcp-server`   |
| `version`         | String   | The version of the MCP server          | `1.0.0`        |
| `instructions`    | String   | The instructions of the MCP server     | (empty string) |
| `requestTimeout`  | Duration | The timeout of the MCP server request  | `20` seconds   |
| `baseUrl`         | String   | The base URL of the MCP server         | (empty string) |
| `messageEndpoint` | String   | The endpoint of the MCP server message | `/mcp/message` |
| `sseEndpoint`     | String   | The endpoint for HTTP SSE mode         | `/sse`         |
| `port`            | int      | The port for HTTP Server               | `8080`         |

#### How to run

Just run the main class like you would launch a web application, and then it's all set.

### Streamable HTTP Server

#### Quick Start

```java
import com.github.codeboyzhou.mcp.declarative.McpServers;
import com.github.codeboyzhou.mcp.declarative.annotation.McpI18nEnabled;
import com.github.codeboyzhou.mcp.declarative.annotation.McpServerApplication;
import com.github.codeboyzhou.mcp.declarative.server.McpStreamableServerInfo;

@McpI18nEnabled
@McpServerApplication
public class McpStreamableServer {

  public static void main(String[] args) {
    McpStreamableServerInfo serverInfo = McpStreamableServerInfo.builder().build();
    McpServers.run(McpStreamableServer.class, args).startStreamableServer(serverInfo);
  }

}
```

#### Server Info

For the method `startStreamableServer`, you can specify the server information by using `McpStreamableServerInfo`:

| Field               | Type     | Description                                                                  | Default Value  |
|---------------------|----------|------------------------------------------------------------------------------|----------------|
| `name`              | String   | The name of the MCP server                                                   | `mcp-server`   |
| `version`           | String   | The version of the MCP server                                                | `1.0.0`        |
| `instructions`      | String   | The instructions of the MCP server                                           | (empty string) |
| `requestTimeout`    | Duration | The timeout of the MCP server request                                        | `20` seconds   |
| `mcpEndpoint`       | String   | The endpoint of the MCP server message                                       | `/mcp`         |
| `disallowDelete`    | boolean  | Whether to disable the DELETE method of HTTP                                 | `false`        |
| `keepAliveInterval` | Duration | The interval for keep-alive pings. No keep-alive will be scheduled if `null` | `null`         |
| `port`              | int      | The port for HTTP server                                                     | `8080`         |

#### How to run

Just run the main class like you would launch a web application, and then it's all set.

## MCP Component

In the previous section, we have learned how to create a MCP server, but the server still has no usable components, like
MCP resources, prompts, and tools. In this section, we will learn how to create MCP components easily with the support
of this high-level SDK. Refer to the following sample code, just focus on your core logic, forget about the low-level
details of native MCP Java SDK.

### Resource

```java
import com.github.codeboyzhou.mcp.declarative.annotation.McpResource;

public class McpResources {

  /**
   * This method defines a MCP resource to expose the OS env variables.
   */
  @McpResource(uri = "system://env", description = "OS env variables")
  public String getSystemEnv() {
    // Just put your logic code here, forget about the native MCP SDK details.
    return System.getenv().toString();
  }

  // Your other MCP resources here...
}
```

### Prompt

```java
import com.github.codeboyzhou.mcp.declarative.annotation.McpPrompt;
import com.github.codeboyzhou.mcp.declarative.annotation.McpPromptParam;

public class McpPrompts {

  /**
   * This method defines a MCP prompt to read a file.
   */
  @McpPrompt(description = "A simple prompt to read a file")
  public String readFile(@McpPromptParam(name = "path", description = "filepath", required = true) String path) {
    // Just put your logic code here, forget about the native MCP SDK details.
    return String.format("What is the complete contents of the file: %s", path);
  }

  // Your other MCP prompts here...
}
```

### Tool

```java
import com.github.codeboyzhou.mcp.declarative.annotation.McpTool;
import com.github.codeboyzhou.mcp.declarative.annotation.McpToolParam;

public class McpTools {

  /**
   * This method defines a MCP tool to read a file.
   */
  @McpTool(description = "Read complete file contents with UTF-8 encoding")
  public String readFile(@McpToolParam(name = "path", description = "filepath", required = true) String path) {
    // Just put your logic code here, forget about the native MCP SDK details.
    return Files.readString(Path.of(path));
  }

  // Your other MCP tools here...
}
```

Now it's all set, all you have to do is run your MCP server, and all the resources, prompts, and tools will be registered
automatically.
