package com.github.thought2code.mcp.annotated;

import com.github.thought2code.mcp.annotated.configuration.McpConfigurationLoader;
import com.github.thought2code.mcp.annotated.configuration.McpServerConfiguration;
import com.github.thought2code.mcp.annotated.reflect.ReflectionsProvider;
import com.github.thought2code.mcp.annotated.server.McpServer;
import com.github.thought2code.mcp.annotated.server.McpSseServer;
import com.github.thought2code.mcp.annotated.server.McpStdioServer;
import com.github.thought2code.mcp.annotated.server.McpStreamableServer;
import com.github.thought2code.mcp.annotated.server.component.ResourceBundleProvider;
import com.github.thought2code.mcp.annotated.util.JacksonHelper;
import io.modelcontextprotocol.server.McpSyncServer;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main application class for the MCP (Model Context Protocol) annotated server.
 *
 * <p>This class provides the entry point for running MCP servers with annotation-based
 * configuration. It supports multiple server modes including STDIO, SSE (Server-Sent Events), and
 * STREAMABLE. The application automatically loads configuration, initializes reflection scanning,
 * and starts the appropriate server based on the configuration settings.
 *
 * @author codeboyzhou
 */
@SuppressWarnings("unused")
public final class McpApplication {

  public static final Logger log = LoggerFactory.getLogger(McpApplication.class);

  private McpApplication() {}

  /**
   * Runs the MCP application with the specified main class and command-line arguments.
   *
   * <p>This method initializes the reflection provider, loads the resource bundle, and starts the
   * MCP server based on the configuration settings.
   *
   * @param mainClass the main class of the application, used as the base for reflection scanning
   * @param args the command-line arguments passed to the application
   */
  @SuppressWarnings("unused")
  public static void run(Class<?> mainClass, String[] args) {
    log.info("Running {} with args: {}", mainClass.getSimpleName(), args);
    ReflectionsProvider.initializeReflectionsInstance(mainClass);
    ResourceBundleProvider.loadResourceBundle(mainClass);
    startMcpServer();
  }

  private static void startMcpServer() {
    McpConfigurationLoader configurationLoader = new McpConfigurationLoader();
    McpServerConfiguration configuration = configurationLoader.loadConfig();
    log.info("Starting MCP server with config: {}", JacksonHelper.toJsonString(configuration));
    if (configuration.enabled()) {
      McpServer mcpServer = null;
      switch (configuration.mode()) {
        case STDIO -> mcpServer = new McpStdioServer(configuration);
        case SSE -> mcpServer = new McpSseServer(configuration);
        case STREAMABLE -> mcpServer = new McpStreamableServer(configuration);
      }

      Objects.requireNonNull(mcpServer, "mcpServer must not be null");
      McpSyncServer mcpSyncServer = mcpServer.createSyncServer();
      mcpServer.registerComponents(mcpSyncServer);

      if (mcpServer instanceof McpSseServer sseServer) {
        sseServer.startHttpServer();
      } else if (mcpServer instanceof McpStreamableServer streamableServer) {
        streamableServer.startHttpServer();
      }
    } else {
      log.warn("MCP server is disabled, please check your configuration file.");
    }
  }
}
