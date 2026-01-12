package com.github.thought2code.mcp.annotated.server.configurable;

import com.github.thought2code.mcp.annotated.configuration.McpServerConfiguration;
import com.github.thought2code.mcp.annotated.server.JettyHttpServer;
import jakarta.servlet.http.HttpServlet;

/**
 * Base class for configurable MCP servers that use HTTP as the transport protocol.
 *
 * @param <T> the type of MCP transport provider that will be used to handle requests
 */
public abstract class HttpBasedConfigurableMcpServer<T extends HttpServlet>
    extends AbstractConfigurableMcpServer {

  /** The MCP transport provider that will be used to handle requests. */
  protected T transportProvider;

  /** The port on which the MCP server will listen for requests. */
  protected int port;

  /**
   * Creates a new instance of {@code HttpBasedConfigurableMcpServer} with the specified
   * configuration.
   *
   * @param configuration the configuration for the MCP server
   */
  protected HttpBasedConfigurableMcpServer(McpServerConfiguration configuration) {
    super(configuration);
  }

  /**
   * Runs the MCP server.
   *
   * <p>This method starts the MCP server using the Jetty HTTP server.
   */
  public void run() {
    JettyHttpServer jettyHttpServer = new JettyHttpServer();
    jettyHttpServer.withTransportProvider(transportProvider).bind(port).start();
  }
}
