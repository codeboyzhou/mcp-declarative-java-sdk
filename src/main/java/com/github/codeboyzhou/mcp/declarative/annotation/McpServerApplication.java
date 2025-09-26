package com.github.codeboyzhou.mcp.declarative.annotation;

import com.github.codeboyzhou.mcp.declarative.util.StringHelper;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to mark a class as an MCP (Model Context Protocol) server application.
 *
 * <p>The base package for component scanning can be specified via the {@code basePackage}
 * attribute. If not specified, the package of the annotated class will be used.
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * @McpServerApplication(basePackage = "com.example.mcp")
 * public class MyMcpServerApplication {
 *     // Application logic...
 * }
 * }</pre>
 *
 * @see <a href="https://modelcontextprotocol.io/docs/learn/server-concepts">MCP Protocol
 *     Documentation</a>
 * @author codeboyzhou
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface McpServerApplication {
  /**
   * The base package for component scanning. Defaults to the package of the annotated class.
   *
   * @return the base package for component scanning
   */
  String basePackage() default StringHelper.EMPTY;

  /**
   * The base package class for component scanning. Defaults to {@code Object.class}.
   *
   * <p>Note: This attribute is intended to be used when the base package cannot be determined
   * statically. In most cases, {@link #basePackage()} should be used instead.
   *
   * @return the base package class for component scanning
   */
  Class<?> basePackageClass() default Object.class;
}
