package com.github.codeboyzhou.mcp.declarative.server;

import com.github.codeboyzhou.mcp.declarative.annotation.McpResource;
import com.github.codeboyzhou.mcp.declarative.util.ReflectionHelper;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.spec.McpSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

public class McpSyncServerResourceRegister
    implements McpServerComponentRegister<McpSyncServer, McpServerFeatures.SyncResourceSpecification> {

    private static final Logger logger = LoggerFactory.getLogger(McpSyncServerResourceRegister.class);

    private final Set<Class<?>> resourceClasses;

    public McpSyncServerResourceRegister(Set<Class<?>> resourceClasses) {
        this.resourceClasses = resourceClasses;
    }

    @Override
    public void registerTo(McpSyncServer server) {
        for (Class<?> resourceClass : resourceClasses) {
            List<Method> methods = ReflectionHelper.getMethodsAnnotatedWith(resourceClass, McpResource.class);
            for (Method method : methods) {
                McpServerFeatures.SyncResourceSpecification resource = createComponentFrom(resourceClass, method);
                server.addResource(resource);
            }
        }
    }

    @Override
    public McpServerFeatures.SyncResourceSpecification createComponentFrom(Class<?> clazz, Method method) {
        McpResource res = method.getAnnotation(McpResource.class);
        final String name = res.name().isBlank() ? method.getName() : res.name();
        McpSchema.Resource resource = new McpSchema.Resource(
            res.uri(), name, res.description(), res.mimeType(),
            new McpSchema.Annotations(List.of(res.roles()), res.priority())
        );
        return new McpServerFeatures.SyncResourceSpecification(resource, (exchange, request) -> {
            Object result;
            try {
                result = ReflectionHelper.invokeMethod(clazz, method);
            } catch (Throwable e) {
                logger.error("Error invoking resource method", e);
                result = e + ": " + e.getMessage();
            }
            McpSchema.ResourceContents contents = new McpSchema.TextResourceContents(
                resource.uri(), resource.mimeType(), result.toString()
            );
            return new McpSchema.ReadResourceResult(List.of(contents));
        });
    }

}
