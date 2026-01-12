package com.github.thought2code.mcp.annotated.server;

import jakarta.servlet.http.HttpServlet;

/**
 * Base class for HTTP-based MCP servers.
 *
 * @param <S> the type of MCP server info
 * @param <T> the type of MCP transport provider that will handle requests
 */
public abstract class HttpBasedMcpServer<S extends McpServerInfo, T extends HttpServlet>
    extends AbstractMcpServer<S> {

  /** The MCP transport provider that will be used to handle requests. */
  protected T transportProvider;

  /** The port on which the MCP server will listen for requests. */
  protected int port;

  /**
   * Runs the MCP server.
   *
   * <p>This method starts the MCP server using the Jetty HTTP server.
   */
  public void run() {
    JettyHttpServer jettyHttpServer = new JettyHttpServer();
    jettyHttpServer.use(transportProvider).bind(port).start();
  }
}
