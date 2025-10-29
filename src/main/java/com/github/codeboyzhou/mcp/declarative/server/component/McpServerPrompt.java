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

/**
 * This class represents an MCP server prompt component.
 *
 * @author codeboyzhou
 */
public class McpServerPrompt
    extends AbstractMcpServerComponent<McpServerFeatures.SyncPromptSpecification> {

  private static final Logger log = LoggerFactory.getLogger(McpServerPrompt.class);

  /** The converter for MCP prompt parameters. */
  private final McpPromptParameterConverter parameterConverter;

  /** Creates a new instance of {@code McpServerPrompt}. */
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
    final String title = localizeAttribute(promptMethod.title(), name);
    final String description = localizeAttribute(promptMethod.description(), name);

    List<McpSchema.PromptArgument> promptArgs = createPromptArguments(methodCache.getParameters());
    McpSchema.Prompt prompt = new McpSchema.Prompt(name, title, description, promptArgs);

    log.debug("Registering prompt: {}", JacksonHelper.toJsonString(prompt));

    return new McpServerFeatures.SyncPromptSpecification(
        prompt, (exchange, request) -> invoke(instance, methodCache, description, request));
  }

  /**
   * Invokes the prompt method with the specified arguments.
   *
   * @param instance the instance of the class that declares the prompt method
   * @param methodCache the cached method information
   * @param description the description of the prompt
   * @param request the request for the prompt
   * @return the result of the prompt invocation
   */
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

  /**
   * Creates a list of prompt arguments from the method parameters.
   *
   * @param methodParams the method parameters
   * @return the list of prompt arguments
   */
  private List<McpSchema.PromptArgument> createPromptArguments(Parameter[] methodParams) {
    List<McpSchema.PromptArgument> promptArguments = new ArrayList<>(methodParams.length);

    for (Parameter param : methodParams) {
      if (param.isAnnotationPresent(McpPromptParam.class)) {
        McpPromptParam promptParam = param.getAnnotation(McpPromptParam.class);
        final String name = promptParam.name();
        final String title = localizeAttribute(promptParam.title(), name);
        final String description = localizeAttribute(promptParam.description(), name);
        final boolean required = promptParam.required();
        promptArguments.add(new McpSchema.PromptArgument(name, title, description, required));
      }
    }

    return promptArguments;
  }
}
