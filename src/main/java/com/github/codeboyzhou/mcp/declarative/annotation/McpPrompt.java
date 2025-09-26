package com.github.codeboyzhou.mcp.declarative.annotation;

import com.github.codeboyzhou.mcp.declarative.util.StringHelper;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to mark a method as an MCP (Model Context Protocol) prompt method.
 *
 * <p>The prompt's name defaults to the name of the annotated method. Prompt metadata such as title
 * and description can be specified via the corresponding attributes. If omitted, these metadata
 * fields will default to the literal string "Not specified".
 *
 * @apiNote Example usage:
 *     <pre>{@code
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
  /** The name of the prompt. Defaults to the name of the annotated method. */
  String name() default StringHelper.EMPTY;

  /** The title of the prompt. Defaults to the literal string "Not specified". */
  String title() default StringHelper.EMPTY;

  /** The description of the prompt. Defaults to the literal string "Not specified". */
  String description() default StringHelper.EMPTY;
}
