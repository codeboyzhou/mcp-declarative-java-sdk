package com.github.thought2code.mcp.annotated.server;

import jakarta.servlet.http.HttpServlet;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple Jetty HTTP server implementation.
 *
 * @author codeboyzhou
 */
public class JettyHttpServer {

  private static final Logger log = LoggerFactory.getLogger(JettyHttpServer.class);

  /** Jetty thread pool name. */
  private static final String JETTY_THREAD_POOL_NAME = "jetty-based-mcp-server-worker";

  /** Default context path. */
  private static final String DEFAULT_CONTEXT_PATH = "/";

  /** Default servlet path. */
  private static final String DEFAULT_SERVLET_PATH = "/*";

  /** MCP transport provider to be registered in Jetty HTTP server. */
  private HttpServlet mcpTransportProvider;

  /** Port to bind Jetty HTTP server. */
  private int port = 8080;

  /** Whether Jetty HTTP server is started. */
  private boolean started = false;

  /**
   * Register a servlet to be handled by Jetty HTTP server.
   *
   * @param mcpTransportProvider the MCP transport provider to be registered
   * @return this server instance
   */
  public JettyHttpServer withTransportProvider(HttpServlet mcpTransportProvider) {
    this.mcpTransportProvider = mcpTransportProvider;
    return this;
  }

  /**
   * Bind Jetty HTTP server to a specific port.
   *
   * @param port the port to bind the server to
   * @return this server instance
   */
  public JettyHttpServer bind(int port) {
    if (port <= 0 || port > 65535) {
      throw new IllegalArgumentException("Port must be between 1 and 65535");
    }
    this.port = port;
    return this;
  }

  /** Start Jetty HTTP server and bind it to the specified port. */
  public void start() {
    if (started) {
      log.warn("Jetty HTTP server is already started");
      return;
    }

    Server server = createServer();

    try {
      server.start();
      started = true;
      log.info("Jetty-based MCP server started on http://127.0.0.1:{}", port);
    } catch (Exception e) {
      log.error("Error starting Jetty-based MCP server on http://127.0.0.1:{}", port, e);
    }

    final boolean isTesting = Boolean.parseBoolean(System.getProperty("mcp.server.testing"));
    if (isTesting) {
      log.debug("Testing Jetty-based MCP server, not awaiting for server to stop");
      return;
    }

    await(server);
  }

  /**
   * Create a Jetty HTTP server instance.
   *
   * @return the Jetty HTTP server instance
   */
  private Server createServer() {
    QueuedThreadPool threadPool = new QueuedThreadPool();
    threadPool.setName(JETTY_THREAD_POOL_NAME);

    ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
    handler.setContextPath(DEFAULT_CONTEXT_PATH);

    ServletHolder servletHolder = new ServletHolder(mcpTransportProvider);
    handler.addServlet(servletHolder, DEFAULT_SERVLET_PATH);

    Server server = new Server(threadPool);
    server.setHandler(handler);
    server.setStopAtShutdown(true);

    ServerConnector connector = new ServerConnector(server);
    connector.setPort(port);
    server.addConnector(connector);

    return server;
  }

  /**
   * Await for Jetty HTTP server to stop.
   *
   * @param server the Jetty HTTP server instance
   */
  private void await(Server server) {
    try {
      server.join();
    } catch (InterruptedException e) {
      log.error("Error joining Jetty-based MCP server", e);
    }
  }
}
