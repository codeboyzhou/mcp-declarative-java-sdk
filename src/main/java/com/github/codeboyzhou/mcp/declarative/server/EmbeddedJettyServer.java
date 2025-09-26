package com.github.codeboyzhou.mcp.declarative.server;

import com.github.codeboyzhou.mcp.declarative.common.NamedThreadFactory;
import jakarta.servlet.http.HttpServlet;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Embedded Jetty HTTP server implementation.
 *
 * @author codeboyzhou
 */
public class EmbeddedJettyServer {

  private static final Logger log = LoggerFactory.getLogger(EmbeddedJettyServer.class);

  /** Default servlet context path. */
  private static final String DEFAULT_SERVLET_CONTEXT_PATH = "/";

  /** Default servlet path. */
  private static final String DEFAULT_SERVLET_PATH = "/*";

  /** Thread pool for Jetty HTTP server. */
  private final ExecutorService threadPool;

  /** Servlet to be registered in Jetty HTTP server. */
  private HttpServlet servlet;

  /** Port to bind Jetty HTTP server. */
  private int port;

  /** Constructor to initialize Jetty HTTP server with a single thread. */
  public EmbeddedJettyServer() {
    this.threadPool = Executors.newSingleThreadExecutor(new NamedThreadFactory("mcp-http-server"));
  }

  /**
   * Register a servlet to be handled by Jetty HTTP server.
   *
   * @param servlet the servlet to be registered
   * @return this server instance
   */
  public EmbeddedJettyServer use(HttpServlet servlet) {
    this.servlet = servlet;
    return this;
  }

  /**
   * Bind Jetty HTTP server to a specific port.
   *
   * @param port the port to bind the server to
   * @return this server instance
   */
  public EmbeddedJettyServer bind(int port) {
    this.port = port;
    return this;
  }

  /** Start Jetty HTTP server and bind it to the specified port. */
  public void start() {
    ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
    handler.setContextPath(DEFAULT_SERVLET_CONTEXT_PATH);

    ServletHolder servletHolder = new ServletHolder(servlet);
    handler.addServlet(servletHolder, DEFAULT_SERVLET_PATH);

    Server httpserver = new Server(port);
    httpserver.setHandler(handler);
    httpserver.setStopAtShutdown(true);
    httpserver.setStopTimeout(Duration.ofSeconds(5).toMillis());

    try {
      httpserver.start();
      addShutdownHook(httpserver);
      log.info("Embedded Jetty server started on http://127.0.0.1:{}", port);
    } catch (Exception e) {
      log.error("Error starting embedded Jetty server on http://127.0.0.1:{}", port, e);
    }

    threadPool.submit(() -> await(httpserver));
  }

  /**
   * Await for Jetty HTTP server to stop.
   *
   * @param httpserver the Jetty HTTP server instance
   */
  private void await(Server httpserver) {
    try {
      httpserver.join();
    } catch (InterruptedException e) {
      log.error("Error joining embedded Jetty server", e);
    }
  }

  /**
   * Add a shutdown hook to Jetty HTTP server to stop it when the JVM is shutting down.
   *
   * @param httpserver the Jetty HTTP server instance
   */
  private void addShutdownHook(Server httpserver) {
    Runnable runnable = () -> shutdown(httpserver);
    Thread shutdownHookThread = new Thread(runnable);
    Runtime.getRuntime().addShutdownHook(shutdownHookThread);
  }

  /**
   * Shutdown Jetty HTTP server and release resources.
   *
   * @param httpserver the Jetty HTTP server instance
   */
  private void shutdown(Server httpserver) {
    try {
      log.info("Shutting down embedded Jetty server");
      httpserver.stop();
      servlet.destroy();
      threadPool.shutdown();
    } catch (Exception e) {
      log.error("Error stopping Jetty server", e);
    }
  }
}
