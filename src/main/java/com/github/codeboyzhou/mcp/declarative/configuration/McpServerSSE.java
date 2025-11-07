package com.github.codeboyzhou.mcp.declarative.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.codeboyzhou.mcp.declarative.util.StringHelper;

/**
 * This record represents the Server-Sent Events (SSE) configuration for an MCP (Model Context
 * Protocol) server.
 *
 * <p>It contains properties such as the message endpoint, endpoint, base URL, and port.
 *
 * @see <a href="https://codeboyzhou.github.io/mcp-declarative-java-sdk/getting-started">MCP
 *     Declarative Java SDK Documentation</a>
 * @author codeboyzhou
 */
public record McpServerSSE(
    @JsonProperty("message-endpoint") String messageEndpoint,
    @JsonProperty("endpoint") String endpoint,
    @JsonProperty("base-url") String baseUrl,
    @JsonProperty("port") Integer port) {

  /**
   * Creates a new instance of {@code Builder} to build {@code McpServerSSE}.
   *
   * @return A new instance of {@code Builder}.
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Returns the default SSE configuration of the MCP server.
   *
   * <p>By default, the message endpoint is "/mcp/message", the endpoint is "/sse", the base URL is
   * {@link StringHelper#EMPTY}, and the port is 8080.
   *
   * @return The default SSE configuration of the MCP server.
   */
  public static McpServerSSE getDefault() {
    return builder()
        .messageEndpoint("/mcp/message")
        .endpoint("/sse")
        .baseUrl(StringHelper.EMPTY)
        .port(8080)
        .build();
  }

  /** Builder class for {@code McpServerSSE}. */
  public static class Builder {
    /** The message endpoint. */
    private String messageEndpoint;

    /** The endpoint. */
    private String endpoint;

    /** The base URL. */
    private String baseUrl;

    /** The port. */
    private Integer port;

    /**
     * Sets the message endpoint.
     *
     * @param messageEndpoint The message endpoint.
     * @return This builder instance.
     */
    public Builder messageEndpoint(String messageEndpoint) {
      this.messageEndpoint = messageEndpoint;
      return this;
    }

    /**
     * Sets the endpoint.
     *
     * @param endpoint The endpoint.
     * @return This builder instance.
     */
    public Builder endpoint(String endpoint) {
      this.endpoint = endpoint;
      return this;
    }

    /**
     * Sets the base URL.
     *
     * @param baseUrl The base URL.
     * @return This builder instance.
     */
    public Builder baseUrl(String baseUrl) {
      this.baseUrl = baseUrl;
      return this;
    }

    /**
     * Sets the port.
     *
     * @param port The port.
     * @return This builder instance.
     */
    public Builder port(Integer port) {
      this.port = port;
      return this;
    }

    /**
     * Builds an instance of {@code McpServerSSE} with the configured values.
     *
     * @return A new instance of {@code McpServerSSE}.
     */
    public McpServerSSE build() {
      return new McpServerSSE(messageEndpoint, endpoint, baseUrl, port);
    }
  }
}
