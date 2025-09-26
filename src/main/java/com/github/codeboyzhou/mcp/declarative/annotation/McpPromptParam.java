package com.github.codeboyzhou.mcp.declarative.annotation;

import com.github.codeboyzhou.mcp.declarative.util.StringHelper;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to mark a method parameter as an MCP (Model Context Protocol) prompt
 * parameter.
 *
 * <p>The parameter's name must be specified explicitly. Parameter metadata such as title,
 * description, and required can be specified via the corresponding attributes. If omitted, these
 * metadata fields will default to the literal string "Not specified" and {@code false}.
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * @McpPrompt
 * public String getWeather(@McpPromptParam(name = "city") String city) {
 *     // Method implementation...
 * }
 * }</pre>
 *
 * @see <a
 *     href="https://modelcontextprotocol.io/docs/learn/server-concepts#core-server-features">MCP
 *     Protocol Documentation</a>
 * @author codeboyzhou
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface McpPromptParam {
  /**
   * The name of the prompt parameter.
   *
   * @return the name of the prompt parameter
   */
  String name();

  /**
   * The title of the prompt parameter. Defaults to the literal string "Not specified".
   *
   * @return the title of the prompt parameter
   */
  String title() default StringHelper.EMPTY;

  /**
   * The description of the prompt parameter. Defaults to the literal string "Not specified".
   *
   * @return the description of the prompt parameter
   */
  String description() default StringHelper.EMPTY;

  /**
   * Whether the prompt parameter is required. Defaults to {@code false}.
   *
   * @return whether the prompt parameter is required
   */
  boolean required() default false;
}
