package com.github.codeboyzhou.mcp.declarative;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.codeboyzhou.mcp.declarative.configuration.McpServerConfiguration;
import com.github.codeboyzhou.mcp.declarative.configuration.YAMLConfigurationLoader;
import com.github.codeboyzhou.mcp.declarative.enums.JavaTypeToJsonSchemaMapper;
import com.github.codeboyzhou.mcp.declarative.enums.ServerMode;
import com.github.codeboyzhou.mcp.declarative.exception.McpServerConfigurationException;
import com.github.codeboyzhou.mcp.declarative.server.McpSseServerInfo;
import com.github.codeboyzhou.mcp.declarative.server.McpStreamableServerInfo;
import com.github.codeboyzhou.mcp.declarative.server.McpStructuredContent;
import com.github.codeboyzhou.mcp.declarative.test.TestMcpStdioServer;
import com.github.codeboyzhou.mcp.declarative.test.TestMcpToolsStructuredContent;
import com.github.codeboyzhou.mcp.declarative.util.StringHelper;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import io.modelcontextprotocol.json.McpJsonMapper;
import io.modelcontextprotocol.spec.McpSchema;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.junit.jupiter.api.Test;

class McpServersTest {

  McpServers servers = McpServers.run(McpServersTest.class, new String[] {});

  @Test
  void testStartStdioServer_shouldSucceed() {
    TestMcpStdioServer.main(new String[] {}); // just for jacoco coverage report

    String classpath = System.getProperty("java.class.path");

    ServerParameters serverParameters =
        ServerParameters.builder("java")
            .args("-cp", classpath, TestMcpStdioServer.class.getName())
            .build();

    StdioClientTransport stdioClientTransport =
        new StdioClientTransport(serverParameters, McpJsonMapper.getDefault());

    try (McpSyncClient client = McpClient.sync(stdioClientTransport).build()) {
      verify(client);
    }
  }

  @Test
  void testStartSseServer_shouldSucceed() {
    final int port = new Random().nextInt(8000, 9000);

    McpSseServerInfo serverInfo =
        McpSseServerInfo.builder()
            .name("mcp-server")
            .version("1.0.0")
            .instructions("test")
            .requestTimeout(Duration.ofSeconds(10))
            .baseUrl("http://localhost:" + port)
            .port(port)
            .sseEndpoint("/sse")
            .messageEndpoint("/mcp/message")
            .build();

    HttpClientSseClientTransport transport =
        HttpClientSseClientTransport.builder("http://localhost:" + port)
            .sseEndpoint("/sse")
            .build();

    servers.startSseServer(serverInfo);

    try (McpSyncClient client = McpClient.sync(transport).build()) {
      verify(client);
    }
  }

  @Test
  void testStartStreamableServer_shouldSucceed() {
    final int port = new Random().nextInt(8000, 9000);

    McpStreamableServerInfo serverInfo =
        McpStreamableServerInfo.builder()
            .name("mcp-server")
            .version("1.0.0")
            .instructions("test")
            .requestTimeout(Duration.ofSeconds(10))
            .port(port)
            .mcpEndpoint("/mcp/message")
            .build();

    HttpClientStreamableHttpTransport transport =
        HttpClientStreamableHttpTransport.builder("http://localhost:" + port)
            .endpoint("/mcp/message")
            .build();

    servers.startStreamableServer(serverInfo);

    try (McpSyncClient client = McpClient.sync(transport).build()) {
      verify(client);
    }
  }

  @Test
  void testStartServer_disabledMCP_shouldSucceed() {
    String configFileName = "test-mcp-server-disabled.yml";
    YAMLConfigurationLoader configLoader = new YAMLConfigurationLoader(configFileName);
    McpServerConfiguration configuration = configLoader.loadConfig();
    assertDoesNotThrow(() -> servers.startServer(configFileName));
    assertFalse(configuration.enabled());
  }

  @Test
  void testStartServer_enableStdioMode_shouldSucceed() {
    String configFileName = "test-mcp-server-enable-stdio-mode.yml";
    YAMLConfigurationLoader configLoader = new YAMLConfigurationLoader(configFileName);
    McpServerConfiguration configuration = configLoader.loadConfig();
    assertDoesNotThrow(() -> servers.startServer(configFileName));
    assertSame(ServerMode.STDIO, configuration.mode());
  }

