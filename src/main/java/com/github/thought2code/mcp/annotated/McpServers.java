package com.github.thought2code.mcp.annotated;

import com.github.thought2code.mcp.annotated.configuration.McpConfigurationLoader;
import com.github.thought2code.mcp.annotated.configuration.McpServerConfiguration;
import com.github.thought2code.mcp.annotated.enums.ServerMode;
import com.github.thought2code.mcp.annotated.reflect.ReflectionsProvider;
import com.github.thought2code.mcp.annotated.server.McpServer;
import com.github.thought2code.mcp.annotated.server.McpSseServer;
import com.github.thought2code.mcp.annotated.server.McpStdioServer;
import com.github.thought2code.mcp.annotated.server.McpStreamableServer;
import com.github.thought2code.mcp.annotated.server.component.ResourceBundleProvider;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class McpServers {

  private static final Logger log = LoggerFactory.getLogger(McpServers.class);

  private static final McpServers INSTANCE = new McpServers();

  private McpServers() {}

  public static McpServers run(Class<?> mainClass, String[] args) {
    ReflectionsProvider.initializeReflectionsInstance(mainClass);
    ResourceBundleProvider.loadResourceBundle(mainClass);
    return INSTANCE;
  }

  public void startStdioServer(McpServerConfiguration.Builder configuration) {
    configuration.enabled(true).mode(ServerMode.STDIO);
    doStartServer(configuration.build());
  }

  public void startSseServer(McpServerConfiguration.Builder configuration) {
    configuration.enabled(true).mode(ServerMode.SSE);
    doStartServer(configuration.build());
  }

  public void startStreamableServer(McpServerConfiguration.Builder configuration) {
    configuration.enabled(true).mode(ServerMode.STREAMABLE);
    doStartServer(configuration.build());
  }

  public void startServer(String configFileName) {
    Assert.notNull(configFileName, "configFileName must not be null");
    McpConfigurationLoader configLoader = new McpConfigurationLoader(configFileName);
    doStartServer(configLoader.loadConfig());
  }

  public void startServer() {
    McpConfigurationLoader configLoader = new McpConfigurationLoader();
    doStartServer(configLoader.loadConfig());
  }

  private void doStartServer(McpServerConfiguration configuration) {
    if (configuration.enabled()) {
      McpServer mcpServer = new McpStreamableServer(configuration);

      if (configuration.mode() == ServerMode.SSE) {
        mcpServer = new McpSseServer(configuration);
      } else if (configuration.mode() == ServerMode.STDIO) {
        mcpServer = new McpStdioServer(configuration);
      }

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
