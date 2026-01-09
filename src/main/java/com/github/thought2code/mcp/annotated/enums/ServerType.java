package com.github.thought2code.mcp.annotated.enums;

/**
 * This enum represents the type of MCP (Model Context Protocol) server.
 *
 * <p>It can be either {@link #SYNC} or {@link #ASYNC}.
 *
 * @author codeboyzhou
 */
public enum ServerType {

  /** The MCP server runs in {@code SYNC} mode. */
  SYNC,

  /** The MCP server runs in {@code ASYNC} mode. */
  ASYNC
}
