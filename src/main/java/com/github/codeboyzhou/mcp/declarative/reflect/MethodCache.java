package com.github.codeboyzhou.mcp.declarative.reflect;

import com.github.codeboyzhou.mcp.declarative.annotation.McpPrompt;
import com.github.codeboyzhou.mcp.declarative.annotation.McpResource;
import com.github.codeboyzhou.mcp.declarative.annotation.McpTool;
import com.github.codeboyzhou.mcp.declarative.common.Immutable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Objects;

/**
 * This class caches information about a Java method, including its name, declaring class,
 * parameters, and annotations.
 *
 * @author codeboyzhou
 */
public final class MethodCache {

  /** The cached method. */
  private final Immutable<Method> method;

  /** The name of the cached method. */
  private final String methodName;

  /** The class that declares the cached method. */
  private final Class<?> declaringClass;

  /** The parameters of the cached method. */
  private final Parameter[] parameters;

  /** The signature of the cached method. */
  private final String methodSignature;

  /** The annotation {@link McpResource} on the cached method. */
  private final McpResource mcpResourceAnnotation;

  /** The annotation {@link McpPrompt} on the cached method. */
  private final McpPrompt mcpPromptAnnotation;

  /** The annotation {@link McpTool} on the cached method. */
  private final McpTool mcpToolAnnotation;

  /**
   * Creates a new instance of {@code MethodCache} with the specified method.
   *
   * @param method the method to cache
   */
  public MethodCache(Method method) {
    this.method = Immutable.of(method);
    this.methodName = method.getName();
    this.declaringClass = method.getDeclaringClass();
    this.parameters = method.getParameters();
    this.methodSignature = method.toGenericString();
    this.mcpResourceAnnotation = method.getAnnotation(McpResource.class);
    this.mcpPromptAnnotation = method.getAnnotation(McpPrompt.class);
    this.mcpToolAnnotation = method.getAnnotation(McpTool.class);
  }

  /**
   * Creates a new instance of {@code MethodCache} with the specified method.
   *
   * @param method the method to cache
   * @return a new instance of {@code MethodCache} with the specified method
   */
  public static MethodCache of(Method method) {
    return new MethodCache(method);
  }

  /**
   * Returns the method cached by this {@code MethodCache} instance.
   *
   * @return the method cached by this {@code MethodCache} instance
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
   * Returns the parameters of the cached method.
   *
   * @return the parameters of the cached method
   */
  public Parameter[] getParameters() {
    return parameters.clone();
  }

  /**
   * Returns the signature of the cached method.
   *
   * @return the signature of the cached method
   */
  public String getMethodSignature() {
    return methodSignature;
  }

  /**
   * Returns the annotation {@link McpResource} on the cached method.
   *
   * @return the annotation {@link McpResource} on the cached method
   */
  public McpResource getMcpResourceAnnotation() {
    return mcpResourceAnnotation;
  }

  /**
   * Returns the annotation {@link McpPrompt} on the cached method.
   *
   * @return the annotation {@link McpPrompt} on the cached method
   */
  public McpPrompt getMcpPromptAnnotation() {
    return mcpPromptAnnotation;
  }

  /**
   * Returns the annotation {@link McpTool} on the cached method.
   *
   * @return the annotation {@link McpTool} on the cached method
   */
  public McpTool getMcpToolAnnotation() {
    return mcpToolAnnotation;
  }

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

  @Override
  public int hashCode() {
    return Objects.hash(method);
  }

  @Override
  public String toString() {
    return String.format("MethodCache{methodSignature=%s}", methodSignature);
  }
}
