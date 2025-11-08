package com.github.codeboyzhou.mcp.declarative.server.configurable;

import com.github.codeboyzhou.mcp.declarative.configuration.McpConfigurationLoader;
import com.github.codeboyzhou.mcp.declarative.configuration.McpServerCapabilities;
import com.github.codeboyzhou.mcp.declarative.configuration.McpServerChangeNotification;
import com.github.codeboyzhou.mcp.declarative.configuration.McpServerConfiguration;
import com.github.codeboyzhou.mcp.declarative.server.component.McpServerComponentRegister;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.spec.McpSchema;
import java.time.Duration;

/**
 * This abstract class represents a configurable MCP (Model Context Protocol) server.
 *
 * <p>A configurable MCP server can use the {@link McpConfigurationLoader} to load its configuration
 * from a YAML file.
 *
 * @author codeboyzhou
 */
public abstract class AbstractConfigurableMcpServer implements ConfigurableMcpServer {

  /** The configuration for the MCP server. */
  protected final McpServerConfiguration configuration;

  /**
   * Creates a new instance of {@code AbstractConfigurableMcpServer} with the specified
   * configuration.
   *
   * @param configuration the configuration for the MCP server
   */
  protected AbstractConfigurableMcpServer(McpServerConfiguration configuration) {
    this.configuration = configuration;
  }

  /**
   * Starts the MCP server.
   *
   * <p>This method starts the MCP server using the sync specification provided by {@link #sync()}.
   */
  public void startServer() {
    McpServerComponentRegister register = new McpServerComponentRegister();
    McpSyncServer server =
        sync()
            .serverInfo(configuration.name(), configuration.version())
            .capabilities(serverCapabilities())
            .instructions(configuration.instructions())
            .requestTimeout(Duration.ofMillis(configuration.requestTimeout()))
            .completions(register.registerCompletions())
            .build();
    register.registerComponents(server);
  }

  /**
   * Returns the server capabilities for the MCP server.
   *
   * @return the server capabilities for the MCP server
   */
  private McpSchema.ServerCapabilities serverCapabilities() {
    McpSchema.ServerCapabilities.Builder capabilities = McpSchema.ServerCapabilities.builder();
    McpServerCapabilities capabilitiesConfig = configuration.capabilities();
    McpServerChangeNotification serverChangeNotification = configuration.changeNotification();
    if (capabilitiesConfig.resource()) {
      final Boolean subscribe = capabilitiesConfig.subscribeResource();
      final Boolean changeNotification = serverChangeNotification.resource();
      capabilities.resources(subscribe, changeNotification);
    }
    if (capabilitiesConfig.prompt()) {
      capabilities.prompts(serverChangeNotification.prompt());
    }
    if (capabilitiesConfig.tool()) {
      capabilities.tools(serverChangeNotification.tool());
    }
    if (capabilitiesConfig.completion()) {
      capabilities.completions();
    }
    return capabilities.build();
  }
}
