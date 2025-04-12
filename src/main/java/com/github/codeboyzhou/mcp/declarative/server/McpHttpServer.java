package com.github.codeboyzhou.mcp.declarative.server;

import com.github.codeboyzhou.mcp.declarative.listener.McpHttpServerStatusListener;
import io.modelcontextprotocol.server.transport.HttpServletSseServerTransportProvider;
import io.modelcontextprotocol.util.Assert;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class McpHttpServer<T> {

    private static final Logger logger = LoggerFactory.getLogger(McpHttpServer.class);

    private static final String DEFAULT_SERVLET_CONTEXT_PATH = "/";

    private static final String DEFAULT_SERVLET_PATH = "/*";

    private HttpServletSseServerTransportProvider transportProvider;

    private McpSseServerInfo serverInfo;

    private McpHttpServerStatusListener<T> statusListener;

    private T mcpServer;

    public McpHttpServer<T> with(HttpServletSseServerTransportProvider transportProvider) {
        Assert.notNull(transportProvider, "transportProvider cannot be null");
        this.transportProvider = transportProvider;
        return this;
    }

    public McpHttpServer<T> with(McpSseServerInfo serverInfo) {
        Assert.notNull(serverInfo, "serverInfo cannot be null");
        this.serverInfo = serverInfo;
        return this;
    }

    public McpHttpServer<T> with(McpHttpServerStatusListener<T> statusListener) {
        Assert.notNull(statusListener, "statusListener cannot be null");
        this.statusListener = statusListener;
        return this;
    }

    public McpHttpServer<T> attach(T mcpServer) {
        Assert.notNull(mcpServer, "mcpServer cannot be null");
        this.mcpServer = mcpServer;
        return this;
    }

    public void start() {
        ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        handler.setContextPath(DEFAULT_SERVLET_CONTEXT_PATH);

        ServletHolder servletHolder = new ServletHolder(transportProvider);
        handler.addServlet(servletHolder, DEFAULT_SERVLET_PATH);

        Server httpserver = new Server(serverInfo.port());
        httpserver.setHandler(handler);
        httpserver.setStopAtShutdown(true);
        httpserver.setStopTimeout(Duration.ofSeconds(5).getSeconds());

        try {
            httpserver.start();
            logger.info("Jetty-based HTTP server started on http://127.0.0.1:{}", serverInfo.port());

            // Notify the listener that the server has started
            statusListener.onStarted(mcpServer);

            // Add a shutdown hook to stop the HTTP server and MCP server gracefully
            addShutdownHook(httpserver);

            // Check if the server is running in test mode
            final boolean testing = Boolean.parseBoolean(System.getProperty("mcp.declarative.java.sdk.testing"));
            if (testing) {
                logger.debug("Jetty-based HTTP server is running in test mode, not waiting for HTTP server to stop");
                return;
            }

            // Wait for the HTTP server to stop
            httpserver.join();
        } catch (Exception e) {
            logger.error("Error starting HTTP server on http://127.0.0.1:{}", serverInfo.port(), e);
            statusListener.onError(mcpServer, e);
        }
    }

    private void addShutdownHook(Server httpserver) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                logger.info("Shutting down HTTP server and MCP server");
                httpserver.stop();
                statusListener.onStopped(mcpServer);
            } catch (Exception e) {
                logger.error("Error stopping HTTP server and MCP server", e);
            }
        }));
    }

}
