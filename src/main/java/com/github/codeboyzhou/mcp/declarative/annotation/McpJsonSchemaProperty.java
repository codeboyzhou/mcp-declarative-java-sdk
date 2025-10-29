package com.github.codeboyzhou.mcp.declarative.annotation;

import com.github.codeboyzhou.mcp.declarative.util.StringHelper;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to define the property of a complex JSON schema type (such as custom Java
 * data classes) for an MCP (Model Context Protocol) server.
 *
 * <p>By using this annotation, you can define custom JSON schema properties that are not natively
 * supported by the MCP protocol. This allows you to use more complex data models in your MCP
 * server.
 *
 * @see McpJsonSchemaDefinition
 * @author codeboyzhou
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface McpJsonSchemaProperty {
  /**
   * The name of the JSON schema property. Defaults to the Java field name.
   *
   * @return the name of the JSON schema property
   */
  String name() default StringHelper.EMPTY;

  /**
   * The description of the JSON schema property. Defaults to the value of the {@code name}
   * attribute.
   *
   * @return the description of the JSON schema property
   */
  String description() default StringHelper.EMPTY;

  /**
   * Whether the JSON schema property is required. Defaults to {@code false}.
   *
   * @return whether the JSON schema property is required
   */
  boolean required() default false;
}
