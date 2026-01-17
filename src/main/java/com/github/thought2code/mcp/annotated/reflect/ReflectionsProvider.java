package com.github.thought2code.mcp.annotated.reflect;

import static org.reflections.scanners.Scanners.FieldsAnnotated;
import static org.reflections.scanners.Scanners.MethodsAnnotated;

import com.github.thought2code.mcp.annotated.annotation.McpServerApplication;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A provider class for reflection operations using the Reflections library.
 *
 * <p>This class provides static methods for initializing and accessing reflection capabilities to
 * scan for annotated methods and fields in a specified package. It uses the Reflections library to
 * perform runtime scanning of classpath components.
 *
 * <p>The class maintains a singleton {@link Reflections} instance that is initialized with a base
 * package derived from the main application class or the {@link McpServerApplication} annotation.
 * The scanning is configured to look for annotated methods and fields.
 *
 * <p>Key features:
 *
 * <ul>
 *   <li>Initializes reflection scanning for a specified base package
 *   <li>Supports package configuration via {@link McpServerApplication} annotation
 *   <li>Retrieves methods annotated with specific annotations
 *   <li>Retrieves fields annotated with specific annotations
 * </ul>
 *
 * <p>This class follows the utility class pattern with a private constructor to prevent
 * instantiation.
 *
 * @author codeboyzhou
 * @see Reflections
 * @see McpServerApplication
 * @see Method
 * @see Field
 */
public final class ReflectionsProvider {

  private static final Logger log = LoggerFactory.getLogger(ReflectionsProvider.class);

  /** The singleton Reflections instance used for scanning and reflection operations. */
  private static Reflections reflections;

  /** Private constructor to prevent instantiation of this utility class. */
  private ReflectionsProvider() {}

  /**
   * Initializes the Reflections instance with the specified main class.
   *
   * <p>This method determines the base package for reflection scanning by examining the provided
   * main class. The base package can be configured in three ways:
   *
   * <ol>
   *   <li>Default: Uses the package name of the main class
   *   <li>Annotation string: Uses the {@code basePackage} attribute from {@link
   *       McpServerApplication} if specified and not blank
   *   <li>Annotation class: Uses the package name of the {@code basePackageClass} attribute from
   *       {@link McpServerApplication} if specified and not {@code Object.class}
   * </ol>
   *
   * <p>The Reflections instance is configured to scan for annotated methods and fields within the
   * determined base package.
   *
   * @param mainClass the main application class used to determine the base package
   * @see McpServerApplication
   * @see Reflections
   */
  public static void initializeReflectionsInstance(Class<?> mainClass) {
    if (reflections != null) {
      log.warn("Reflections instance is already initialized");
      return;
    }

    log.info("Initializing Reflections instance for main class: {}", mainClass.getName());
    String basePackage = mainClass.getPackageName();
    McpServerApplication application = mainClass.getAnnotation(McpServerApplication.class);
    if (application != null) {
      if (!application.basePackage().trim().isBlank()) {
        basePackage = application.basePackage();
      }
      if (application.basePackageClass() != Object.class) {
        basePackage = application.basePackageClass().getPackageName();
      }
    }
    reflections = new Reflections(basePackage, MethodsAnnotated, FieldsAnnotated);
    log.info("Reflections instance initialized for base package: {}", basePackage);
  }

  /**
   * Retrieves all methods annotated with the specified annotation.
   *
   * <p>This method uses the initialized Reflections instance to scan the configured base package
   * and return a set of all methods that are annotated with the given annotation type.
   *
   * <p>The method requires that {@link #initializeReflectionsInstance(Class)} has been called
   * before invoking this method.
   *
   * @param annotation the annotation class to search for
   * @return a set of methods annotated with the specified annotation
   * @see Reflections#getMethodsAnnotatedWith(Class)
   * @see Method
   */
  public static Set<Method> getMethodsAnnotatedWith(Class<? extends Annotation> annotation) {
    return reflections.getMethodsAnnotatedWith(annotation);
  }

  /**
   * Retrieves all fields annotated with the specified annotation.
   *
   * <p>This method uses the initialized Reflections instance to scan the configured base package
   * and return a set of all fields that are annotated with the given annotation type.
   *
   * <p>The method requires that {@link #initializeReflectionsInstance(Class)} has been called
   * before invoking this method.
   *
   * @param annotation the annotation class to search for
   * @return a set of fields annotated with the specified annotation
   * @see Reflections#getFieldsAnnotatedWith(Class)
   * @see Field
   */
  public static Set<Field> getFieldsAnnotatedWith(Class<? extends Annotation> annotation) {
    return reflections.getFieldsAnnotatedWith(annotation);
  }
}
