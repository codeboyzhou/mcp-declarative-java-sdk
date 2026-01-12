package com.github.thought2code.mcp.annotated.server;

import jakarta.servlet.http.HttpServlet;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple Jetty HTTP server implementation.
 *
 * @author codeboyzhou
 */
public class JettyHttpServer {

  private static final Logger log = LoggerFactory.getLogger(JettyHttpServer.class);

  /** Jetty server name. */
  private static final String JETTY_SERVER_NAME = "jetty-based-mcp-server";

  /** Default servlet context path. */
  private static final String DEFAULT_SERVLET_CONTEXT_PATH = "/";

  /** Default servlet path. */
  private static final String DEFAULT_SERVLET_PATH = "/*";

  /** MCP transport provider to be registered in Jetty HTTP server. */
  private HttpServlet mcpTransportProvider;

  /** Port to bind Jetty HTTP server. */
  private int port;

  /**
   * Register a servlet to be handled by Jetty HTTP server.
   *
   * @param mcpTransportProvider the MCP transport provider to be registered
   * @return this server instance
   */
  public JettyHttpServer use(HttpServlet mcpTransportProvider) {
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
    this.port = port;
    return this;
  }

  /** Start Jetty HTTP server and bind it to the specified port. */
  public void start() {
    ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
    handler.setContextPath(DEFAULT_SERVLET_CONTEXT_PATH);

    ServletHolder servletHolder = new ServletHolder(mcpTransportProvider);
    handler.addServlet(servletHolder, DEFAULT_SERVLET_PATH);

    Server server = new Server(port);
    server.setHandler(handler);
    server.setStopAtShutdown(true);
    server.setName(JETTY_SERVER_NAME);

    try {
      server.start();
      addShutdownHook(server);
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

  /**
   * Add a shutdown hook to Jetty HTTP server to stop it when the JVM is shutting down.
   *
   * @param server the Jetty HTTP server instance
   */
  private void addShutdownHook(Server server) {
    Runnable runnable = () -> shutdown(server);
    Thread shutdownHookThread = new Thread(runnable);
    Runtime.getRuntime().addShutdownHook(shutdownHookThread);
  }

  /**
   * Shutdown Jetty HTTP server and release resources.
   *
   * @param server the Jetty HTTP server instance
   */
  private void shutdown(Server server) {
    try {
      log.info("Shutting down Jetty-based MCP server");
      server.stop();
    } catch (Exception e) {
      log.error("Error stopping Jetty-based MCP server", e);
    }
  }
}
