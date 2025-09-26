package com.github.codeboyzhou.mcp.declarative.annotation;

import com.github.codeboyzhou.mcp.declarative.util.StringHelper;
import io.modelcontextprotocol.spec.McpSchema;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to mark a method as an MCP (Model Context Protocol) resource method.
 *
 * <p>The resource's URI must be specified explicitly. Resource metadata such as name, title,
 * description, and MIME type can be specified via the corresponding attributes. If omitted, these
 * metadata fields will default to the literal string "Not specified" and "text/plain".
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * @McpResource(uri = "weather://forecast/{city}/{date}")
 * public String getWeather() {
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
public @interface McpResource {
  /**
   * The URI of the resource.
   *
   * @return the URI of the resource
   */
  String uri();

  /**
   * The name of the resource. Defaults to the name of the annotated method.
   *
   * @return the name of the resource
   */
  String name() default StringHelper.EMPTY;

  /**
   * The title of the resource. Defaults to the literal string "Not specified".
   *
   * @return the title of the resource
   */
  String title() default StringHelper.EMPTY;

  /**
   * The description of the resource. Defaults to the literal string "Not specified".
   *
   * @return the description of the resource
   */
  String description() default StringHelper.EMPTY;

  /**
   * The MIME type of the resource. Defaults to "text/plain".
   *
   * @return the MIME type of the resource
   */
  String mimeType() default "text/plain";

  /**
   * The roles required to access the resource. Defaults to {@link McpSchema.Role#ASSISTANT} and
   * {@link McpSchema.Role#USER}.
   *
   * @return the roles required to access the resource
   */
  McpSchema.Role[] roles() default {McpSchema.Role.ASSISTANT, McpSchema.Role.USER};

  /**
   * The priority of the resource. Defaults to 1.0.
   *
   * @return the priority of the resource
   */
  double priority() default 1.0;
}
