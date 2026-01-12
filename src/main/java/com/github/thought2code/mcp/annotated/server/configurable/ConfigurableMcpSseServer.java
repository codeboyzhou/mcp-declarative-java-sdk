package com.github.thought2code.mcp.annotated.server.configurable;

import com.github.thought2code.mcp.annotated.configuration.McpServerConfiguration;
import com.github.thought2code.mcp.annotated.configuration.McpServerSSE;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.transport.HttpServletSseServerTransportProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used to create a new instance of {@link ConfigurableMcpSseServer} based on the
 * specified {@link McpServerConfiguration} in HTTP SSE mode.
 *
 * @author codeboyzhou
 */
public class ConfigurableMcpSseServer
    extends HttpBasedConfigurableMcpServer<HttpServletSseServerTransportProvider> {

  private static final Logger log = LoggerFactory.getLogger(ConfigurableMcpSseServer.class);

  /**
   * Creates a new instance of {@link ConfigurableMcpSseServer} with the specified configuration.
   *
   * @param configuration the configuration for the MCP server
   */
  public ConfigurableMcpSseServer(McpServerConfiguration configuration) {
    super(configuration);
  }

  /**
   * Returns the sync specification for the MCP server in HTTP SSE mode.
   *
   * <p>This method returns the sync specification for the MCP server in HTTP SSE mode. The sync
   * specification is used to start the MCP server in HTTP SSE mode.
   *
   * @return the sync specification for the MCP server in HTTP SSE mode
   */
  @Override
  public McpServer.SyncSpecification<?> sync() {
    log.warn("HTTP SSE mode has been deprecated, recommend to use Stream HTTP server instead.");
    McpServerSSE sse = configuration.sse();
    transportProvider =
        HttpServletSseServerTransportProvider.builder()
            .baseUrl(sse.baseUrl())
            .sseEndpoint(sse.endpoint())
            .messageEndpoint(sse.messageEndpoint())
            .build();
    port = sse.port();
    return McpServer.sync(transportProvider);
  }
}
