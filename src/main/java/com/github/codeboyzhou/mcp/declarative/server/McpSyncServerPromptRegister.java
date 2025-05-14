package com.github.codeboyzhou.mcp.declarative.server;

import com.github.codeboyzhou.mcp.declarative.annotation.McpPrompt;
import com.github.codeboyzhou.mcp.declarative.annotation.McpPromptParam;
import com.github.codeboyzhou.mcp.declarative.util.ReflectionHelper;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.spec.McpSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class McpSyncServerPromptRegister
    implements McpServerComponentRegister<McpSyncServer, McpServerFeatures.SyncPromptSpecification> {

    private static final Logger logger = LoggerFactory.getLogger(McpSyncServerPromptRegister.class);

    private final Set<Class<?>> promptClasses;

    public McpSyncServerPromptRegister(Set<Class<?>> promptClasses) {
        this.promptClasses = promptClasses;
    }

    @Override
    public void registerTo(McpSyncServer server) {
        for (Class<?> promptClass : promptClasses) {
            List<Method> methods = ReflectionHelper.getMethodsAnnotatedWith(promptClass, McpPrompt.class);
            for (Method method : methods) {
                McpServerFeatures.SyncPromptSpecification prompt = createComponentFrom(promptClass, method);
                server.addPrompt(prompt);
            }
        }
    }

    @Override
    public McpServerFeatures.SyncPromptSpecification createComponentFrom(Class<?> clazz, Method method) {
        McpPrompt promptMethod = method.getAnnotation(McpPrompt.class);
        final String name = promptMethod.name().isBlank() ? method.getName() : promptMethod.name();
        final String description = promptMethod.description();
        List<McpSchema.PromptArgument> promptArguments = createPromptArguments(method);
        McpSchema.Prompt prompt = new McpSchema.Prompt(name, description, promptArguments);
        return new McpServerFeatures.SyncPromptSpecification(prompt, (exchange, request) -> {
            Object result;
            try {
                result = ReflectionHelper.invokeMethod(clazz, method, request.arguments());
            } catch (Throwable e) {
                logger.error("Error invoking prompt method", e);
                result = e + ": " + e.getMessage();
            }
            McpSchema.Content content = new McpSchema.TextContent(result.toString());
            McpSchema.PromptMessage message = new McpSchema.PromptMessage(McpSchema.Role.USER, content);
            return new McpSchema.GetPromptResult(description, List.of(message));
        });
    }

    private List<McpSchema.PromptArgument> createPromptArguments(Method method) {
        List<Parameter> parameters = ReflectionHelper.getParametersAnnotatedWith(method, McpPromptParam.class);
        List<McpSchema.PromptArgument> promptArguments = new ArrayList<>(parameters.size());
        for (Parameter parameter : parameters) {
            McpPromptParam promptParam = parameter.getAnnotation(McpPromptParam.class);
            final String name = promptParam.name();
            final String description = promptParam.description();
            final boolean required = promptParam.required();
            McpSchema.PromptArgument promptArgument = new McpSchema.PromptArgument(name, description, required);
            promptArguments.add(promptArgument);
        }
        return promptArguments;
    }

}