  @Test
  void testStartServer_enableHttpSseMode_shouldSucceed() {
    String configFileName = "test-mcp-server-enable-http-sse-mode.yml";
    YAMLConfigurationLoader configLoader = new YAMLConfigurationLoader(configFileName);
    McpServerConfiguration configuration = configLoader.loadConfig();
    assertDoesNotThrow(() -> servers.startServer(configFileName));
    assertSame(ServerMode.SSE, configuration.mode());
  }

  @Test
  void testStartServer_enableStreamableHttpMode_shouldSucceed() {
    String configFileName = "test-mcp-server-enable-streamable-http-mode.yml";
    YAMLConfigurationLoader configLoader = new YAMLConfigurationLoader(configFileName);
    McpServerConfiguration configuration = configLoader.loadConfig();
    assertDoesNotThrow(() -> servers.startServer(configFileName));
    assertSame(ServerMode.STREAMABLE, configuration.mode());
  }

  @Test
  void testStartServer_enableUnknownMode_shouldThrowException() {
    String configFileName = "test-mcp-server-enable-unknown-mode.yml";
    assertThrows(McpServerConfigurationException.class, () -> servers.startServer(configFileName));
  }

  @Test
  void testStartServer_useDefaultConfigFileName_shouldSucceed() {
    String configFileName = "mcp-server.yml";
    YAMLConfigurationLoader configLoader = new YAMLConfigurationLoader(configFileName);
    McpServerConfiguration configuration = configLoader.loadConfig();
    assertSame(ServerMode.STREAMABLE, configuration.mode());
    assertDoesNotThrow(() -> servers.startServer());
  }

  private void verify(McpSyncClient client) {
    verifyServerInfo(client);
    verifyResourcesRegistered(client);
    verifyPromptsRegistered(client);
    verifyToolsRegistered(client);
    verifyPromptsCalled(client);
    verifyToolsCalled(client);
  }

  private void verifyServerInfo(McpSyncClient client) {
    McpSchema.InitializeResult initialized = client.initialize();
    assertEquals("mcp-server", initialized.serverInfo().name());
    assertEquals("1.0.0", initialized.serverInfo().version());
    assertEquals("test", initialized.instructions());
  }

  private void verifyResourcesRegistered(McpSyncClient client) {
    List<McpSchema.Resource> resources = client.listResources().resources();
    assertEquals(2, resources.size());

    verifyResourceRegistered(
        resources,
        "test://resource1",
        "resource1_name",
        "resource1_title",
        "resource1_description");
    verifyResourceRegistered(
        resources,
        "test://resource2",
        "resource2_name",
        "resource2_title",
        "resource2_description");
  }

  private void verifyResourceRegistered(
      List<McpSchema.Resource> resources,
      String resourceUri,
      String resourceName,
      String resourceTitle,
      String resourceDescription) {

    McpSchema.Resource resource =
        resources.stream().filter(r -> r.uri().equals(resourceUri)).findAny().orElse(null);
    assertNotNull(resource);
    assertEquals(resourceUri, resource.uri());
    assertEquals(resourceName, resource.name());
    assertEquals(resourceTitle, resource.title());
    assertEquals(resourceDescription, resource.description());
  }

  private void verifyResourcesCalled(McpSyncClient client) {
    verifyResourceCalled(client, "test://resource1", "text/plain", "resource1_content");
    verifyResourceCalled(client, "test://resource2", "text/plain", "resource2_content");
  }

  private void verifyResourceCalled(
      McpSyncClient client, String resourceUri, String resourceMimeType, String resourceContent) {

    McpSchema.ReadResourceRequest request = new McpSchema.ReadResourceRequest(resourceUri);
    McpSchema.ReadResourceResult result = client.readResource(request);
    McpSchema.TextResourceContents content =
        (McpSchema.TextResourceContents) result.contents().get(0);
    assertNotNull(content);
    assertEquals(resourceUri, content.uri());
    assertEquals(resourceMimeType, content.mimeType());
    assertEquals(resourceContent, content.text());
  }

