package com.github.codeboyzhou.mcp.declarative.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

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
    @JsonProperty("port") Integer port) {}
