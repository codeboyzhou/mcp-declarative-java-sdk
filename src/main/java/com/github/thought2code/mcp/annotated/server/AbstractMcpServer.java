package com.github.thought2code.mcp.annotated.server;

import com.github.thought2code.mcp.annotated.configuration.McpServerCapabilities;
import com.github.thought2code.mcp.annotated.configuration.McpServerChangeNotification;
import com.github.thought2code.mcp.annotated.configuration.McpServerConfiguration;
import com.github.thought2code.mcp.annotated.server.component.McpServerComponentRegister;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.spec.McpSchema;
import java.time.Duration;

/**
 * This abstract class is used to provide a base implementation for an MCP server.
 *
 * @author codeboyzhou
 */
public abstract class AbstractMcpServer implements McpServer {
  /** The server configuration used by this MCP server. */
  protected final McpServerConfiguration configuration;

  /** The component register used by this MCP server. */
  protected final McpServerComponentRegister componentRegister;

  /**
   * Constructs a new {@link AbstractMcpServer} with the specified configuration.
   *
   * @param configuration the server configuration
   */
  public AbstractMcpServer(McpServerConfiguration configuration) {
    this.configuration = configuration;
    this.componentRegister = new McpServerComponentRegister();
  }

  @Override
  public McpSchema.ServerCapabilities capabilities() {
    McpServerCapabilities capabilitiesConfig = configuration.capabilities();
    McpServerChangeNotification serverChangeNotification = configuration.changeNotification();

    McpSchema.ServerCapabilities.Builder capabilities = McpSchema.ServerCapabilities.builder();

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

  @Override
  public void start() {
    McpSyncServer server =
        syncServer()
            .serverInfo(configuration.name(), configuration.version())
            .capabilities(capabilities())
            .instructions(configuration.instructions())
            .requestTimeout(Duration.ofMillis(configuration.requestTimeout()))
            .completions(componentRegister.registerCompletions())
            .build();
    componentRegister.registerComponents(server);
  }
}
