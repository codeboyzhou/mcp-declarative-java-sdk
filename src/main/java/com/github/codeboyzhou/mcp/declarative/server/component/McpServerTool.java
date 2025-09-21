package com.github.codeboyzhou.mcp.declarative.server.component;

import com.github.codeboyzhou.mcp.declarative.annotation.McpJsonSchemaDefinition;
import com.github.codeboyzhou.mcp.declarative.annotation.McpJsonSchemaDefinitionProperty;
import com.github.codeboyzhou.mcp.declarative.annotation.McpTool;
import com.github.codeboyzhou.mcp.declarative.annotation.McpToolParam;
import com.github.codeboyzhou.mcp.declarative.enums.JsonSchemaDataType;
import com.github.codeboyzhou.mcp.declarative.reflect.InvocationResult;
import com.github.codeboyzhou.mcp.declarative.reflect.MethodCache;
import com.github.codeboyzhou.mcp.declarative.server.converter.McpToolParameterConverter;
import com.github.codeboyzhou.mcp.declarative.util.JacksonHelper;
import com.github.codeboyzhou.mcp.declarative.util.ReflectionHelper;
import com.github.codeboyzhou.mcp.declarative.util.StringHelper;
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

public class McpServerTool
    extends AbstractMcpServerComponent<McpServerFeatures.SyncToolSpecification> {

  private static final Logger log = LoggerFactory.getLogger(McpServerTool.class);

  private final McpToolParameterConverter parameterConverter;

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
    final String title = resolveComponentAttributeValue(toolMethod.title());
    final String description = resolveComponentAttributeValue(toolMethod.description());

    McpSchema.JsonSchema paramSchema = createJsonSchema(methodCache.getParameters());
    McpSchema.Tool tool =
        McpSchema.Tool.builder()
            .name(name)
            .title(title)
            .description(description)
            .inputSchema(paramSchema)
            .build();

    log.debug("Registering tool: {}", JacksonHelper.toJsonString(tool));

    return McpServerFeatures.SyncToolSpecification.builder()
        .tool(tool)
        .callHandler((exchange, request) -> invoke(instance, methodCache, request))
        .build();
  }

  private McpSchema.CallToolResult invoke(
      Object instance, MethodCache methodCache, McpSchema.CallToolRequest request) {

    Map<String, Object> arguments = request.arguments();
    List<Object> params = parameterConverter.convertAll(methodCache.getParameters(), arguments);
    InvocationResult invocation = ReflectionHelper.INSTANCE.invoke(instance, methodCache, params);

    final boolean isError = invocation.isError();
    McpSchema.Content content = new McpSchema.TextContent(invocation.result().toString());
    return McpSchema.CallToolResult.builder().content(List.of(content)).isError(isError).build();
  }

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
          property.put("type", definitionClass.getSimpleName().toLowerCase());
          property.put("description", resolveComponentAttributeValue(toolParam.description()));
        }
        properties.put(parameterName, property);

        if (toolParam.required()) {
          required.add(parameterName);
        }
      }
    }

    final boolean hasAdditionalProperties = false;
    return new McpSchema.JsonSchema(
        JsonSchemaDataType.OBJECT.getType(),
        properties,
        required,
        hasAdditionalProperties,
        definitions,
        definitions);
  }

  private Map<String, Object> createJsonSchemaDefinition(Class<?> definitionClass) {
    Map<String, Object> definitionJsonSchema = new HashMap<>();
    definitionJsonSchema.put("type", JsonSchemaDataType.OBJECT.getType());

    Map<String, Object> properties = new LinkedHashMap<>();
    List<String> required = new ArrayList<>();

    Reflections reflections = injector.getInstance(Reflections.class);
    Set<Field> definitionFields =
        reflections.getFieldsAnnotatedWith(McpJsonSchemaDefinitionProperty.class);
    List<Field> fields =
        definitionFields.stream().filter(f -> f.getDeclaringClass() == definitionClass).toList();

    for (Field field : fields) {
      McpJsonSchemaDefinitionProperty property =
          field.getAnnotation(McpJsonSchemaDefinitionProperty.class);
      if (property == null) {
        continue;
      }

      Map<String, Object> fieldProperties = new HashMap<>();
      fieldProperties.put("type", JsonSchemaDataType.fromJavaType(field.getType()).getType());
      fieldProperties.put("description", resolveComponentAttributeValue(property.description()));

      final String fieldName = StringHelper.defaultIfBlank(property.name(), field.getName());
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
