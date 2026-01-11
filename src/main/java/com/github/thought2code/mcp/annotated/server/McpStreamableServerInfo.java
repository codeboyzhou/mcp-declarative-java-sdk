package com.github.thought2code.mcp.annotated.server;

import io.modelcontextprotocol.common.McpTransportContext;
import io.modelcontextprotocol.server.McpTransportContextExtractor;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;

/**
 * This class is used to define the information of the MCP server with streamable http support.
 *
 * @see McpServerInfo
 * @author codeboyzhou
 */
@Deprecated(since = "0.11.0", forRemoval = true)
public class McpStreamableServerInfo extends McpServerInfo {

  /** The port of the MCP HTTP server. */
  private final int port;

  /** The endpoint of the MCP server to send messages. */
  private final String mcpEndpoint;

  /** Whether to disallow http delete requests. */
  private final boolean disallowDelete;

  /** The extractor to extract the transport context from the HTTP request. */
  private final McpTransportContextExtractor<HttpServletRequest> contextExtractor;

  /** The interval to send keep-alive messages. */
  private final Duration keepAliveInterval;

  /**
   * Constructs a new {@code McpStreamableServerInfo} instance with the specified builder.
   *
   * @param builder the builder to construct the instance
   */
  private McpStreamableServerInfo(McpStreamableServerInfo.Builder builder) {
    super(builder);
    this.port = builder.port;
    this.mcpEndpoint = builder.mcpEndpoint;
    this.disallowDelete = builder.disallowDelete;
    this.contextExtractor = builder.contextExtractor;
    this.keepAliveInterval = builder.keepAliveInterval;
  }

  /**
   * Returns a new builder instance to construct a {@code McpStreamableServerInfo} instance.
   *
   * @return a new builder instance
   */
  public static McpStreamableServerInfo.Builder builder() {
    return new McpStreamableServerInfo.Builder();
  }

  /**
   * Returns the port of the MCP HTTP server.
   *
   * @return the port of the MCP HTTP server
   */
  public int port() {
    return port;
  }

  /**
   * Returns the endpoint of the MCP server to send messages.
   *
   * @return the endpoint of the MCP server to send messages
   */
  public String mcpEndpoint() {
    return mcpEndpoint;
  }

  /**
   * Returns whether to disallow http delete requests.
   *
   * @return whether to disallow http delete requests
   */
  public boolean disallowDelete() {
    return disallowDelete;
  }

  /**
   * Returns the extractor to extract the transport context from the HTTP request.
   *
   * @return the extractor to extract the transport context from the HTTP request
   */
  public McpTransportContextExtractor<HttpServletRequest> contextExtractor() {
    return contextExtractor;
  }

  /**
   * Returns the interval to send keep-alive messages.
   *
   * @return the interval to send keep-alive messages
   */
  public Duration keepAliveInterval() {
    return keepAliveInterval;
  }

  /**
   * The builder class for {@code McpStreamableServerInfo}.
   *
   * @see McpStreamableServerInfo
   */
  @Deprecated(since = "0.11.0", forRemoval = true)
  public static class Builder extends McpServerInfo.Builder<McpStreamableServerInfo.Builder> {

    /** The port of the MCP HTTP server. Default value is {@code 8080}. */
    private int port = 8080;

    /** The endpoint of the MCP server to send messages. Default value is {@code /mcp}. */
    private String mcpEndpoint = "/mcp";

    /** Whether to disallow http delete requests. Default value is {@code false}. */
    private boolean disallowDelete = false;

    /**
     * The extractor to extract the transport context from the HTTP request. Default value is {@code
     * request -> McpTransportContext.EMPTY}.
     */
    private McpTransportContextExtractor<HttpServletRequest> contextExtractor =
        request -> McpTransportContext.EMPTY;

    /**
     * The interval to send keep-alive messages. Default value is {@code null}, which means no
     * keep-alive messages will be sent.
     */
    private Duration keepAliveInterval;

    /**
     * Returns the self reference of the builder, which is used to chain the method calls.
     *
     * @return the self reference of the builder
     */
    @Override
    protected McpStreamableServerInfo.Builder self() {
      return this;
    }

    /**
     * Builds a new {@code McpStreamableServerInfo} instance with the specified builder.
     *
     * @return a new {@code McpStreamableServerInfo} instance
     */
    @Override
    public McpStreamableServerInfo build() {
      return new McpStreamableServerInfo(this);
    }

    /**
     * Sets the port of the MCP HTTP server.
     *
     * @param port the port of the MCP HTTP server
     * @return the self reference of the builder
     */
    public McpStreamableServerInfo.Builder port(int port) {
      this.port = port;
      return self();
    }

    /**
     * Sets the endpoint of the MCP server to send messages.
     *
     * @param mcpEndpoint the endpoint of the MCP server to send messages
     * @return the self reference of the builder
     */
    public McpStreamableServerInfo.Builder mcpEndpoint(String mcpEndpoint) {
      this.mcpEndpoint = mcpEndpoint;
      return self();
    }

    /**
     * Sets whether to disallow http delete requests.
     *
     * @param disallowDelete whether to disallow http delete requests
     * @return the self reference of the builder
     */
    public McpStreamableServerInfo.Builder disallowDelete(boolean disallowDelete) {
      this.disallowDelete = disallowDelete;
      return self();
    }

    /**
     * Sets the extractor to extract the transport context from the HTTP request.
     *
     * @param contextExtractor the extractor to extract the transport context from the HTTP request
     * @return the self reference of the builder
     */
    public McpStreamableServerInfo.Builder contextExtractor(
        McpTransportContextExtractor<HttpServletRequest> contextExtractor) {
      this.contextExtractor = contextExtractor;
      return self();
    }

    /**
     * Sets the interval to send keep-alive messages.
     *
     * @param keepAliveInterval the interval to send keep-alive messages
     * @return the self reference of the builder
     */
    public McpStreamableServerInfo.Builder keepAliveInterval(Duration keepAliveInterval) {
      this.keepAliveInterval = keepAliveInterval;
      return self();
    }
  }
}
