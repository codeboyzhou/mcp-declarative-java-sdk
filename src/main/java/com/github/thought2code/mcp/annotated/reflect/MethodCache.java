package com.github.thought2code.mcp.annotated.reflect;

import com.github.thought2code.mcp.annotated.annotation.McpPrompt;
import com.github.thought2code.mcp.annotated.annotation.McpPromptCompletion;
import com.github.thought2code.mcp.annotated.annotation.McpResource;
import com.github.thought2code.mcp.annotated.annotation.McpResourceCompletion;
import com.github.thought2code.mcp.annotated.annotation.McpTool;
import com.github.thought2code.mcp.annotated.util.Immutable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A cache class that stores metadata and annotations for Java methods.
 *
 * <p>This class encapsulates information about a Java method, including its name, declaring class,
 * parameters, return type, and MCP-specific annotations. It provides efficient access to method
 * metadata without repeated reflection operations.
 *
 * <p>The class maintains a thread-safe static cache using {@link ConcurrentHashMap} to ensure that
 * each method is cached only once, improving performance when the same method is accessed multiple
 * times.
 *
 * <p>Supported MCP annotations include:
 *
 * <ul>
 *   <li>{@link McpResource} - Marks methods as MCP resources
 *   <li>{@link McpPrompt} - Marks methods as MCP prompts
 *   <li>{@link McpTool} - Marks methods as MCP tools
 *   <li>{@link McpPromptCompletion} - Marks methods as MCP prompt completion handlers
 *   <li>{@link McpResourceCompletion} - Marks methods as MCP resource completion handlers
 * </ul>
 *
 * <p>This class is immutable and thread-safe once constructed.
 *
 * @author codeboyzhou
 * @see McpResource
 * @see McpPrompt
 * @see McpTool
 * @see McpPromptCompletion
 * @see McpResourceCompletion
 * @see Method
 */
public final class MethodCache {

  private static final Logger log = LoggerFactory.getLogger(MethodCache.class);

  /** Thread-safe static cache for storing MethodCache instances keyed by Method objects. */
  private static final ConcurrentHashMap<Method, MethodCache> CACHE = new ConcurrentHashMap<>();

  /**
   * The cached method wrapped in an {@link Immutable} wrapper for avoiding EI_EXPOSE_REP2 issue.
   */
  private final Immutable<Method> method;

  /** The name of the cached method. */
  private final String methodName;

  /** The class that declares the cached method. */
  private final Class<?> declaringClass;

  /** The parameters of the cached method. */
  private final Parameter[] parameters;

  /** The return type of the cached method. */
  private final Class<?> returnType;

  /** The generic signature of the cached method. */
  private final String methodSignature;

  /** The {@link McpResource} annotation on the cached method, or null if not present. */
  private final McpResource mcpResourceAnnotation;

  /** The {@link McpPrompt} annotation on the cached method, or null if not present. */
  private final McpPrompt mcpPromptAnnotation;

  /** The {@link McpTool} annotation on the cached method, or null if not present. */
  private final McpTool mcpToolAnnotation;

  /** The {@link McpPromptCompletion} annotation on the cached method, or null if not present. */
  private final McpPromptCompletion mcpPromptCompletionAnnotation;

  /** The {@link McpResourceCompletion} annotation on the cached method, or null if not present. */
  private final McpResourceCompletion mcpResourceCompletionAnnotation;

  /**
   * Creates a new instance of {@code MethodCache} with the specified method.
   *
   * <p>This constructor extracts all relevant metadata from the method including its name,
   * declaring class, parameters, return type, generic signature, and all MCP-specific annotations.
   * The method is wrapped in an {@link Immutable} wrapper for thread safety.
   *
   * <p>Note: For creating cached instances, prefer using the {@link #of(Method)} factory method
   * which utilizes the static cache.
   *
   * @param method the method to cache, must not be null
   * @throws NullPointerException if the method is null
   * @see #of(Method)
   * @see Immutable
   */
  public MethodCache(Method method) {
    this.method = Immutable.of(method);
    this.methodName = method.getName();
    this.declaringClass = method.getDeclaringClass();
    this.parameters = method.getParameters();
    this.returnType = method.getReturnType();
    this.methodSignature = method.toGenericString();
    this.mcpResourceAnnotation = method.getAnnotation(McpResource.class);
    this.mcpPromptAnnotation = method.getAnnotation(McpPrompt.class);
    this.mcpToolAnnotation = method.getAnnotation(McpTool.class);
    this.mcpPromptCompletionAnnotation = method.getAnnotation(McpPromptCompletion.class);
    this.mcpResourceCompletionAnnotation = method.getAnnotation(McpResourceCompletion.class);
  }

