package com.github.codeboyzhou.mcp.declarative.server;

import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.transport.HttpServletSseServerTransportProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used to create a new instance of {@link McpSseServer} based on the specified {@link
 * McpSseServerInfo} in HTTP SSE mode.
 *
 * @author codeboyzhou
 */
public class McpSseServer extends AbstractMcpServer<McpSseServerInfo> {

  private static final Logger log = LoggerFactory.getLogger(McpSseServer.class);

  /**
   * Returns the sync specification for the MCP server in HTTP SSE mode.
   *
   * <p>This method returns the sync specification for the MCP server in SSE mode. The sync
   * specification is used to start the MCP server in HTTP SSE mode.
   *
   * @param info the server info
   * @return the sync specification for the MCP server in SSE mode
   */
  @Override
  public McpServer.SyncSpecification<?> sync(McpSseServerInfo info) {
    log.warn("HTTP SSE mode has been deprecated, recommend to use Stream HTTP server instead.");
    HttpServletSseServerTransportProvider transportProvider =
        HttpServletSseServerTransportProvider.builder()
            .baseUrl(info.baseUrl())
            .sseEndpoint(info.sseEndpoint())
            .messageEndpoint(info.messageEndpoint())
            .build();
    EmbeddedJettyServer httpserver = new EmbeddedJettyServer();
    httpserver.use(transportProvider).bind(info.port()).start();
    return McpServer.sync(transportProvider);
  }
}
