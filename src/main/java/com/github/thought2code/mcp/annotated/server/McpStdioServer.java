package com.github.thought2code.mcp.annotated.server;

import com.github.thought2code.mcp.annotated.configuration.McpServerConfiguration;
import io.modelcontextprotocol.json.McpJsonMapper;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;

/**
 * This class is used to create a new instance of {@link McpStdioServer} in STDIO mode.
 *
 * @author codeboyzhou
 */
public class McpStdioServer extends AbstractMcpServer {
  /**
   * Constructs a new {@link McpStdioServer} with the specified configuration.
   *
   * @param configuration the server configuration
   */
  public McpStdioServer(McpServerConfiguration configuration) {
    super(configuration);
  }

  @Override
  public McpServer.SyncSpecification<?> syncServer() {
    return McpServer.sync(new StdioServerTransportProvider(McpJsonMapper.getDefault()));
  }
}
