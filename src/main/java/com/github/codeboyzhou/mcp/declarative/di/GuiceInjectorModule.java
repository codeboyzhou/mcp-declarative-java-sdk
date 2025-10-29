package com.github.codeboyzhou.mcp.declarative.di;

import static com.google.inject.Scopes.SINGLETON;
import static java.util.stream.Collectors.toSet;
import static org.reflections.scanners.Scanners.FieldsAnnotated;
import static org.reflections.scanners.Scanners.MethodsAnnotated;

import com.github.codeboyzhou.mcp.declarative.annotation.McpI18nEnabled;
import com.github.codeboyzhou.mcp.declarative.annotation.McpPrompt;
import com.github.codeboyzhou.mcp.declarative.annotation.McpResource;
import com.github.codeboyzhou.mcp.declarative.annotation.McpServerApplication;
import com.github.codeboyzhou.mcp.declarative.annotation.McpTool;
import com.github.codeboyzhou.mcp.declarative.server.McpSseServer;
import com.github.codeboyzhou.mcp.declarative.server.McpStdioServer;
import com.github.codeboyzhou.mcp.declarative.server.McpStreamableServer;
import com.github.codeboyzhou.mcp.declarative.server.component.McpServerPrompt;
import com.github.codeboyzhou.mcp.declarative.server.component.McpServerResource;
import com.github.codeboyzhou.mcp.declarative.server.component.McpServerTool;
import com.github.codeboyzhou.mcp.declarative.server.converter.McpPromptParameterConverter;
import com.github.codeboyzhou.mcp.declarative.server.converter.McpToolParameterConverter;
import com.github.codeboyzhou.mcp.declarative.util.StringHelper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is a Guice module that configures bindings for classes annotated with {@link
 * McpServerApplication}, {@link McpResource}, {@link McpPrompt}, and {@link McpTool}.
 *
 * @author codeboyzhou
 */
public final class GuiceInjectorModule extends AbstractModule {

  private static final Logger log = LoggerFactory.getLogger(GuiceInjectorModule.class);

  /** The main class to use for configuration. */
  private final Class<?> mainClass;

  /**
   * Constructs a new {@link GuiceInjectorModule} with the specified main class.
   *
   * @param mainClass the main class to use for configuration
   */
  public GuiceInjectorModule(Class<?> mainClass) {
    this.mainClass = mainClass;
  }

  @Override
  protected void configure() {
    // Bind classes of methods annotated by McpResource, McpPrompt, McpTool
    bindClassesOfMethodsAnnotatedWith(McpResource.class);
    bindClassesOfMethodsAnnotatedWith(McpPrompt.class);
    bindClassesOfMethodsAnnotatedWith(McpTool.class);

    // Bind all implementations of McpServerComponent
    bind(McpServerResource.class).in(SINGLETON);
    bind(McpServerPrompt.class).in(SINGLETON);
    bind(McpServerTool.class).in(SINGLETON);

    // Bind all implementations of ParameterConverter
    bind(McpPromptParameterConverter.class).in(SINGLETON);
    bind(McpToolParameterConverter.class).in(SINGLETON);

    // Bind all implementations of com.github.codeboyzhou.mcp.declarative.server.McpServer
    bind(McpStdioServer.class).in(SINGLETON);
    bind(McpSseServer.class).in(SINGLETON);
    bind(McpStreamableServer.class).in(SINGLETON);
  }

  /**
   * Provides a {@link Reflections} instance for the main class.
   *
   * @return a {@link Reflections} instance for the main class
   */
  @Provides
  @Singleton
  public Reflections provideReflections() {
    McpServerApplication application = mainClass.getAnnotation(McpServerApplication.class);
    final String basePackage = determineBasePackage(application);
    return new Reflections(basePackage, MethodsAnnotated, FieldsAnnotated);
  }

  /**
   * Provides a {@link ResourceBundle} instance for the main class.
   *
   * @return a {@link ResourceBundle} instance for the main class
   */
  @Provides
  @Singleton
  public ResourceBundle provideResourceBundle() {
    McpI18nEnabled mcpI18nEnabled = mainClass.getAnnotation(McpI18nEnabled.class);
    if (mcpI18nEnabled == null) {
      log.info("McpI18nEnabled annotation is not present on the main class, skip i18n support.");
      return null;
    }

    final String baseName = mcpI18nEnabled.resourceBundleBaseName();
    if (StringHelper.isBlank(baseName)) {
      throw new IllegalArgumentException("resourceBundleBaseName must not be blank.");
    }

    return ResourceBundle.getBundle(baseName, Locale.getDefault());
  }

  /**
   * Determines the base package for the {@link Reflections} instance to scan.
   *
   * @param application the {@link McpServerApplication} annotation
   * @return the base package for the {@link Reflections} instance to scan
   */
  private String determineBasePackage(McpServerApplication application) {
    if (application != null) {
      if (!application.basePackage().trim().isBlank()) {
        return application.basePackage();
      }
      if (application.basePackageClass() != Object.class) {
        return application.basePackageClass().getPackageName();
      }
    }
    return mainClass.getPackageName();
  }

  /**
   * Binds all classes of methods annotated with the specified annotation.
   *
   * @param annotation the annotation to scan for methods
   */
  private void bindClassesOfMethodsAnnotatedWith(Class<? extends Annotation> annotation) {
    Reflections reflections = provideReflections();
    Set<Method> methods = reflections.getMethodsAnnotatedWith(annotation);
    Set<Class<?>> classes = methods.stream().map(Method::getDeclaringClass).collect(toSet());
    classes.forEach(clazz -> bind(clazz).in(SINGLETON));
  }
}
