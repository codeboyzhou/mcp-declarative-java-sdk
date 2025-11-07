package com.github.codeboyzhou.mcp.declarative.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.codeboyzhou.mcp.declarative.enums.ServerMode;
import com.github.codeboyzhou.mcp.declarative.enums.ServerType;
import com.github.codeboyzhou.mcp.declarative.util.StringHelper;

/**
 * This record represents the configuration of an MCP (Model Context Protocol) server.
 *
 * <p>It contains various properties such as enabled status, server mode, name, version, type,
 * instructions, request timeout, capabilities, change notification, SSE (Server-Sent Events), and
 * streamable configuration.
 *
 * @see <a href="https://codeboyzhou.github.io/mcp-declarative-java-sdk/getting-started">MCP
 *     Declarative Java SDK Documentation</a>
 * @author codeboyzhou
 */
public record McpServerConfiguration(
    @JsonProperty("profile") String profile,
    @JsonProperty("enabled") Boolean enabled,
    @JsonProperty("mode") ServerMode mode,
    @JsonProperty("name") String name,
    @JsonProperty("version") String version,
    @JsonProperty("type") ServerType type,
    @JsonProperty("instructions") String instructions,
    @JsonProperty("request-timeout") Long requestTimeout,
    @JsonProperty("capabilities") McpServerCapabilities capabilities,
    @JsonProperty("change-notification") McpServerChangeNotification changeNotification,
    @JsonProperty("sse") McpServerSSE sse,
    @JsonProperty("streamable") McpServerStreamable streamable) {

  /**
   * Creates a new instance of {@code Builder} to build {@code McpServerConfiguration}.
   *
   * @return A new instance of {@code Builder}.
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Returns the default configuration of the MCP server.
   *
   * <p>By default, the enabled status is {@code true}, the server mode is {@link
   * ServerMode#STREAMABLE}, the server name is "mcp-server", the server version is "1.0.0", the
   * server type is {@link ServerType#SYNC}, the server instructions is {@link StringHelper#EMPTY},
   * the request timeout is 20000 milliseconds, the capabilities is {@link
   * McpServerCapabilities#getDefault()}, the change notification is {@link
   * McpServerChangeNotification#getDefault()}, the SSE is {@link McpServerSSE#getDefault()}, and
   * the streamable is {@link McpServerStreamable#getDefault()}.
   *
   * @return The default configuration of the MCP server.
   */
  public static McpServerConfiguration getDefault() {
    return builder()
        .enabled(true)
        .mode(ServerMode.STREAMABLE)
        .name("mcp-server")
        .version("1.0.0")
        .type(ServerType.SYNC)
        .instructions(StringHelper.EMPTY)
        .requestTimeout(20000L)
        .capabilities(McpServerCapabilities.getDefault())
        .changeNotification(McpServerChangeNotification.getDefault())
        .sse(McpServerSSE.getDefault())
        .streamable(McpServerStreamable.getDefault())
        .build();
  }

  /** Builder class for {@code McpServerConfiguration}. */
  public static class Builder {
    /** The profile. */
    private String profile;

    /** The enabled status. */
    private Boolean enabled;

    /** The server mode. */
    private ServerMode mode;

    /** The server name. */
    private String name;

    /** The server version. */
    private String version;

    /** The server type. */
    private ServerType type;

    /** The server instructions. */
    private String instructions;

    /** The request timeout. */
    private Long requestTimeout;

    /** The server capabilities. */
    private McpServerCapabilities capabilities;

    /** The change notification configuration. */
    private McpServerChangeNotification changeNotification;

    /** The SSE configuration. */
    private McpServerSSE sse;

    /** The streamable configuration. */
    private McpServerStreamable streamable;

    /**
     * Sets the profile.
     *
     * @param profile The profile.
     * @return This builder instance.
     */
    public Builder profile(String profile) {
      this.profile = profile;
      return this;
    }

    /**
     * Sets the enabled status.
     *
     * @param enabled The enabled status.
     * @return This builder instance.
     */
    public Builder enabled(Boolean enabled) {
      this.enabled = enabled;
      return this;
    }

    /**
     * Sets the server mode.
     *
     * @param mode The server mode.
     * @return This builder instance.
     */
    public Builder mode(ServerMode mode) {
      this.mode = mode;
      return this;
    }

    /**
     * Sets the server name.
     *
     * @param name The server name.
     * @return This builder instance.
     */
    public Builder name(String name) {
      this.name = name;
      return this;
    }

    /**
     * Sets the server version.
     *
     * @param version The server version.
     * @return This builder instance.
     */
    public Builder version(String version) {
      this.version = version;
      return this;
    }

    /**
     * Sets the server type.
     *
     * @param type The server type.
     * @return This builder instance.
     */
    public Builder type(ServerType type) {
      this.type = type;
      return this;
    }

    /**
     * Sets the server instructions.
     *
     * @param instructions The server instructions.
     * @return This builder instance.
     */
    public Builder instructions(String instructions) {
      this.instructions = instructions;
      return this;
    }

    /**
     * Sets the request timeout.
     *
     * @param requestTimeout The request timeout.
     * @return This builder instance.
     */
    public Builder requestTimeout(Long requestTimeout) {
      this.requestTimeout = requestTimeout;
      return this;
    }

    /**
     * Sets the server capabilities.
     *
     * @param capabilities The server capabilities.
     * @return This builder instance.
     */
    public Builder capabilities(McpServerCapabilities capabilities) {
      this.capabilities = capabilities;
      return this;
    }

    /**
     * Sets the change notification configuration.
     *
     * @param changeNotification The change notification configuration.
     * @return This builder instance.
     */
    public Builder changeNotification(McpServerChangeNotification changeNotification) {
      this.changeNotification = changeNotification;
      return this;
    }

    /**
     * Sets the SSE configuration.
     *
     * @param sse The SSE configuration.
     * @return This builder instance.
     */
    public Builder sse(McpServerSSE sse) {
      this.sse = sse;
      return this;
    }

    /**
     * Sets the streamable configuration.
     *
     * @param streamable The streamable configuration.
     * @return This builder instance.
     */
    public Builder streamable(McpServerStreamable streamable) {
      this.streamable = streamable;
      return this;
    }

    /**
     * Builds a new instance of {@code McpServerConfiguration}.
     *
     * @return A new instance of {@code McpServerConfiguration}.
     */
    public McpServerConfiguration build() {
      return new McpServerConfiguration(
          profile,
          enabled,
          mode,
          name,
          version,
          type,
          instructions,
          requestTimeout,
          capabilities,
          changeNotification,
          sse,
          streamable);
    }
  }
}
