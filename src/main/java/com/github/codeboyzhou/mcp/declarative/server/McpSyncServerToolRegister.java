package com.github.codeboyzhou.mcp.declarative.server;

import com.github.codeboyzhou.mcp.declarative.annotation.McpTool;
import com.github.codeboyzhou.mcp.declarative.annotation.McpToolParam;
import com.github.codeboyzhou.mcp.declarative.util.ReflectionHelper;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.spec.McpSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class McpSyncServerToolRegister
    implements McpServerComponentRegister<McpSyncServer, McpServerFeatures.SyncToolSpecification> {

    private static final Logger logger = LoggerFactory.getLogger(McpSyncServerToolRegister.class);

    private static final String OBJECT_TYPE_NAME = Object.class.getSimpleName().toLowerCase();

    private final Set<Class<?>> toolClasses;

    public McpSyncServerToolRegister(Set<Class<?>> toolClasses) {
        this.toolClasses = toolClasses;
    }

    @Override
    public void registerTo(McpSyncServer server) {
        for (Class<?> toolClass : toolClasses) {
            List<Method> methods = ReflectionHelper.getMethodsAnnotatedWith(toolClass, McpTool.class);
            for (Method method : methods) {
                McpServerFeatures.SyncToolSpecification tool = createComponentFrom(toolClass, method);
                server.addTool(tool);
            }
        }
    }

    @Override
    public McpServerFeatures.SyncToolSpecification createComponentFrom(Class<?> clazz, Method method) {
        McpTool toolMethod = method.getAnnotation(McpTool.class);
        McpSchema.JsonSchema paramSchema = createJsonSchema(method);
        final String name = toolMethod.name().isBlank() ? method.getName() : toolMethod.name();
        McpSchema.Tool tool = new McpSchema.Tool(name, toolMethod.description(), paramSchema);
        return new McpServerFeatures.SyncToolSpecification(tool, (exchange, params) -> {
            Object result;
            boolean isError = false;
            try {
                result = ReflectionHelper.invokeMethod(clazz, method, paramSchema, params);
            } catch (Throwable e) {
                logger.error("Error invoking tool method", e);
                result = e + ": " + e.getMessage();
                isError = true;
            }
            McpSchema.Content content = new McpSchema.TextContent(result.toString());
            return new McpSchema.CallToolResult(List.of(content), isError);
        });
    }

    private McpSchema.JsonSchema createJsonSchema(Method method) {
        //has to use linkedhashmap to make order correct
        Map<String, Object> properties = new LinkedHashMap<>();
        List<String> required = new ArrayList<>();

        List<Parameter> parameters = ReflectionHelper.getParametersAnnotatedWith(method, McpToolParam.class);
        for (Parameter parameter : parameters) {
            McpToolParam toolParam = parameter.getAnnotation(McpToolParam.class);
            final String parameterName = toolParam.name();
            final String parameterType = parameter.getType().getName().toLowerCase();

            Map<String, String> parameterProperties = Map.of(
                "type", parameterType,
                "description", toolParam.description()
            );
            properties.put(parameterName, parameterProperties);

            if (toolParam.required()) {
                required.add(parameterName);
            }
        }

        return new McpSchema.JsonSchema(OBJECT_TYPE_NAME, properties, required, false);
    }

}
