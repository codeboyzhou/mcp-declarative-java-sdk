package com.github.thought2code.mcp.annotated.annotation;

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
 * <p>Example usage:
 *
 * <pre>{@code
 * @McpI18nEnabled(resourceBundleBaseName = "mcp_server_component_i18n")
 * public class MyMcpServerApplication {
 *     // Application logic...
 * }
 * }</pre>
 *
 * @author codeboyzhou
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface McpI18nEnabled {
  /**
   * Specifies the base name of the resource bundle to be used for i18n.
   *
   * <p>The resource bundle base name should follow the standard Java resource bundle naming
   * convention. For example, if the resource bundle base name is "messages", the corresponding
   * resource bundle files should be named "messages.properties", "messages_en_US.properties",
   * "messages_zh_CN.properties", etc.
   *
   * @return the base name of the resource bundle
   */
  String resourceBundleBaseName();
}
