package com.github.codeboyzhou.mcp.declarative.util;

import com.github.codeboyzhou.mcp.declarative.reflect.InvocationResult;
import com.github.codeboyzhou.mcp.declarative.reflect.MethodCache;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class for reflection operations.
 *
 * <p>This class provides static methods for caching and invoking methods.
 *
 * @author codeboyzhou
 */
public enum ReflectionHelper {

  /** The singleton instance of the ReflectionHelper enum. */
  INSTANCE;

  private static final Logger log = LoggerFactory.getLogger(ReflectionHelper.class);

  /** The cache for storing method metadata. */
  private final ConcurrentHashMap<Method, MethodCache> methodCache = new ConcurrentHashMap<>();

  /**
   * Get or cache the metadata for the specified method.
   *
   * @param method the method to cache
   * @return the cached method metadata
   */
  public MethodCache getOrCache(Method method) {
    return methodCache.computeIfAbsent(
        method,
        m -> {
          final String className = m.getDeclaringClass().getName();
          log.debug("Caching method: {}.{}", className, m.getName());
          return MethodCache.of(m);
        });
  }

  /**
   * Invoke the method represented by the specified method cache on the given instance with the
   * provided parameters.
   *
   * @param instance the instance on which to invoke the method
   * @param methodCache the method cache containing the method metadata
   * @param params the list of parameters to pass to the method
   * @return the result of the method invocation
   */
  public InvocationResult invoke(Object instance, MethodCache methodCache, List<Object> params) {
    Method method = methodCache.getMethod();
    InvocationResult.Builder builder = InvocationResult.builder();
    try {
      Object result = method.invoke(instance, params.toArray());

      Class<?> returnType = method.getReturnType();
      if (returnType == void.class || returnType == Void.class) {
        return builder.result("The method call succeeded but has a void return type").build();
      }

      final String resultIfNull = "The method call succeeded but the return value is null";
      return builder.result(Objects.requireNonNullElse(result, resultIfNull)).build();
    } catch (Exception e) {
      final String errorMessage = "Error invoking method: " + methodCache.getMethodSignature();
      log.error(errorMessage, e);
      return builder.result(errorMessage).exception(e).build();
    }
  }

  /**
   * Invoke the method represented by the specified method cache on the given instance with no
   * parameters.
   *
   * @param instance the instance on which to invoke the method
   * @param methodCache the method cache containing the method metadata
   * @return the result of the method invocation
   */
  public InvocationResult invoke(Object instance, MethodCache methodCache) {
    return invoke(instance, methodCache, List.of());
  }
}
