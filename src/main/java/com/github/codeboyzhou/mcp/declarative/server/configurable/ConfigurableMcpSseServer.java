package com.github.codeboyzhou.mcp.declarative.server.configurable;

import com.github.codeboyzhou.mcp.declarative.configuration.McpServerConfiguration;
import com.github.codeboyzhou.mcp.declarative.configuration.McpServerSSE;
import com.github.codeboyzhou.mcp.declarative.server.EmbeddedJettyServer;
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
public class ConfigurableMcpSseServer extends AbstractConfigurableMcpServer {

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
    HttpServletSseServerTransportProvider transportProvider =
        HttpServletSseServerTransportProvider.builder()
            .baseUrl(sse.baseUrl())
            .sseEndpoint(sse.endpoint())
            .messageEndpoint(sse.messageEndpoint())
            .build();
    EmbeddedJettyServer httpserver = new EmbeddedJettyServer();
    httpserver.use(transportProvider).bind(sse.port()).start();
    return McpServer.sync(transportProvider);
  }
}
