package com.github.codeboyzhou.mcp.declarative.server.component;

import static com.github.codeboyzhou.mcp.declarative.di.GuiceInjectorModule.INJECTED_VARIABLE_NAME_I18N_ENABLED;

import com.github.codeboyzhou.mcp.declarative.di.DependencyInjector;
import com.github.codeboyzhou.mcp.declarative.di.DependencyInjectorProvider;
import com.github.codeboyzhou.mcp.declarative.util.StringHelper;
import java.util.Locale;
import java.util.ResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This abstract class represents an MCP server component (resource/prompt/tool) that is responsible
 * for creating instances of a specific type {@code T} for a given method, and provides common
 * functionality for all MCP server components.
 *
 * @param <T> the type of the component
 * @author codeboyzhou
 */
public abstract class AbstractMcpServerComponent<T> implements McpServerComponent<T> {

  private static final Logger log = LoggerFactory.getLogger(AbstractMcpServerComponent.class);

  /** The base name of the resource bundle for MCP server component descriptions. */
  private static final String RESOURCE_BUNDLE_BASE_NAME = "i18n/mcp_server_component_descriptions";

  /** The default value to use when a component attribute is not specified. */
  protected static final String NOT_SPECIFIED = "Not specified";

  /** The dependency injector to use for injecting component attributes. */
  protected final DependencyInjector injector;

  /** The resource bundle to use for localizing component descriptions. */
  private final ResourceBundle bundle;

  /** Whether to enable i18n for component descriptions. */
  private final boolean i18nEnabled;

  /** Creates a new instance of {@code AbstractMcpServerComponent}. */
  protected AbstractMcpServerComponent() {
    this.bundle = loadResourceBundle();
    this.injector = DependencyInjectorProvider.INSTANCE.getInjector();
    this.i18nEnabled = injector.getVariable(Boolean.class, INJECTED_VARIABLE_NAME_I18N_ENABLED);
  }

  /**
   * Resolves the value of a component attribute, using the resource bundle for localization if i18n
   * is enabled.
   *
   * @param attributeLiteralValue the literal value of the component attribute
   * @return the resolved value of the component attribute
   */
  protected String resolveComponentAttributeValue(String attributeLiteralValue) {
    if (i18nEnabled && bundle != null && bundle.containsKey(attributeLiteralValue)) {
      return bundle.getString(attributeLiteralValue);
    }
    return StringHelper.defaultIfBlank(attributeLiteralValue, NOT_SPECIFIED);
  }

  /**
   * Loads the resource bundle for MCP server component descriptions, using the default locale.
   *
   * @return the resource bundle for MCP server component descriptions
   */
  private ResourceBundle loadResourceBundle() {
    Locale locale = Locale.getDefault();
    try {
      return ResourceBundle.getBundle(RESOURCE_BUNDLE_BASE_NAME, locale);
    } catch (Exception e) {
      log.warn(
          "Can't find resource bundle for base name: {}, locale {}, i18n will be unsupported",
          RESOURCE_BUNDLE_BASE_NAME,
          locale);
      return null;
    }
  }
}
