package com.github.thought2code.mcp.annotated.server.configurable;

import com.github.thought2code.mcp.annotated.configuration.McpServerConfiguration;
import com.github.thought2code.mcp.annotated.configuration.McpServerStreamable;
import io.modelcontextprotocol.json.McpJsonMapper;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.transport.HttpServletStreamableServerTransportProvider;
import java.time.Duration;

/**
 * This class is used to create a new instance of {@link ConfigurableMcpStreamableServer} based on
 * the specified {@link McpServerConfiguration} in Streamable HTTP mode.
 *
 * @author codeboyzhou
 */
public class ConfigurableMcpStreamableServer
    extends HttpBasedConfigurableMcpServer<HttpServletStreamableServerTransportProvider> {

  /**
   * Creates a new instance of {@link ConfigurableMcpStreamableServer} with the specified
   * configuration.
   *
   * @param configuration the configuration for the MCP server
   */
  public ConfigurableMcpStreamableServer(McpServerConfiguration configuration) {
    super(configuration);
  }

  /**
   * Returns the sync specification for the MCP server in Streamable HTTP mode.
   *
   * <p>This method returns the sync specification for the MCP server in Streamable HTTP mode. The
   * sync specification is used to start the MCP server in Streamable HTTP mode.
   *
   * @return the sync specification for the MCP server in Streamable HTTP mode
   */
  @Override
  public McpServer.SyncSpecification<?> sync() {
    McpServerStreamable streamable = configuration.streamable();
    transportProvider =
        HttpServletStreamableServerTransportProvider.builder()
            .jsonMapper(McpJsonMapper.getDefault())
            .mcpEndpoint(streamable.mcpEndpoint())
            .disallowDelete(streamable.disallowDelete())
            .keepAliveInterval(Duration.ofMillis(streamable.keepAliveInterval()))
            .build();
    port = streamable.port();
    return McpServer.sync(transportProvider);
  }
}
