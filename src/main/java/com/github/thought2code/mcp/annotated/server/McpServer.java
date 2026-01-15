package com.github.thought2code.mcp.annotated.server;

import io.modelcontextprotocol.spec.McpSchema;

/**
 * This interface is used to define the contract for an MCP server.
 *
 * @author codeboyzhou
 */
public interface McpServer {
  /**
   * Defines the server capabilities.
   *
   * @return the server capabilities
   */
  McpSchema.ServerCapabilities capabilities();

  /**
   * Defines the sync server specification.
   *
   * @return the sync server specification
   */
  io.modelcontextprotocol.server.McpServer.SyncSpecification<?> syncServer();

  /** Starts the server. */
  void start();
}
