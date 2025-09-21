package com.github.codeboyzhou.mcp.declarative.reflect;

import com.github.codeboyzhou.mcp.declarative.annotation.McpPrompt;
import com.github.codeboyzhou.mcp.declarative.annotation.McpResource;
import com.github.codeboyzhou.mcp.declarative.annotation.McpTool;
import com.github.codeboyzhou.mcp.declarative.common.Immutable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Objects;

public final class MethodCache {

  private final Immutable<Method> method;

  private final String methodName;

  private final Class<?> declaringClass;

  private final Parameter[] parameters;

  private final String methodSignature;

  private final McpResource mcpResourceAnnotation;

  private final McpPrompt mcpPromptAnnotation;

  private final McpTool mcpToolAnnotation;

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

  public static MethodCache of(Method method) {
    return new MethodCache(method);
  }

  public Method getMethod() {
    return method.get();
  }

  public String getMethodName() {
    return methodName;
  }

  public Class<?> getDeclaringClass() {
    return declaringClass;
  }

  public Parameter[] getParameters() {
    return parameters.clone();
  }

  public String getMethodSignature() {
    return methodSignature;
  }

  public McpResource getMcpResourceAnnotation() {
    return mcpResourceAnnotation;
  }

  public McpPrompt getMcpPromptAnnotation() {
    return mcpPromptAnnotation;
  }

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
