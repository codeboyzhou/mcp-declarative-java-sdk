package com.github.codeboyzhou.mcp.declarative.util;

import com.github.codeboyzhou.mcp.declarative.reflect.InvocationResult;
import com.github.codeboyzhou.mcp.declarative.reflect.MethodCache;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum ReflectionHelper {
  INSTANCE;

  private static final Logger log = LoggerFactory.getLogger(ReflectionHelper.class);

  private final ConcurrentHashMap<Method, MethodCache> methodCache = new ConcurrentHashMap<>();

  public MethodCache getOrCache(Method method) {
    return methodCache.computeIfAbsent(
        method,
        m -> {
          final String className = m.getDeclaringClass().getName();
          log.debug("Caching method: {}.{}", className, m.getName());
          return MethodCache.of(m);
        });
  }

  public boolean isCached(Method method) {
    return methodCache.containsKey(method);
  }

  public InvocationResult invoke(Object instance, MethodCache methodCache, List<Object> params) {
    Method method = methodCache.getMethod();
    InvocationResult.Builder builder = InvocationResult.builder();
    try {
      Object result = method.invoke(instance, params.toArray());
      if (method.getReturnType() == void.class) {
        builder.result("The method call succeeded but has a void return type");
      } else {
        final String resultIfNull = "The method call succeeded but the return value is null";
        builder.result(Objects.requireNonNullElse(result, resultIfNull));
      }
    } catch (Exception e) {
      final String errorMessage = "Error invoking method: " + methodCache.getMethodSignature();
      log.error(errorMessage, e);
      builder.result(errorMessage);
      builder.exception(e);
    }
    return builder.build();
  }

  public InvocationResult invoke(Object instance, MethodCache methodCache) {
    return invoke(instance, methodCache, List.of());
  }
}
