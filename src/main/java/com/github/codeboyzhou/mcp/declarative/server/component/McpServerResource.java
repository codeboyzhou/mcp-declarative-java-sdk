package com.github.codeboyzhou.mcp.declarative.server.component;

import com.github.codeboyzhou.mcp.declarative.annotation.McpResource;
import com.github.codeboyzhou.mcp.declarative.reflect.InvocationResult;
import com.github.codeboyzhou.mcp.declarative.reflect.MethodCache;
import com.github.codeboyzhou.mcp.declarative.util.JacksonHelper;
import com.github.codeboyzhou.mcp.declarative.util.ReflectionHelper;
import com.github.codeboyzhou.mcp.declarative.util.StringHelper;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;
import java.lang.reflect.Method;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents an MCP server resource component.
 *
 * @author codeboyzhou
 */
public class McpServerResource
    extends AbstractMcpServerComponent<McpServerFeatures.SyncResourceSpecification> {

  private static final Logger log = LoggerFactory.getLogger(McpServerResource.class);

  @Override
  public McpServerFeatures.SyncResourceSpecification create(Method method) {
    // Use reflection cache for performance optimization
    MethodCache methodCache = ReflectionHelper.INSTANCE.getOrCache(method);
    Object instance = injector.getInstance(methodCache.getDeclaringClass());

    McpResource res = methodCache.getMcpResourceAnnotation();
    final String name = StringHelper.defaultIfBlank(res.name(), methodCache.getMethodName());
    final String title = resolveComponentAttributeValue(res.title());
    final String description = resolveComponentAttributeValue(res.description());

    McpSchema.Resource resource =
        McpSchema.Resource.builder()
            .uri(res.uri())
            .name(name)
            .title(title)
            .description(description)
            .mimeType(res.mimeType())
            .annotations(new McpSchema.Annotations(List.of(res.roles()), res.priority()))
            .build();

    log.debug("Registering resource: {}", JacksonHelper.toJsonString(resource));

    return new McpServerFeatures.SyncResourceSpecification(
        resource, (exchange, request) -> invoke(instance, methodCache, resource));
  }

  /**
   * Invokes the resource method with the specified arguments.
   *
   * @param instance the instance of the class that declares the resource method
   * @param methodCache the cached method information
   * @param resource the resource specification
   * @return the result of the resource invocation
   */
  private McpSchema.ReadResourceResult invoke(
      Object instance, MethodCache methodCache, McpSchema.Resource resource) {

    InvocationResult invocation = ReflectionHelper.INSTANCE.invoke(instance, methodCache);
    final String uri = resource.uri();
    final String mimeType = resource.mimeType();
    final String text = invocation.result().toString();
    McpSchema.ResourceContents contents = new McpSchema.TextResourceContents(uri, mimeType, text);
    return new McpSchema.ReadResourceResult(List.of(contents));
  }
}
