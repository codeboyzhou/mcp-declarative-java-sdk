package com.github.codeboyzhou.mcp.declarative.server.component;

import com.github.codeboyzhou.mcp.declarative.annotation.McpPrompt;
import com.github.codeboyzhou.mcp.declarative.annotation.McpPromptParam;
import com.github.codeboyzhou.mcp.declarative.reflect.MethodMetadata;
import com.github.codeboyzhou.mcp.declarative.reflect.ReflectionCache;
import com.github.codeboyzhou.mcp.declarative.server.converter.McpPromptParameterConverter;
import com.github.codeboyzhou.mcp.declarative.util.ObjectMappers;
import com.github.codeboyzhou.mcp.declarative.util.Strings;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class McpServerPrompt
    extends AbstractMcpServerComponent<
        McpServerFeatures.SyncPromptSpecification,
        McpSchema.GetPromptRequest,
        McpSchema.GetPromptResult> {

  private static final Logger log = LoggerFactory.getLogger(McpServerPrompt.class);

  private final McpPromptParameterConverter converter;

  private MethodMetadata methodCache;

  private Object instance;

  private String description;

  public McpServerPrompt() {
    this.converter = injector.getInstance(McpPromptParameterConverter.class);
  }

  @Override
  public McpServerFeatures.SyncPromptSpecification create(Method method) {
    // Use reflection cache for performance optimization
    methodCache = ReflectionCache.INSTANCE.getMethodMetadata(method);
    instance = injector.getInstance(methodCache.getDeclaringClass());

    McpPrompt promptMethod = methodCache.getMcpPromptAnnotation();
    final String name = Strings.defaultIfBlank(promptMethod.name(), methodCache.getMethodName());
    final String title = resolveComponentAttributeValue(promptMethod.title());
    description = resolveComponentAttributeValue(promptMethod.description());

    List<McpSchema.PromptArgument> promptArguments = createPromptArguments();
    McpSchema.Prompt prompt = new McpSchema.Prompt(name, title, description, promptArguments);

    log.debug(
        "Registering prompt: {} (Cached: {})",
        ObjectMappers.toJson(prompt),
        ReflectionCache.INSTANCE.isCached(method));

    return new McpServerFeatures.SyncPromptSpecification(prompt, this);
  }

  @Override
  public McpSchema.GetPromptResult apply(McpSyncServerExchange ex, McpSchema.GetPromptRequest req) {
    Object result;
    try {
      Map<String, Object> arguments = req.arguments();
      List<Object> convertedParams = converter.convertAllParameters(methodCache, arguments);
      // Use cached method for invocation
      result = methodCache.getMethod().invoke(instance, convertedParams.toArray());
    } catch (Exception e) {
      log.error("Error invoking prompt method: {}", methodCache.getMethodSignature(), e);
      result = e + ": " + e.getMessage();
    }
    McpSchema.Content content = new McpSchema.TextContent(result.toString());
    McpSchema.PromptMessage message = new McpSchema.PromptMessage(McpSchema.Role.USER, content);
    return new McpSchema.GetPromptResult(description, List.of(message));
  }

  private List<McpSchema.PromptArgument> createPromptArguments() {
    Parameter[] methodParams = methodCache.getParameters();
    List<McpSchema.PromptArgument> promptArguments = new ArrayList<>(methodParams.length);

    for (Parameter param : methodParams) {
      if (param.isAnnotationPresent(McpPromptParam.class)) {
        McpPromptParam promptParam = param.getAnnotation(McpPromptParam.class);
        final String name = promptParam.name();
        final String title = resolveComponentAttributeValue(promptParam.title());
        final String description = resolveComponentAttributeValue(promptParam.description());
        final boolean required = promptParam.required();
        promptArguments.add(new McpSchema.PromptArgument(name, title, description, required));
      }
    }

    return promptArguments;
  }
}
