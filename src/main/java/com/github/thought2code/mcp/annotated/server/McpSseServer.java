package com.github.thought2code.mcp.annotated.server;

import com.github.thought2code.mcp.annotated.configuration.McpServerConfiguration;
import com.github.thought2code.mcp.annotated.configuration.McpServerSSE;
import io.modelcontextprotocol.json.McpJsonMapper;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.transport.HttpServletSseServerTransportProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used to create a new instance of {@link McpSseServer} in HTTP SSE mode.
 *
 * @author codeboyzhou
 */
public class McpSseServer extends AbstractMcpServer {

  private static final Logger log = LoggerFactory.getLogger(McpSseServer.class);

  /** The HTTP SSE server transport provider used by this MCP server. */
  private HttpServletSseServerTransportProvider transportProvider;

  /** The port number used by this MCP server. */
  private int port;

  /**
   * Constructs a new {@link McpSseServer} with the specified configuration.
   *
   * @param configuration the server configuration
   */
  public McpSseServer(McpServerConfiguration configuration) {
    super(configuration);
  }

  @Override
  public McpServer.SyncSpecification<?> syncServer() {
    log.warn("HTTP SSE mode has been deprecated, recommend to use Stream HTTP server instead.");
    McpServerSSE sse = configuration.sse();
    port = sse.port();
    transportProvider =
        HttpServletSseServerTransportProvider.builder()
            .jsonMapper(McpJsonMapper.getDefault())
            .baseUrl(sse.baseUrl())
            .sseEndpoint(sse.endpoint())
            .messageEndpoint(sse.messageEndpoint())
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