  /**
   * Creates or retrieves a cached instance of {@code MethodCache} for the specified method.
   *
   * <p>This factory method uses a thread-safe cache to ensure that each method is cached only once.
   * If a {@code MethodCache} for the given method already exists in the cache, it will be returned;
   * otherwise, a new instance will be created, cached, and returned.
   *
   * <p>The caching mechanism significantly improves performance by avoiding repeated reflection
   * operations when the same method is accessed multiple times.
   *
   * @param method the method to cache or retrieve from cache, must not be null
   * @return a cached MethodCache instance for the specified method
   * @see MethodCache
   * @see ConcurrentHashMap#computeIfAbsent(Object, java.util.function.Function)
   */
  public static MethodCache of(Method method) {
    return CACHE.computeIfAbsent(
        method,
        m -> {
          final String className = m.getDeclaringClass().getName();
          log.debug("Caching method: {}.{}", className, m.getName());
          return new MethodCache(m);
        });
  }

  /**
   * Returns the method cached by this {@code MethodCache} instance.
   *
   * <p>The method is retrieved from the {@link Immutable} wrapper, ensuring thread-safe access to
   * the underlying method object.
   *
   * @return the method cached by this {@code MethodCache} instance
   * @see Immutable
   */
  public Method getMethod() {
    return method.get();
  }

  /**
   * Returns the name of the cached method.
   *
   * @return the name of the cached method
   */
  public String getMethodName() {
    return methodName;
  }

  /**
   * Returns the class that declares the cached method.
   *
   * @return the class that declares the cached method
   */
  public Class<?> getDeclaringClass() {
    return declaringClass;
  }

  /**
   * Returns a clone of the parameters array of the cached method.
   *
   * <p>This method returns a defensive copy of the parameters array to prevent external
   * modification of the internal state.
   *
   * @return a clone of the parameters of the cached method
   * @see java.lang.reflect.Parameter
   */
  public Parameter[] getParameters() {
    return parameters.clone();
  }

  /**
   * Returns the return type of the cached method.
   *
   * @return the return type of the cached method
   */
  public Class<?> getReturnType() {
    return returnType;
  }

  /**
   * Returns the generic signature of the cached method.
   *
   * <p>The generic signature includes the fully qualified class name, method name, parameter types,
   * and return type with generic type information.
   *
   * @return the generic signature of the cached method
   * @see java.lang.reflect.Method#toGenericString()
   */
  public String getMethodSignature() {
    return methodSignature;
  }

  /**
   * Returns the {@link McpResource} annotation on the cached method.
   *
   * <p>Returns null if the method is not annotated with {@code @McpResource}.
   *
   * @return the {@link McpResource} annotation on the cached method, or null if not present
   * @see McpResource
   */
  public McpResource getMcpResourceAnnotation() {
    return mcpResourceAnnotation;
  }

  /**
   * Returns the {@link McpPrompt} annotation on the cached method.
   *
   * <p>Returns null if the method is not annotated with {@code @McpPrompt}.
   *
   * @return the {@link McpPrompt} annotation on the cached method, or null if not present
   * @see McpPrompt
   */
  public McpPrompt getMcpPromptAnnotation() {
    return mcpPromptAnnotation;
  }

  /**
   * Returns the {@link McpTool} annotation on the cached method.
   *
   * <p>Returns null if the method is not annotated with {@code @McpTool}.
   *
   * @return the {@link McpTool} annotation on the cached method, or null if not present
   * @see McpTool
   */
  public McpTool getMcpToolAnnotation() {
    return mcpToolAnnotation;
  }

  /**
   * Returns the {@link McpPromptCompletion} annotation on the cached method.
   *
   * <p>Returns null if the method is not annotated with {@code @McpPromptCompletion}.
   *
   * @return the {@link McpPromptCompletion} annotation on the cached method, or null if not present
   * @see McpPromptCompletion
   */
  public McpPromptCompletion getMcpPromptCompletionAnnotation() {
    return mcpPromptCompletionAnnotation;
  }

  /**
   * Returns the {@link McpResourceCompletion} annotation on the cached method.
   *
   * <p>Returns null if the method is not annotated with {@code @McpResourceCompletion}.
   *
   * @return the {@link McpResourceCompletion} annotation on the cached method, or null if not
   *     present
   * @see McpResourceCompletion
   */
  public McpResourceCompletion getMcpResourceCompletionAnnotation() {
    return mcpResourceCompletionAnnotation;
  }

  /**
   * Indicates whether some other object is "equal to" this one.
   *
   * <p>Two {@code MethodCache} instances are considered equal if they cache the same method object.
   * The comparison is based on the underlying method reference.
   *
   * @param obj the reference object with which to compare
   * @return true if this object is the same as the obj argument, false otherwise
   * @see Object#equals(Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    MethodCache that = (MethodCache) obj;
    return Objects.equals(method, that.method);
  }

  /**
   * Returns a hash code value for the object.
   *
   * <p>The hash code is based on the cached method object, ensuring that equal {@code MethodCache}
   * instances have the same hash code.
   *
   * @return a hash code value for this object
   * @see Object#hashCode()
   * @see #equals(Object)
   */
  @Override
  public int hashCode() {
    return Objects.hash(method);
  }

  /**
   * Returns a string representation of the object.
   *
   * <p>The string representation includes the method signature, providing a concise and informative
   * description of the cached method.
   *
   * @return a string representation of the object
   * @see Object#toString()
   * @see #getMethodSignature()
   */
  @Override
  public String toString() {
    return String.format("MethodCache{methodSignature=%s}", methodSignature);
  }
}
