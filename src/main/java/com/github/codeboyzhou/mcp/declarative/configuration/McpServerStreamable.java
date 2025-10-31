package com.github.codeboyzhou.mcp.declarative.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This record represents the streamable http server configuration for an MCP (Model Context
 * Protocol) server.
 *
 * <p>It contains properties such as the MCP endpoint, disallow delete flag, keep-alive interval,
 * and port.
 *
 * @see <a href="https://codeboyzhou.github.io/mcp-declarative-java-sdk/getting-started">MCP
 *     Declarative Java SDK Documentation</a>
 * @author codeboyzhou
 */
public record McpServerStreamable(
    @JsonProperty("mcp-endpoint") String mcpEndpoint,
    @JsonProperty("disallow-delete") Boolean disallowDelete,
    @JsonProperty("keep-alive-interval") Long keepAliveInterval,
    @JsonProperty("port") Integer port) {}
