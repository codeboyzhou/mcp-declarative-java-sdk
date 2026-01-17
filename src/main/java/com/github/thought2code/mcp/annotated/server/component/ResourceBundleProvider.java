package com.github.thought2code.mcp.annotated.server.component;

import com.github.thought2code.mcp.annotated.annotation.McpI18nEnabled;
import com.github.thought2code.mcp.annotated.util.Immutable;
import com.github.thought2code.mcp.annotated.util.StringHelper;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A provider class for managing internationalization (i18n) resource bundles.
 *
 * <p>This class provides static methods for loading and accessing resource bundles to support
 * internationalization in MCP server applications. It uses the {@link McpI18nEnabled} annotation to
 * configure the resource bundle base name.
 *
 * <p>The class maintains a singleton {@link ResourceBundle} instance that is loaded from the
 * specified base name using the default locale. The resource bundle can be accessed through a
 * supplier for lazy evaluation and thread safety.
 *
 * <p>Key features:
 *
 * <ul>
 *   <li>Loads resource bundles based on {@link McpI18nEnabled} annotation configuration
 *   <li>Provides a supplier for accessing the loaded resource bundle
 *   <li>Supports default locale for resource bundle resolution
 *   <li>Follows the utility class pattern with a private constructor
 * </ul>
 *
 * @author codeboyzhou
 * @see ResourceBundle
 * @see McpI18nEnabled
 * @see Locale
 */
public final class ResourceBundleProvider {

  public static final Logger log = LoggerFactory.getLogger(ResourceBundleProvider.class);

  /**
   * The singleton ResourceBundle instance loaded for i18n support, wrapped in an {@link Immutable}
   * wrapper for avoiding EI_EXPOSE_REP2 issue.
   */
  private static Immutable<ResourceBundle> bundle;

  /** Private constructor to prevent instantiation of this utility class. */
  private ResourceBundleProvider() {}

  /**
   * Loads a resource bundle based on the {@link McpI18nEnabled} annotation on the main class.
   *
   * <p>This method checks if the main class is annotated with {@code @McpI18nEnabled}. If the
   * annotation is present, it loads a resource bundle using the base name specified in the
   * annotation's {@code resourceBundleBaseName} attribute. The resource bundle is loaded using the
   * default locale.
   *
   * <p>If the annotation is not present, the method logs an info message and returns without
   * loading any resource bundle, effectively disabling i18n support.
   *
   * @param mainClass the main application class to check for the McpI18nEnabled annotation
   * @throws IllegalArgumentException if the resourceBundleBaseName is blank
   * @throws MissingResourceException if no resource bundle is found for the specified base name
   * @see McpI18nEnabled
   * @see ResourceBundle#getBundle(String, Locale)
   * @see Locale#getDefault()
   */
  public static void loadResourceBundle(Class<?> mainClass) {
    log.info("Loading resource bundle for main class: {}", mainClass.getName());
    McpI18nEnabled mcpI18nEnabled = mainClass.getAnnotation(McpI18nEnabled.class);
    if (mcpI18nEnabled == null) {
      log.info("McpI18nEnabled annotation is not present on the main class, skip i18n support.");
      return;
    }

    final String baseName = mcpI18nEnabled.resourceBundleBaseName();
    if (StringHelper.isBlank(baseName)) {
      throw new IllegalArgumentException("resourceBundleBaseName must not be blank.");
    }

    bundle = Immutable.of(ResourceBundle.getBundle(baseName, Locale.getDefault()));
    log.info("Resource bundle loaded for base name: {}", baseName);
  }

  /**
   * Returns the loaded resource bundle instance.
   *
   * <p>This method returns the currently loaded {@link ResourceBundle} instance, if the main class
   * was not annotated with {@link McpI18nEnabled}, this method returns {@code null}.
   *
   * @return the loaded resource bundle instance, or {@code null} if not loaded
   * @see ResourceBundle
   * @see #loadResourceBundle(Class)
   */
  public static ResourceBundle getResourceBundle() {
    return bundle == null ? null : bundle.get();
  }
}
