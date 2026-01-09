package com.github.thought2code.mcp.annotated.exception;

/**
 * This exception is thrown to indicate a JSON processing error in the MCP (Model Context Protocol)
 * server.
 *
 * @author codeboyzhou
 */
public class McpServerJsonProcessingException extends McpServerException {
  /**
   * Creates a new instance of {@code McpServerJsonProcessingException} with the specified detail
   * message.
   *
   * @param message the detail message
   */
  public McpServerJsonProcessingException(String message) {
    super(message);
  }

  /**
   * Creates a new instance of {@code McpServerJsonProcessingException} with the specified detail
   * message and cause.
   *
   * @param message the detail message
   * @param cause the cause of the exception
   */
  public McpServerJsonProcessingException(String message, Throwable cause) {
    super(message, cause);
  }
}
