package com.github.thought2code.mcp.annotated.server.component;

import java.lang.reflect.Method;

/**
 * This interface represents an MCP server component (resource/prompt/tool) that is responsible for
 * creating instances of a specific type {@code T} for a given method.
 *
 * @param <T> the type of the component
 * @author codeboyzhou
 */
public interface McpServerComponent<T> {
  /**
   * Creates an instance of the component for the specified method.
   *
   * @param method the method for which to create an instance
   * @return an instance of the component for the specified method
   */
  T create(Method method);
}
