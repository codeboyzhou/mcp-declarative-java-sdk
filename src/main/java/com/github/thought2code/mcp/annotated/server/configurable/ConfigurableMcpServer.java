package com.github.thought2code.mcp.annotated.server.configurable;

import com.github.thought2code.mcp.annotated.configuration.McpConfigurationLoader;
import io.modelcontextprotocol.server.McpServer;

/**
 * This interface represents a configurable MCP (Model Context Protocol) server.
 *
 * <p>A configurable MCP server can use the {@link McpConfigurationLoader} to load its configuration
 * from a YAML file.
 *
 * @author codeboyzhou
 */
public interface ConfigurableMcpServer {
  /**
   * Returns the sync specification for the MCP server.
   *
   * @return the sync specification for the MCP server
   */
  McpServer.SyncSpecification<?> sync();
}
