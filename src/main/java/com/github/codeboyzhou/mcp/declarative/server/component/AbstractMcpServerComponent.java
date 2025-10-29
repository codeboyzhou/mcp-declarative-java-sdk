package com.github.codeboyzhou.mcp.declarative.server.component;

import com.github.codeboyzhou.mcp.declarative.di.DependencyInjector;
import com.github.codeboyzhou.mcp.declarative.di.DependencyInjectorProvider;
import com.github.codeboyzhou.mcp.declarative.util.StringHelper;
import java.util.ResourceBundle;

/**
 * This abstract class represents an MCP server component (resource/prompt/tool) that is responsible
 * for creating instances of a specific type {@code T} for a given method, and provides common
 * functionality for all MCP server components.
 *
 * @param <T> the type of the component
 * @author codeboyzhou
 */
public abstract class AbstractMcpServerComponent<T> implements McpServerComponent<T> {
  /** The dependency injector to use for injecting component attributes. */
  protected final DependencyInjector injector;

  /** The resource bundle to use for localizing component descriptions. */
  private final ResourceBundle bundle;

  /** Creates a new instance of {@code AbstractMcpServerComponent}. */
  protected AbstractMcpServerComponent() {
    this.injector = DependencyInjectorProvider.INSTANCE.getInjector();
    this.bundle = injector.getInstance(ResourceBundle.class);
  }

  /**
   * Localizes the attribute with the specified i18n key using the resource bundle, or returns the
   * default value if the key is not found in the bundle.
   *
   * @param i18nKey the i18n key of the attribute to localize
   * @param defaultValue the default value to return if the i18n key is not found in the bundle
   * @return the localized value of the attribute, or the default value if the i18n key is not found
   *     in the bundle
   */
  protected String localizeAttribute(String i18nKey, String defaultValue) {
    if (bundle != null && bundle.containsKey(i18nKey)) {
      return bundle.getString(i18nKey);
    }
    // If we don't need to localize, just return the literal value or default value
    return StringHelper.defaultIfBlank(i18nKey, defaultValue);
  }
}
