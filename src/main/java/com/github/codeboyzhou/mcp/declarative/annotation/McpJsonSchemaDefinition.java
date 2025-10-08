package com.github.codeboyzhou.mcp.declarative.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to define the complex JSON schema type (such as custom Java data classes)
 * for an MCP (Model Context Protocol) server.
 *
 * <p>By using this annotation, you can define custom JSON schema types that are not natively
 * supported by the MCP protocol. This allows you to use more complex data models in your MCP
 * server.
 *
 * @see McpJsonSchemaProperty
 * @author codeboyzhou
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface McpJsonSchemaDefinition {}
