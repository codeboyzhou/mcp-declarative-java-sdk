package com.github.codeboyzhou.mcp.declarative.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to mark a class as i18n enabled.
 *
 * <p>When the main class of MCP (Model Context Protocol) server application is annotated with
 * {@code @McpI18nEnabled}, it indicates that the server application supports internationalization
 * (i18n) for tool/prompt/resource methods. This allows for the translation of tool/prompt/resource
 * titles and descriptions into different languages.
 *
 * <p>Note: This annotation is only applicable to the main class of the MCP server application.
 *
 * @apiNote Example usage:
 *     <pre>{@code
 * @McpI18nEnabled
 * public class MyMcpServerApplication {
 *     // Application logic...
 * }
 * }</pre>
 *
 * @author codeboyzhou
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface McpI18nEnabled {}