  private void verifyPromptsRegistered(McpSyncClient client) {
    List<McpSchema.Prompt> prompts = client.listPrompts().prompts();
    assertEquals(10, prompts.size());

    verifyPromptRegistered(prompts, "promptWithDefaultName", "title", "description", 0);
    verifyPromptRegistered(prompts, "promptWithDefaultTitle", "Not specified", "description", 0);
    verifyPromptRegistered(prompts, "promptWithDefaultDescription", "title", "Not specified", 0);
    verifyPromptRegistered(prompts, "promptWithAllDefault", "Not specified", "Not specified", 0);
    verifyPromptRegistered(prompts, "promptWithOptionalParam", "Not specified", "Not specified", 1);
    verifyPromptRegistered(prompts, "promptWithRequiredParam", "Not specified", "Not specified", 1);
    verifyPromptRegistered(prompts, "promptWithMultiParams", "Not specified", "Not specified", 2);
    verifyPromptRegistered(prompts, "promptWithMixedParams", "Not specified", "Not specified", 1);
    verifyPromptRegistered(prompts, "promptWithVoidReturn", "Not specified", "Not specified", 0);
    verifyPromptRegistered(prompts, "promptWithReturnNull", "Not specified", "Not specified", 0);
  }

  private void verifyPromptRegistered(
      List<McpSchema.Prompt> prompts,
      String promptName,
      String promptTitle,
      String promptDescription,
      int promptArgumentsSize) {

    McpSchema.Prompt prompt =
        prompts.stream().filter(p -> p.name().equals(promptName)).findAny().orElse(null);
    assertNotNull(prompt);
    assertEquals(promptName, prompt.name());
    assertEquals(promptTitle, prompt.title());
    assertEquals(promptDescription, prompt.description());
    assertEquals(promptArgumentsSize, prompt.arguments().size());
  }

  private void verifyPromptsCalled(McpSyncClient client) {
    verifyPromptCalled(
        client, "promptWithDefaultName", Map.of(), "promptWithDefaultName is called");
    verifyPromptCalled(
        client, "promptWithDefaultTitle", Map.of(), "promptWithDefaultTitle is called");
    verifyPromptCalled(
        client, "promptWithDefaultDescription", Map.of(), "promptWithDefaultDescription is called");
    verifyPromptCalled(client, "promptWithAllDefault", Map.of(), "promptWithAllDefault is called");
    verifyPromptCalled(
        client,
        "promptWithOptionalParam",
        Map.of("param", "value"),
        "promptWithOptionalParam is called with param: value");
    verifyPromptCalled(
        client,
        "promptWithRequiredParam",
        Map.of("param", "value"),
        "promptWithRequiredParam is called with param: value");
    verifyPromptCalled(
        client,
        "promptWithMultiParams",
        Map.of("param1", "value1", "param2", "value2"),
        "promptWithMultiParams is called with params: value1, value2");
    verifyPromptCalled(
        client,
        "promptWithMixedParams",
        Map.of("mcpParam", "value"),
        "promptWithMixedParams is called with params: value, " + StringHelper.EMPTY);
    verifyPromptCalled(
        client,
        "promptWithVoidReturn",
        Map.of(),
        "The method call succeeded but has a void return type");
    verifyPromptCalled(
        client,
        "promptWithReturnNull",
        Map.of(),
        "The method call succeeded but the return value is null");
  }

  private void verifyPromptCalled(
      McpSyncClient client, String promptName, Map<String, Object> params, String expectedResult) {

    McpSchema.GetPromptRequest request = new McpSchema.GetPromptRequest(promptName, params);
    McpSchema.GetPromptResult result = client.getPrompt(request);
    McpSchema.TextContent content = (McpSchema.TextContent) result.messages().get(0).content();
    assertEquals(expectedResult, content.text());
  }

