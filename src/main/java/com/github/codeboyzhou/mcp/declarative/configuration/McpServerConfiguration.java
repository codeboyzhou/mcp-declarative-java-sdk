package com.github.codeboyzhou.mcp.declarative.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.codeboyzhou.mcp.declarative.enums.ServerMode;
import com.github.codeboyzhou.mcp.declarative.enums.ServerType;

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
    @JsonProperty("streamable") McpServerStreamable streamable) {}
