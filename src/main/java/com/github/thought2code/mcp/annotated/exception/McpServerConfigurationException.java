package com.github.thought2code.mcp.annotated.exception;

/**
 * This exception is thrown to indicate a configuration error in the MCP (Model Context Protocol)
 * server.
 *
 * @author codeboyzhou
 */
public class McpServerConfigurationException extends McpServerException {
  /**
   * Creates a new instance of {@code McpServerConfigurationException} with the specified detail
   * message.
   *
   * @param message the detail message
   */
  public McpServerConfigurationException(String message) {
    super(message);
  }

  /**
   * Creates a new instance of {@code McpServerConfigurationException} with the specified detail
   * message and cause.
   *
   * @param message the detail message
   * @param cause the cause of the exception
   */
  public McpServerConfigurationException(String message, Throwable cause) {
    super(message, cause);
  }
}
