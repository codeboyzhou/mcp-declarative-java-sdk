package com.github.thought2code.mcp.annotated.server.component;

import com.github.thought2code.mcp.annotated.annotation.McpJsonSchemaDefinition;
import com.github.thought2code.mcp.annotated.annotation.McpJsonSchemaProperty;
import com.github.thought2code.mcp.annotated.annotation.McpTool;
import com.github.thought2code.mcp.annotated.annotation.McpToolParam;
import com.github.thought2code.mcp.annotated.enums.JavaTypeToJsonSchemaMapper;
import com.github.thought2code.mcp.annotated.reflect.InvocationResult;
import com.github.thought2code.mcp.annotated.reflect.MethodCache;
import com.github.thought2code.mcp.annotated.server.McpStructuredContent;
import com.github.thought2code.mcp.annotated.server.converter.McpToolParameterConverter;
import com.github.thought2code.mcp.annotated.util.JacksonHelper;
import com.github.thought2code.mcp.annotated.util.ReflectionHelper;
import com.github.thought2code.mcp.annotated.util.StringHelper;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents an MCP server tool component.
 *
 * @author codeboyzhou
 */
public class McpServerTool
    extends AbstractMcpServerComponent<McpServerFeatures.SyncToolSpecification> {

  private static final Logger log = LoggerFactory.getLogger(McpServerTool.class);

  /** The parameter converter for MCP tool parameters. */
  private final McpToolParameterConverter parameterConverter;

  /** Creates a new instance of {@code McpServerTool}. */
  public McpServerTool() {
    this.parameterConverter = injector.getInstance(McpToolParameterConverter.class);
  }

  @Override
  public McpServerFeatures.SyncToolSpecification create(Method method) {
    // Use reflection cache for performance optimization
    MethodCache methodCache = ReflectionHelper.INSTANCE.getOrCache(method);
    Object instance = injector.getInstance(methodCache.getDeclaringClass());

    McpTool toolMethod = methodCache.getMcpToolAnnotation();
    final String name = StringHelper.defaultIfBlank(toolMethod.name(), methodCache.getMethodName());
    final String title = localizeAttribute(toolMethod.title(), name);
    final String description = localizeAttribute(toolMethod.description(), name);

    McpSchema.JsonSchema inputSchema = createJsonSchema(methodCache.getParameters());
    Map<String, Object> outputSchema = createJsonSchemaDefinition(methodCache.getReturnType());
    McpSchema.Tool tool =
        McpSchema.Tool.builder()
            .name(name)
            .title(title)
            .description(description)
            .inputSchema(inputSchema)
            .outputSchema(outputSchema)
            .build();

    log.debug("Registering tool: {}", JacksonHelper.toJsonString(tool));

    return McpServerFeatures.SyncToolSpecification.builder()
        .tool(tool)
        .callHandler((exchange, request) -> invoke(instance, methodCache, request))
        .build();
  }

  /**
   * Invokes the tool method with the specified arguments.
   *
   * @param instance the instance of the class that declares the tool method
   * @param methodCache the cached method information
   * @param request the tool request containing the arguments
   * @return the result of the tool invocation
   */
  private McpSchema.CallToolResult invoke(
      Object instance, MethodCache methodCache, McpSchema.CallToolRequest request) {

    Map<String, Object> arguments = request.arguments();
    List<Object> params = parameterConverter.convertAll(methodCache.getParameters(), arguments);
    InvocationResult invocation = ReflectionHelper.INSTANCE.invoke(instance, methodCache, params);

    Object result = invocation.result();
    String textContent = result.toString();
    Object structuredContent = Map.of();

    if (result instanceof McpStructuredContent mcpStructuredContent) {
      textContent = mcpStructuredContent.asTextContent();
      structuredContent = mcpStructuredContent;
    }

    return McpSchema.CallToolResult.builder()
        .content(List.of(new McpSchema.TextContent(textContent)))
        .structuredContent(structuredContent)
        .isError(invocation.isError())
        .build();
  }

  /**
   * Creates a JSON schema for the tool method parameters.
   *
   * @param methodParams the method parameters
   * @return the JSON schema for the tool method parameters
   */
  private McpSchema.JsonSchema createJsonSchema(Parameter[] methodParams) {
    Map<String, Object> properties = new LinkedHashMap<>();
    Map<String, Object> definitions = new LinkedHashMap<>();
    List<String> required = new ArrayList<>();

    for (Parameter param : methodParams) {
      if (param.isAnnotationPresent(McpToolParam.class)) {
        McpToolParam toolParam = param.getAnnotation(McpToolParam.class);
        final String parameterName = toolParam.name();
        Class<?> definitionClass = param.getType();
        Map<String, String> property = new HashMap<>();

        if (definitionClass.isAnnotationPresent(McpJsonSchemaDefinition.class)) {
          final String definitionClassName = definitionClass.getSimpleName();
          property.put("$ref", "#/definitions/" + definitionClassName);
          Map<String, Object> definition = createJsonSchemaDefinition(definitionClass);
          definitions.put(definitionClassName, definition);
        } else {
          property.put("type", JavaTypeToJsonSchemaMapper.getJsonSchemaType(definitionClass));
          property.put("description", localizeAttribute(toolParam.description(), parameterName));
        }
        properties.put(parameterName, property);

        if (toolParam.required()) {
          required.add(parameterName);
        }
      }
    }

    final boolean hasAdditionalProperties = false;
    return new McpSchema.JsonSchema(
        JavaTypeToJsonSchemaMapper.OBJECT.getJsonSchemaType(),
        properties,
        required,
        hasAdditionalProperties,
        definitions,
        definitions);
  }

  /**
   * Creates a JSON schema definition for the specified class.
   *
   * @param definitionClass the class to create the JSON schema definition for
   * @return the JSON schema definition for the specified class
   */
  private Map<String, Object> createJsonSchemaDefinition(Class<?> definitionClass) {
    Map<String, Object> definitionJsonSchema = new HashMap<>();
    definitionJsonSchema.put("type", JavaTypeToJsonSchemaMapper.OBJECT.getJsonSchemaType());

    Map<String, Object> properties = new LinkedHashMap<>();
    List<String> required = new ArrayList<>();

    Reflections reflections = injector.getInstance(Reflections.class);
    Set<Field> definitionFields = reflections.getFieldsAnnotatedWith(McpJsonSchemaProperty.class);
    List<Field> fields =
        definitionFields.stream().filter(f -> f.getDeclaringClass() == definitionClass).toList();

    for (Field field : fields) {
      McpJsonSchemaProperty property = field.getAnnotation(McpJsonSchemaProperty.class);
      if (property == null) {
        continue;
      }

      Map<String, Object> fieldProperties = new HashMap<>();
      final String fieldName = StringHelper.defaultIfBlank(property.name(), field.getName());
      fieldProperties.put("type", JavaTypeToJsonSchemaMapper.getJsonSchemaType(field.getType()));
      fieldProperties.put("description", localizeAttribute(property.description(), fieldName));

      properties.put(fieldName, fieldProperties);

      if (property.required()) {
        required.add(fieldName);
      }
    }

    definitionJsonSchema.put("properties", properties);
    definitionJsonSchema.put("required", required);

    return definitionJsonSchema;
  }
}
