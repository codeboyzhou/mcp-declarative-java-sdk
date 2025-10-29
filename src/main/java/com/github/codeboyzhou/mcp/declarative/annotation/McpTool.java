package com.github.codeboyzhou.mcp.declarative.annotation;

import com.github.codeboyzhou.mcp.declarative.util.StringHelper;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to mark a method as an MCP (Model Context Protocol) tool method.
 *
 * <p>The tool's name defaults to the name of the annotated method. Tool metadata such as title and
 * description can be specified via the corresponding attributes. If omitted, these metadata fields
 * will default to the value of the {@code name} attribute.
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * @McpTool
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
public @interface McpTool {
  /**
   * The name of the tool. Defaults to the name of the annotated method.
   *
   * @return the name of the tool
   */
  String name() default StringHelper.EMPTY;

  /**
   * The title of the tool. Defaults to the value of the {@code name} attribute.
   *
   * @return the title of the tool
   */
  String title() default StringHelper.EMPTY;

  /**
   * The description of the tool. Defaults to the value of the {@code name} attribute.
   *
   * @return the description of the tool
   */
  String description() default StringHelper.EMPTY;
}
