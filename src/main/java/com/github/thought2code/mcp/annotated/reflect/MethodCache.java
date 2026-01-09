package com.github.thought2code.mcp.annotated.reflect;

import com.github.thought2code.mcp.annotated.annotation.McpPrompt;
import com.github.thought2code.mcp.annotated.annotation.McpPromptCompletion;
import com.github.thought2code.mcp.annotated.annotation.McpResource;
import com.github.thought2code.mcp.annotated.annotation.McpResourceCompletion;
import com.github.thought2code.mcp.annotated.annotation.McpTool;
import com.github.thought2code.mcp.annotated.common.Immutable;
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

  /** The return type of the cached method. */
  private final Class<?> returnType;

  /** The signature of the cached method. */
  private final String methodSignature;

  /** The annotation {@link McpResource} on the cached method. */
  private final McpResource mcpResourceAnnotation;

  /** The annotation {@link McpPrompt} on the cached method. */
  private final McpPrompt mcpPromptAnnotation;

  /** The annotation {@link McpTool} on the cached method. */
  private final McpTool mcpToolAnnotation;

  /** The annotation {@link McpPromptCompletion} on the cached method. */
  private final McpPromptCompletion mcpPromptCompletionAnnotation;

  /** The annotation {@link McpResourceCompletion} on the cached method. */
  private final McpResourceCompletion mcpResourceCompletionAnnotation;

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
    this.returnType = method.getReturnType();
    this.methodSignature = method.toGenericString();
    this.mcpResourceAnnotation = method.getAnnotation(McpResource.class);
    this.mcpPromptAnnotation = method.getAnnotation(McpPrompt.class);
    this.mcpToolAnnotation = method.getAnnotation(McpTool.class);
    this.mcpPromptCompletionAnnotation = method.getAnnotation(McpPromptCompletion.class);
    this.mcpResourceCompletionAnnotation = method.getAnnotation(McpResourceCompletion.class);
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
   * Returns the return type of the cached method.
   *
   * @return the return type of the cached method
   */
  public Class<?> getReturnType() {
    return returnType;
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

  /**
   * Returns the annotation {@link McpPromptCompletion} on the cached method.
   *
   * @return the annotation {@link McpPromptCompletion} on the cached method
   */
  public McpPromptCompletion getMcpPromptCompletionAnnotation() {
    return mcpPromptCompletionAnnotation;
  }

  /**
   * Returns the annotation {@link McpResourceCompletion} on the cached method.
   *
   * @return the annotation {@link McpResourceCompletion} on the cached method
   */
  public McpResourceCompletion getMcpResourceCompletionAnnotation() {
    return mcpResourceCompletionAnnotation;
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