  private void verifyToolsRegistered(McpSyncClient client) {
    List<McpSchema.Tool> tools = client.listTools().tools();
    assertEquals(22, tools.size());

    verifyToolRegistered(tools, "toolWithDefaultName", "title", "description", Map.of());
    verifyToolRegistered(tools, "toolWithDefaultTitle", "Not specified", "description", Map.of());
    verifyToolRegistered(tools, "toolWithDefaultDescription", "title", "Not specified", Map.of());
    verifyToolRegistered(tools, "toolWithAllDefault", "Not specified", "Not specified", Map.of());
    verifyToolRegistered(
        tools,
        "toolWithOptionalParam",
        "Not specified",
        "Not specified",
        Map.of("param", String.class));
    verifyToolRegistered(
        tools,
        "toolWithRequiredParam",
        "Not specified",
        "Not specified",
        Map.of("param", String.class));
    verifyToolRegistered(
        tools,
        "toolWithMultiParams",
        "Not specified",
        "Not specified",
        Map.of("param1", String.class, "param2", String.class));
    verifyToolRegistered(
        tools,
        "toolWithMixedParams",
        "Not specified",
        "Not specified",
        Map.of("mcpParam", String.class));
    verifyToolRegistered(tools, "toolWithVoidReturn", "Not specified", "Not specified", Map.of());
    verifyToolRegistered(tools, "toolWithReturnNull", "Not specified", "Not specified", Map.of());
    verifyToolRegistered(
        tools, "toolWithIntParam", "Not specified", "Not specified", Map.of("param", int.class));
    verifyToolRegistered(
        tools,
        "toolWithIntegerParam",
        "Not specified",
        "Not specified",
        Map.of("param", Integer.class));
    verifyToolRegistered(
        tools, "toolWithLongParam", "Not specified", "Not specified", Map.of("param", long.class));
    verifyToolRegistered(
        tools,
        "toolWithLongClassParam",
        "Not specified",
        "Not specified",
        Map.of("param", Long.class));
    verifyToolRegistered(
        tools,
        "toolWithFloatParam",
        "Not specified",
        "Not specified",
        Map.of("param", float.class));
    verifyToolRegistered(
        tools,
        "toolWithFloatClassParam",
        "Not specified",
        "Not specified",
        Map.of("param", Float.class));
    verifyToolRegistered(
        tools,
        "toolWithDoubleParam",
        "Not specified",
        "Not specified",
        Map.of("param", double.class));
    verifyToolRegistered(
        tools,
        "toolWithDoubleClassParam",
        "Not specified",
        "Not specified",
        Map.of("param", Double.class));
    verifyToolRegistered(
        tools,
        "toolWithNumberParam",
        "Not specified",
        "Not specified",
        Map.of("param", Number.class));
    verifyToolRegistered(
        tools,
        "toolWithBooleanParam",
        "Not specified",
        "Not specified",
        Map.of("param", boolean.class));
    verifyToolRegistered(
        tools,
        "toolWithBooleanClassParam",
        "Not specified",
        "Not specified",
        Map.of("param", Boolean.class));
    verifyToolRegistered(
        tools, "toolWithReturnStructuredContent", "Not specified", "Not specified", Map.of());
  }

  @SuppressWarnings("unchecked")
  private void verifyToolRegistered(
      List<McpSchema.Tool> tools,
      String toolName,
      String toolTitle,
      String toolDescription,
      Map<String, Class<?>> inputSchemaPropertiesTypes) {

    McpSchema.Tool tool =
        tools.stream().filter(t -> t.name().equals(toolName)).findAny().orElse(null);
    assertNotNull(tool);
    assertEquals(toolName, tool.name());
    assertEquals(toolTitle, tool.title());
    assertEquals(toolDescription, tool.description());
    assertEquals(inputSchemaPropertiesTypes.size(), tool.inputSchema().properties().size());

    // verify input schema properties types
    tool.inputSchema()
        .properties()
        .forEach(
            (name, property) -> {
              Map<String, String> props = (Map<String, String>) property;
              Class<?> javaClass = inputSchemaPropertiesTypes.get(name);
              final String jsonSchemaType = JavaTypeToJsonSchemaMapper.getJsonSchemaType(javaClass);
              assertEquals(jsonSchemaType, props.get("type"));
            });
  }

