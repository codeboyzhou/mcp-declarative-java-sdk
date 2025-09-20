package com.github.codeboyzhou.mcp.declarative;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.codeboyzhou.mcp.declarative.configuration.McpServerConfiguration;
import com.github.codeboyzhou.mcp.declarative.configuration.YAMLConfigurationLoader;
import com.github.codeboyzhou.mcp.declarative.enums.ServerMode;
import com.github.codeboyzhou.mcp.declarative.exception.McpServerConfigurationException;
import com.github.codeboyzhou.mcp.declarative.server.McpSseServerInfo;
import com.github.codeboyzhou.mcp.declarative.server.McpStreamableServerInfo;
import com.github.codeboyzhou.mcp.declarative.test.TestSimpleMcpStdioServer;
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
    TestSimpleMcpStdioServer.main(new String[] {}); // just for jacoco coverage report

    String classpath = System.getProperty("java.class.path");

    ServerParameters serverParameters =
        ServerParameters.builder("java")
            .args("-cp", classpath, TestSimpleMcpStdioServer.class.getName())
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
    assertEquals(1, resources.size());
    McpSchema.Resource resource = resources.get(0);
    assertEquals("test://resource1", resource.uri());
    assertEquals("resource1_name", resource.name());
    assertEquals("resource1_title", resource.title());
    assertEquals("resource1_description", resource.description());
    assertEquals("text/plain", resource.mimeType());
  }

  private void verifyPromptsRegistered(McpSyncClient client) {
    List<McpSchema.Prompt> prompts = client.listPrompts().prompts();
    assertEquals(2, prompts.size());

    McpSchema.Prompt prompt1 =
        prompts.stream()
            .filter(prompt -> prompt.name().equals("prompt1_name"))
            .findAny()
            .orElse(null);
    assertNotNull(prompt1);
    assertEquals("prompt1_name", prompt1.name());
    assertEquals("prompt1_title", prompt1.title());
    assertEquals("prompt1_description", prompt1.description());

    List<McpSchema.PromptArgument> arguments1 = prompt1.arguments();
    assertEquals(2, arguments1.size());
    assertEquals("param1", arguments1.get(0).name());
    assertEquals("param1_title", arguments1.get(0).title());
    assertEquals("param1_description", arguments1.get(0).description());
    assertEquals("param2", arguments1.get(1).name());
    assertEquals("param2_title", arguments1.get(1).title());
    assertEquals("param2_description", arguments1.get(1).description());

    McpSchema.Prompt prompt2 =
        prompts.stream()
            .filter(prompt -> prompt.name().equals("prompt2_name"))
            .findAny()
            .orElse(null);
    assertNotNull(prompt2);
    assertEquals("prompt2_name", prompt2.name());
    assertEquals("prompt2_title", prompt2.title());
    assertEquals("prompt2_description", prompt2.description());

    List<McpSchema.PromptArgument> arguments2 = prompt2.arguments();
    assertEquals(2, arguments2.size());
    assertEquals("param1", arguments2.get(0).name());
    assertEquals("param1_title", arguments2.get(0).title());
    assertEquals("param1_description", arguments2.get(0).description());
    assertEquals("param2", arguments2.get(1).name());
    assertEquals("param2_title", arguments2.get(1).title());
    assertEquals("param2_description", arguments2.get(1).description());
  }

  private void verifyPromptsCalled(McpSyncClient client) {
    String name1 = "prompt1_name";
    Map<String, Object> args1 = Map.of("param1", "value1", "param2", "value2");
    McpSchema.GetPromptRequest request1 = new McpSchema.GetPromptRequest(name1, args1);
    McpSchema.GetPromptResult result1 = client.getPrompt(request1);
    McpSchema.TextContent content = (McpSchema.TextContent) result1.messages().get(0).content();
    assertEquals("prompt1_description", result1.description());
    assertEquals("prompt1 is called", content.text());

    String name2 = "prompt2_name";
    Map<String, Object> args2 = Map.of("param1", "value1", "param2", "value2");
    McpSchema.GetPromptRequest request2 = new McpSchema.GetPromptRequest(name2, args2);
    McpSchema.GetPromptResult result2 = client.getPrompt(request2);
    McpSchema.TextContent content2 = (McpSchema.TextContent) result2.messages().get(0).content();
    assertEquals("prompt2_description", result2.description());
    assertEquals("prompt2 is called", content2.text());
  }

  private void verifyToolsRegistered(McpSyncClient client) {
    List<McpSchema.Tool> tools = client.listTools().tools();
    assertEquals(3, tools.size());

    McpSchema.Tool tool1 =
        tools.stream().filter(tool -> tool.name().equals("tool1_name")).findAny().orElse(null);
    assertNotNull(tool1);
    assertEquals("tool1_name", tool1.name());
    assertEquals("tool1_title", tool1.title());
    assertEquals("tool1_description", tool1.description());

    McpSchema.Tool tool2 =
        tools.stream().filter(tool -> tool.name().equals("tool2_name")).findAny().orElse(null);
    assertNotNull(tool2);
    assertEquals("tool2_name", tool2.name());
    assertEquals("tool2_title", tool2.title());
    assertEquals("tool2_description", tool2.description());

    McpSchema.Tool tool3 =
        tools.stream().filter(tool -> tool.name().equals("tool3_name")).findAny().orElse(null);
    assertNotNull(tool3);
    assertEquals("tool3_name", tool3.name());
    assertEquals("tool3_title", tool3.title());
    assertEquals("tool3_description", tool3.description());
  }

  private void verifyToolsCalled(McpSyncClient client) {
    String name1 = "tool1_name";
    Map<String, Object> args1 = Map.of("param1", "value1", "param2", "value2");
    McpSchema.CallToolRequest request1 = new McpSchema.CallToolRequest(name1, args1);
    McpSchema.CallToolResult result1 = client.callTool(request1);
    McpSchema.TextContent content = (McpSchema.TextContent) result1.content().get(0);
    assertFalse(result1.isError());
    assertEquals("tool1 is called", content.text());

    String name2 = "tool2_name";
    Map<String, Object> args2 = Map.of("param1", "value1", "param2", "value2");
    McpSchema.CallToolRequest request2 = new McpSchema.CallToolRequest(name2, args2);
    McpSchema.CallToolResult result2 = client.callTool(request2);
    McpSchema.TextContent content2 = (McpSchema.TextContent) result2.content().get(0);
    assertFalse(result2.isError());
    assertEquals("tool2 is called", content2.text());

    String name3 = "tool3_name";
    Map<String, Object> args3 = Map.of("param1", "value1", "param2", "value2");
    McpSchema.CallToolRequest request3 = new McpSchema.CallToolRequest(name3, args3);
    McpSchema.CallToolResult result3 = client.callTool(request3);
    McpSchema.TextContent content3 = (McpSchema.TextContent) result3.content().get(0);
    assertFalse(result3.isError());
    assertEquals("This tool returned nullable or void", content3.text());
  }
}
