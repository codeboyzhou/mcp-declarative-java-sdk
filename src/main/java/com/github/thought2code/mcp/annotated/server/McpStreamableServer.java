package com.github.thought2code.mcp.annotated.server;

import com.github.thought2code.mcp.annotated.configuration.McpServerConfiguration;
import com.github.thought2code.mcp.annotated.configuration.McpServerStreamable;
import io.modelcontextprotocol.json.McpJsonMapper;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.transport.HttpServletStreamableServerTransportProvider;
import java.time.Duration;

/**
 * This class is used to create a new instance of {@link McpStreamableServer} in Streamable HTTP
 * mode.
 *
 * @author codeboyzhou
 */
public class McpStreamableServer extends AbstractMcpServer {
  /** The HTTP Streamable server transport provider used by this MCP server. */
  private HttpServletStreamableServerTransportProvider transportProvider;

  /** The port number used by this MCP server. */
  private int port;

  /**
   * Constructs a new {@link McpStreamableServer} with the specified configuration.
   *
   * @param configuration the server configuration
   */
  public McpStreamableServer(McpServerConfiguration configuration) {
    super(configuration);
  }

  @Override
  public McpServer.SyncSpecification<?> syncServer() {
    McpServerStreamable streamable = configuration.streamable();
    port = streamable.port();
    transportProvider =
        HttpServletStreamableServerTransportProvider.builder()
            .jsonMapper(McpJsonMapper.getDefault())
            .mcpEndpoint(streamable.mcpEndpoint())
            .disallowDelete(streamable.disallowDelete())
            .keepAliveInterval(Duration.ofMillis(streamable.keepAliveInterval()))
            .build();
    return McpServer.sync(transportProvider);
  }

  @Override
  public void start() {
    super.start();
    JettyHttpServer httpServer = new JettyHttpServer();
    httpServer.withTransportProvider(transportProvider).bind(port).start();
  }
}
