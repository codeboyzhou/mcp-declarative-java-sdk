---
hide:
    - navigation
    - toc
---

# Core Components

MCP (Model Context Protocol) defines three core component types, and this SDK simplifies the creation process of these components through annotations.

## Resources

Resource components are used to expose data to LLMs, similar to GET requests in Web APIs.

### Basic Usage

```java
import com.github.codeboyzhou.mcp.declarative.annotation.McpResource;

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

### Annotation Explanation

- `@McpResource`: Marks a method as an MCP resource
- `uri`: Unique identifier of the resource, following URI format
- `description`: Resource description for LLM to understand the resource's purpose

## Tools

Tool components are used to execute operations or calculations, similar to POST requests in Web APIs.

### Basic Usage

```java
import com.github.codeboyzhou.mcp.declarative.annotation.McpTool;
import com.github.codeboyzhou.mcp.declarative.annotation.McpToolParam;

public class MyTools {
    @McpTool(description = "Calculate the sum of two numbers")
    public int add(
        @McpToolParam(name = "a", description = "First number", required = true) int a,
        @McpToolParam(name = "b", description = "Second number", required = true) int b
    ) {
        return a + b;
    }

    @McpTool(description = "Read complete file contents with UTF-8 encoding")
    public String readFile(@McpToolParam(name = "path", description = "File path", required = true) String path) {
        try {
            return Files.readString(Path.of(path));
        } catch (IOException e) {
            return "Error reading file: " + e.getMessage();
        }
    }
}
```

### Annotation Explanation

- `@McpTool`: Marks a method as an MCP tool
- `@McpToolParam`: Marks method parameters as tool parameters
  - `name`: Parameter name
  - `description`: Parameter description
  - `required`: Whether the parameter is required

## Prompts

Prompt components are used to define reusable prompt templates.

### Basic Usage

```java
import com.github.codeboyzhou.mcp.declarative.annotation.McpPrompt;
import com.github.codeboyzhou.mcp.declarative.annotation.McpPromptParam;

public class MyPrompts {
    @McpPrompt(description = "Generate code for a given task")
    public String generateCode(
        @McpPromptParam(name = "language", description = "Programming language", required = true) String language,
        @McpPromptParam(name = "task", description = "Task description", required = true) String task
    ) {
        return String.format("Write %s code to: %s", language, task);
    }

    @McpPrompt(description = "Format text as specified style")
    public String formatText(
        @McpPromptParam(name = "text", description = "Text to format", required = true) String text,
        @McpPromptParam(name = "style", description = "Format style (e.g., formal, casual, technical)", required = true) String style
    ) {
        return String.format("Rewrite the following text in a %s style: %s", style, text);
    }
}
```

### Annotation Explanation

- `@McpPrompt`: Marks a method as an MCP prompt
- `@McpPromptParam`: Marks method parameters as prompt parameters
  - `name`: Parameter name
  - `description`: Parameter description
  - `required`: Whether the parameter is required

## Multilingual Support

This SDK has built-in multilingual support, which can be enabled through the `@McpI18nEnabled` annotation.

### Configure Multilingual

```java
@McpServerApplication
@McpI18nEnabled(resourceBundleBaseName = "messages")
public class I18nMcpServer {
    public static void main(String[] args) {
        McpServers.run(I18nMcpServer.class, args)
            .startStdioServer(McpServerInfo.builder()
                .name("i18n-server")
                .version("1.0.0")
                .build());
    }
}
```

### Internationalization Resource Files

Create `messages.properties` file:

```properties
# messages.properties
tool.add.description=Calculate the sum of two numbers
tool.add.param.a.description=First number
tool.add.param.b.description=Second number
resource.system.info.description=System information
prompt.generate.code.description=Generate code for a given task
prompt.generate.code.param.language.description=Programming language
prompt.generate.code.param.task.description=Task description
```

Create `messages_zh_CN.properties` file:

```properties
# messages_zh_CN.properties
tool.add.description=Calculate the sum of two numbers
tool.add.param.a.description=First number
tool.add.param.b.description=Second number
resource.system.info.description=System information
prompt.generate.code.description=Generate code for a given task
prompt.generate.code.param.language.description=Programming language
prompt.generate.code.param.task.description=Task description
```

Using internationalized messages in components:

```java
@McpTool(description = "tool.add.description")
public int add(
    @McpToolParam(name = "a", description = "tool.add.param.a.description") int a,
    @McpToolParam(name = "b", description = "tool.add.param.b.description") int b
) {
    return a + b;
}
```

## Automatic Registration

After defining MCP components, they will be automatically registered to the server. You just need to ensure that the component classes are in the package scanning path of the server application.

If you need to specify a specific package path, you can use the following methods:

```java
@McpServerApplication(basePackageClass = MyMcpServer.class)
// or
@McpServerApplication(basePackage = "com.example.mcp.components")
```

If no package path is specified, the package containing the main method will be scanned.
