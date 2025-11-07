package com.github.codeboyzhou.mcp.declarative.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This record represents the capabilities of an MCP (Model Context Protocol) server.
 *
 * @author codeboyzhou
 */
public record McpServerCapabilities(
    @JsonProperty("resource") Boolean resource,
    @JsonProperty("prompt") Boolean prompt,
    @JsonProperty("tool") Boolean tool,
    @JsonProperty("completion") Boolean completion) {

  /**
   * Creates a new instance of {@code McpServerCapabilities} with default values.
   *
   * <p>By default, all capabilities are set to {@code true}.
   */
  public McpServerCapabilities() {
    this(true, true, true, true);
  }
}