  private void verifyToolsCalled(McpSyncClient client) {
    verifyToolCalled(client, "toolWithDefaultName", Map.of(), "toolWithDefaultName is called");
    verifyToolCalled(client, "toolWithDefaultTitle", Map.of(), "toolWithDefaultTitle is called");
    verifyToolCalled(
        client, "toolWithDefaultDescription", Map.of(), "toolWithDefaultDescription is called");
    verifyToolCalled(client, "toolWithAllDefault", Map.of(), "toolWithAllDefault is called");
    verifyToolCalled(
        client,
        "toolWithOptionalParam",
        Map.of("param", "value"),
        "toolWithOptionalParam is called with optional param: value");
    verifyToolCalled(
        client,
        "toolWithRequiredParam",
        Map.of("param", "value"),
        "toolWithRequiredParam is called with required param: value");
    verifyToolCalled(
        client,
        "toolWithMultiParams",
        Map.of("param1", "value1", "param2", "value2"),
        "toolWithMultiParams is called with params: value1, value2");
    verifyToolCalled(
        client,
        "toolWithMixedParams",
        Map.of("mcpParam", "value"),
        "toolWithMixedParams is called with params: value, " + StringHelper.EMPTY);
    verifyToolCalled(
        client,
        "toolWithVoidReturn",
        Map.of(),
        "The method call succeeded but has a void return type");
    verifyToolCalled(
        client,
        "toolWithReturnNull",
        Map.of(),
        "The method call succeeded but the return value is null");
    verifyToolCalled(
        client,
        "toolWithIntParam",
        Map.of("param", 123),
        "toolWithIntParam is called with param: 123");
    verifyToolCalled(
        client,
        "toolWithIntegerParam",
        Map.of("param", 123),
        "toolWithIntegerParam is called with param: 123");
    verifyToolCalled(
        client,
        "toolWithLongParam",
        Map.of("param", 123L),
        "toolWithLongParam is called with param: 123");
    verifyToolCalled(
        client,
        "toolWithLongClassParam",
        Map.of("param", 123L),
        "toolWithLongClassParam is called with param: 123");
    verifyToolCalled(
        client,
        "toolWithFloatParam",
        Map.of("param", 123.0F),
        "toolWithFloatParam is called with param: 123.0");
    verifyToolCalled(
        client,
        "toolWithFloatClassParam",
        Map.of("param", 123.0F),
        "toolWithFloatClassParam is called with param: 123.0");
    verifyToolCalled(
        client,
        "toolWithDoubleParam",
        Map.of("param", 123.0),
        "toolWithDoubleParam is called with param: 123.0");
    verifyToolCalled(
        client,
        "toolWithDoubleClassParam",
        Map.of("param", 123.0),
        "toolWithDoubleClassParam is called with param: 123.0");
    verifyToolCalled(
        client,
        "toolWithNumberParam",
        Map.of("param", 123),
        "toolWithNumberParam is called with param: 123");
    verifyToolCalled(
        client,
        "toolWithBooleanParam",
        Map.of("param", true),
        "toolWithBooleanParam is called with param: true");
    verifyToolCalled(
        client,
        "toolWithBooleanClassParam",
        Map.of("param", true),
        "toolWithBooleanClassParam is called with param: true");
    verifyToolCalled(
        client,
        "toolWithReturnStructuredContent",
        Map.of(),
        new TestMcpToolsStructuredContent.TestStructuredContent(1, 2, 3L, 4L, 5.0F, 6.0F, 7.0, 8.0)
            .asTextContent());
  }

  private void verifyToolCalled(
      McpSyncClient client, String toolName, Map<String, Object> args, String expectedResult) {

    McpSchema.CallToolRequest request = new McpSchema.CallToolRequest(toolName, args);
    McpSchema.CallToolResult result = client.callTool(request);
    McpSchema.TextContent content = (McpSchema.TextContent) result.content().get(0);
    assertFalse(result.isError());
    assertEquals(expectedResult, content.text());

    if (result.structuredContent() instanceof McpStructuredContent structuredContent) {
      assertEquals(expectedResult, structuredContent.asTextContent());
    }
  }
}
