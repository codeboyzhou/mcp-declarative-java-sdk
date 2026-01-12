package com.github.thought2code.mcp.annotated.server;

import io.modelcontextprotocol.json.McpJsonMapper;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.transport.HttpServletStreamableServerTransportProvider;

/**
 * This class is used to create a new instance of {@link McpStreamableServer} based on the specified
 * {@link McpStreamableServerInfo} in Streamable HTTP mode.
 *
 * @author codeboyzhou
 */
public class McpStreamableServer
    extends HttpBasedMcpServer<
        McpStreamableServerInfo, HttpServletStreamableServerTransportProvider> {
  /**
   * Returns the sync specification for the MCP server in Streamable HTTP mode.
   *
   * <p>This method returns the sync specification for the MCP server in Streamable HTTP mode. The
   * sync specification is used to start the MCP server in Streamable HTTP mode.
   *
   * @param info the server info
   * @return the sync specification for the MCP server in Streamable HTTP mode
   */
  @Override
  public McpServer.SyncSpecification<?> sync(McpStreamableServerInfo info) {
    transportProvider =
        HttpServletStreamableServerTransportProvider.builder()
            .jsonMapper(McpJsonMapper.getDefault())
            .mcpEndpoint(info.mcpEndpoint())
            .disallowDelete(info.disallowDelete())
            .contextExtractor(info.contextExtractor())
            .keepAliveInterval(info.keepAliveInterval())
            .build();
    port = info.port();
    return McpServer.sync(transportProvider);
  }
}
