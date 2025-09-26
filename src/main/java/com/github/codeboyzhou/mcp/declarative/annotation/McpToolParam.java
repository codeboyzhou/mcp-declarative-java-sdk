package com.github.codeboyzhou.mcp.declarative.annotation;

import com.github.codeboyzhou.mcp.declarative.util.StringHelper;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to mark a method parameter as an MCP (Model Context Protocol) tool
 * parameter.
 *
 * <p>The parameter's name must be specified explicitly. Parameter metadata such as description and
 * required status can be specified via the corresponding attributes. If omitted, these metadata
 * fields will default to the literal string "Not specified" and {@code false}.
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * @McpTool
 * public String getWeather(@McpToolParam(name = "city") String city) {
 *     // Method implementation...
 * }
 * }</pre>
 *
 * @author codeboyzhou
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface McpToolParam {
  /**
   * The name of the tool parameter.
   *
   * @return the name of the tool parameter
   */
  String name();

  /**
   * The description of the tool parameter. Defaults to the literal string "Not specified".
   *
   * @return the description of the tool parameter
   */
  String description() default StringHelper.EMPTY;

  /**
   * Whether the tool parameter is required. Defaults to {@code false}.
   *
   * @return whether the tool parameter is required
   */
  boolean required() default false;
}
