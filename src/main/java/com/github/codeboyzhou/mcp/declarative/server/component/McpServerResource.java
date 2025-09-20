package com.github.codeboyzhou.mcp.declarative.server.component;

import com.github.codeboyzhou.mcp.declarative.annotation.McpResource;
import com.github.codeboyzhou.mcp.declarative.reflect.MethodMetadata;
import com.github.codeboyzhou.mcp.declarative.reflect.ReflectionCache;
import com.github.codeboyzhou.mcp.declarative.util.JacksonHelper;
import com.github.codeboyzhou.mcp.declarative.util.StringHelper;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema;
import java.lang.reflect.Method;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class McpServerResource
    extends AbstractMcpServerComponent<
        McpServerFeatures.SyncResourceSpecification,
        McpSchema.ReadResourceRequest,
        McpSchema.ReadResourceResult> {

  private static final Logger log = LoggerFactory.getLogger(McpServerResource.class);

  private Object instance;

  private McpSchema.Resource resource;

  @Override
  public McpServerFeatures.SyncResourceSpecification create(Method method) {
    // Use reflection cache for performance optimization
    MethodMetadata methodCache = ReflectionCache.INSTANCE.getMethodMetadata(method);
    instance = injector.getInstance(methodCache.getDeclaringClass());

    McpResource res = methodCache.getMcpResourceAnnotation();
    final String name = StringHelper.defaultIfBlank(res.name(), methodCache.getMethodName());
    final String title = resolveComponentAttributeValue(res.title());
    final String description = resolveComponentAttributeValue(res.description());

    resource =
        McpSchema.Resource.builder()
            .uri(res.uri())
            .name(name)
            .title(title)
            .description(description)
            .mimeType(res.mimeType())
            .annotations(new McpSchema.Annotations(List.of(res.roles()), res.priority()))
            .build();

    log.debug(
        "Registering resource: {} (Cached: {})",
        JacksonHelper.toJsonString(resource),
        ReflectionCache.INSTANCE.isCached(method));

    return new McpServerFeatures.SyncResourceSpecification(
        resource, (exchange, request) -> invoke(method, description, exchange, request));
  }

  @Override
  public McpSchema.ReadResourceResult invoke(
      Method method,
      String description,
      McpSyncServerExchange exchange,
      McpSchema.ReadResourceRequest request) {

    Object result;
    MethodMetadata methodCache = ReflectionCache.INSTANCE.getMethodMetadata(method);
    try {
      // Use cached method for invocation
      result = methodCache.getMethod().invoke(instance);
    } catch (Exception e) {
      log.error("Error invoking resource method: {}", methodCache.getMethodSignature(), e);
      result = e + ": " + e.getMessage();
    }
    McpSchema.ResourceContents contents =
        new McpSchema.TextResourceContents(resource.uri(), resource.mimeType(), result.toString());
    return new McpSchema.ReadResourceResult(List.of(contents));
  }
}
