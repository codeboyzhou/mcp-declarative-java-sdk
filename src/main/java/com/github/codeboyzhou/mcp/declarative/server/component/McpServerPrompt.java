package com.github.codeboyzhou.mcp.declarative.server.component;

import com.github.codeboyzhou.mcp.declarative.annotation.McpPrompt;
import com.github.codeboyzhou.mcp.declarative.annotation.McpPromptParam;
import com.github.codeboyzhou.mcp.declarative.reflect.InvocationResult;
import com.github.codeboyzhou.mcp.declarative.reflect.MethodCache;
import com.github.codeboyzhou.mcp.declarative.server.converter.McpPromptParameterConverter;
import com.github.codeboyzhou.mcp.declarative.util.JacksonHelper;
import com.github.codeboyzhou.mcp.declarative.util.ReflectionHelper;
import com.github.codeboyzhou.mcp.declarative.util.StringHelper;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class McpServerPrompt
    extends AbstractMcpServerComponent<McpServerFeatures.SyncPromptSpecification> {

  private static final Logger log = LoggerFactory.getLogger(McpServerPrompt.class);

  private final McpPromptParameterConverter parameterConverter;

  public McpServerPrompt() {
    this.parameterConverter = injector.getInstance(McpPromptParameterConverter.class);
  }

  @Override
  public McpServerFeatures.SyncPromptSpecification create(Method method) {
    // Use reflection cache for performance optimization
    MethodCache methodCache = ReflectionHelper.INSTANCE.getOrCache(method);
    Object instance = injector.getInstance(methodCache.getDeclaringClass());

    McpPrompt promptMethod = methodCache.getMcpPromptAnnotation();
    final String name =
        StringHelper.defaultIfBlank(promptMethod.name(), methodCache.getMethodName());
    final String title = resolveComponentAttributeValue(promptMethod.title());
    final String description = resolveComponentAttributeValue(promptMethod.description());

    List<McpSchema.PromptArgument> promptArgs = createPromptArguments(methodCache.getParameters());
    McpSchema.Prompt prompt = new McpSchema.Prompt(name, title, description, promptArgs);

    log.debug("Registering prompt: {}", JacksonHelper.toJsonString(prompt));

    return new McpServerFeatures.SyncPromptSpecification(
        prompt, (exchange, request) -> invoke(instance, methodCache, description, request));
  }

  private McpSchema.GetPromptResult invoke(
      Object instance,
      MethodCache methodCache,
      String description,
      McpSchema.GetPromptRequest request) {

    Map<String, Object> arguments = request.arguments();
    List<Object> params = parameterConverter.convertAll(methodCache.getParameters(), arguments);
    InvocationResult invocation = ReflectionHelper.INSTANCE.invoke(instance, methodCache, params);

    McpSchema.Content content = new McpSchema.TextContent(invocation.result().toString());
    McpSchema.PromptMessage message = new McpSchema.PromptMessage(McpSchema.Role.USER, content);
    return new McpSchema.GetPromptResult(description, List.of(message));
  }

  private List<McpSchema.PromptArgument> createPromptArguments(Parameter[] methodParams) {
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
