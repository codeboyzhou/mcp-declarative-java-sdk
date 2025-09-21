package com.github.codeboyzhou.mcp.declarative.server.component;

import static com.github.codeboyzhou.mcp.declarative.di.GuiceInjectorModule.INJECTED_VARIABLE_NAME_I18N_ENABLED;

import com.github.codeboyzhou.mcp.declarative.di.DependencyInjector;
import com.github.codeboyzhou.mcp.declarative.di.DependencyInjectorProvider;
import com.github.codeboyzhou.mcp.declarative.util.StringHelper;
import java.util.Locale;
import java.util.ResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractMcpServerComponent<T> implements McpServerComponent<T> {

  private static final Logger log = LoggerFactory.getLogger(AbstractMcpServerComponent.class);

  private static final String RESOURCE_BUNDLE_BASE_NAME = "i18n/mcp_server_component_descriptions";

  protected static final String NOT_SPECIFIED = "Not specified";

  protected final DependencyInjector injector;

  private final ResourceBundle bundle;

  private final boolean i18nEnabled;

  protected AbstractMcpServerComponent() {
    this.bundle = loadResourceBundle();
    this.injector = DependencyInjectorProvider.INSTANCE.getInjector();
    this.i18nEnabled = injector.getVariable(Boolean.class, INJECTED_VARIABLE_NAME_I18N_ENABLED);
  }

  protected String resolveComponentAttributeValue(String attributeLiteralValue) {
    if (i18nEnabled && bundle != null && bundle.containsKey(attributeLiteralValue)) {
      return bundle.getString(attributeLiteralValue);
    }
    return StringHelper.defaultIfBlank(attributeLiteralValue, NOT_SPECIFIED);
  }

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
