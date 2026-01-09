package com.github.thought2code.mcp.annotated.annotation;

import com.github.thought2code.mcp.annotated.util.StringHelper;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to mark a method as an MCP (Model Context Protocol) prompt method.
 *
 * <p>The prompt's name defaults to the name of the annotated method. Prompt metadata such as title
 * and description can be specified via the corresponding attributes. If omitted, these metadata
 * fields will default to the value of the {@code name} attribute.
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * @McpPrompt
 * public String getWeather(String city) {
 *     // Method implementation...
 * }
 * }</pre>
 *
 * @see <a
 *     href="https://modelcontextprotocol.io/docs/learn/server-concepts#core-server-features">MCP
 *     Protocol Documentation</a>
 * @author codeboyzhou
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface McpPrompt {
  /**
   * The name of the prompt. Defaults to the name of the annotated method.
   *
   * @return the name of the prompt
   */
  String name() default StringHelper.EMPTY;

  /**
   * The title of the prompt. Defaults to the value of the {@code name} attribute.
   *
   * @return the title of the prompt
   */
  String title() default StringHelper.EMPTY;

  /**
   * The description of the prompt. Defaults to the value of the {@code name} attribute.
   *
   * @return the description of the prompt
   */
  String description() default StringHelper.EMPTY;
}
