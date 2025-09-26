package com.github.codeboyzhou.mcp.declarative.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This record represents a change notification for MCP (Model Context Protocol) server
 * capabilities.
 *
 * <p>It contains boolean flags indicating whether the server supports resource, prompt, and tool
 * change notification.
 *
 * @author codeboyzhou
 */
public record McpServerChangeNotification(
    @JsonProperty("resource") boolean resource,
    @JsonProperty("prompt") boolean prompt,
    @JsonProperty("tool") boolean tool) {

  /**
   * Creates a new instance of {@code McpServerChangeNotification} with default values.
   *
   * <p>By default, all change notification flags are set to {@code true}.
   */
  public McpServerChangeNotification() {
    this(true, true, true);
  }
}
