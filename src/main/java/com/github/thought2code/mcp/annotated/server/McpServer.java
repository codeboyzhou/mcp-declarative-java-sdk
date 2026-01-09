package com.github.thought2code.mcp.annotated.server;

/**
 * This interface is used to define the contract for an MCP server.
 *
 * @author codeboyzhou
 */
public interface McpServer<S extends McpServerInfo> {
  /**
   * Returns the sync specification for the MCP server.
   *
   * @param serverInfo the server info
   * @return the sync specification for the MCP server
   */
  io.modelcontextprotocol.server.McpServer.SyncSpecification<?> sync(S serverInfo);
}
