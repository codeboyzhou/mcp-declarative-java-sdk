package com.github.thought2code.mcp.annotated.server.component;

import java.lang.reflect.Method;

/**
 * Interface representing an MCP server component (resource/prompt/tool) that is responsible for
 * creating instances of a specific type {@code T} for a given method.
 *
 * <p>This interface defines the contract for MCP server components that can create and register
 * different types of server components based on annotated methods. Implementations of this
 * interface handle the creation of specific component types (resources, prompts, tools) from
 * methods annotated with appropriate MCP annotations.
 *
 * <p>Typical usage involves:
 *
 * <ul>
 *   <li>Creating a component instance from a method using {@link #from(Method)}
 *   <li>Registering the component with the server using {@link #register()}
 * </ul>
 *
 * @param <T> the type of component this interface creates (e.g., McpSchema.Resource,
 *     McpSchema.Prompt, McpSchema.Tool)
 * @author codeboyzhou
 * @see McpServerResource
 * @see McpServerPrompt
 * @see McpServerTool
 */
public interface McpServerComponent<T> {
  /**
   * Creates a component instance of type {@code T} from the specified method.
   *
   * <p>This method processes the annotated method and creates an appropriate component instance
   * based on the method's annotations, parameters, and return type. The exact type of component
   * created depends on the implementation of this interface.
   *
   * @param method the annotated method to create a component from
   * @return a component instance of type {@code T} created from the method
   */
  T from(Method method);

  /**
   * Registers all components of this type with the MCP server.
   *
   * <p>This method scans for methods annotated with the appropriate annotation(s) for this
   * component type and registers them with the server. The exact behavior depends on the
   * implementation, but typically involves reflection to discover annotated methods and
   * registration of created components with the server instance.
   */
  void register();
}
