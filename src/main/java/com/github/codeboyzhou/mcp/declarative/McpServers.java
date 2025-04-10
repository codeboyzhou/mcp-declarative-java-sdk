package com.github.codeboyzhou.mcp.declarative;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.codeboyzhou.mcp.declarative.annotation.McpComponentScan;
import com.github.codeboyzhou.mcp.declarative.annotation.McpResource;
import com.github.codeboyzhou.mcp.declarative.annotation.McpResources;
import com.github.codeboyzhou.mcp.declarative.annotation.McpTool;
import com.github.codeboyzhou.mcp.declarative.annotation.McpToolParam;
import com.github.codeboyzhou.mcp.declarative.annotation.McpTools;
import com.github.codeboyzhou.mcp.declarative.util.ReflectionHelper;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.HttpServletSseServerTransportProvider;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.server.Server;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class McpServers {

    private static final Logger logger = LoggerFactory.getLogger(McpServers.class);

    private static final McpServers INSTANCE = new McpServers();

    private static final McpSchema.ServerCapabilities DEFAULT_SERVER_CAPABILITIES = McpSchema.ServerCapabilities
        .builder()
        .resources(true, true)
        .prompts(true)
        .tools(true)
        .build();

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final String OBJECT_TYPE_NAME = Object.class.getSimpleName().toLowerCase();

    private static final String DEFAULT_MESSAGE_ENDPOINT = "/message";

    private static final String DEFAULT_SSE_ENDPOINT = "/sse";

    private static final int DEFAULT_HTTP_SERVER_PORT = 8080;

    private static Reflections reflections;

    public static McpServers run(Class<?> applicationMainClass, String[] args) {
        McpComponentScan scan = applicationMainClass.getAnnotation(McpComponentScan.class);
        reflections = new Reflections(determineBasePackage(scan, applicationMainClass));
        return INSTANCE;
    }

    private static String determineBasePackage(McpComponentScan scan, Class<?> applicationMainClass) {
        if (scan != null) {
            if (!scan.basePackage().trim().isBlank()) {
                return scan.basePackage();
            }
            if (scan.basePackageClass() != Object.class) {
                return scan.basePackageClass().getPackageName();
            }
        }
        return applicationMainClass.getPackageName();
    }

    public void startSyncStdioServer(String name, String version) {
        McpSyncServer server = McpServer.sync(new StdioServerTransportProvider())
            .capabilities(DEFAULT_SERVER_CAPABILITIES)
            .serverInfo(name, version)
            .build();

        registerResources(server);
        registerTools(server);
    }

    public void startSyncSseServer(String name, String version) {
        startSyncSseServer(name, version, DEFAULT_MESSAGE_ENDPOINT, DEFAULT_SSE_ENDPOINT, DEFAULT_HTTP_SERVER_PORT);
    }

    public void startSyncSseServer(String name, String version, int port) {
        startSyncSseServer(name, version, DEFAULT_MESSAGE_ENDPOINT, DEFAULT_SSE_ENDPOINT, port);
    }

    public void startSyncSseServer(String name, String version, String messageEndpoint, String sseEndpoint, int port) {
        HttpServletSseServerTransportProvider transport = new HttpServletSseServerTransportProvider(
            OBJECT_MAPPER, messageEndpoint, sseEndpoint
        );

        McpSyncServer server = McpServer.sync(transport)
            .capabilities(DEFAULT_SERVER_CAPABILITIES)
            .serverInfo(name, version)
            .build();

        registerResources(server);
        registerTools(server);

        startHttpServer(server, transport, port);
    }

    private void startHttpServer(McpSyncServer server, HttpServletSseServerTransportProvider transport, int port) {
        ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletContextHandler.setContextPath("/");

        ServletHolder servletHolder = new ServletHolder(transport);
        servletContextHandler.addServlet(servletHolder, "/*");

        Server httpserver = new Server(port);
        httpserver.setHandler(servletContextHandler);

        try {
            httpserver.start();
            logger.info("Jetty-based HTTP server started on http://127.0.0.1:{}", port);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    logger.info("Shutting down HTTP server");
                    httpserver.stop();
                    server.close();
                } catch (Exception e) {
                    logger.error("Error stopping HTTP server", e);
                }
            }));

            // Wait for the HTTP server to stop
            httpserver.join();
        } catch (Exception e) {
            logger.error("Error starting HTTP server on http://127.0.0.1:{}", port, e);
            server.close();
        }
    }

    private void registerResources(McpSyncServer server) {
        Set<Class<?>> resourceClasses = reflections.getTypesAnnotatedWith(McpResources.class);
        for (Class<?> resourceClass : resourceClasses) {
            Set<Method> methods = ReflectionHelper.getMethodsAnnotatedWith(resourceClass, McpResource.class);
            for (Method method : methods) {
                McpResource resourceMethod = method.getAnnotation(McpResource.class);
                McpSchema.Resource resource = new McpSchema.Resource(
                    resourceMethod.uri(),
                    resourceMethod.name().isBlank() ? method.getName() : resourceMethod.name(),
                    resourceMethod.description(),
                    resourceMethod.mimeType(),
                    new McpSchema.Annotations(List.of(resourceMethod.roles()), resourceMethod.priority())
                );
                server.addResource(new McpServerFeatures.SyncResourceSpecification(resource, (exchange, request) -> {
                    Object result;
                    try {
                        result = ReflectionHelper.invokeMethod(resourceClass, method);
                    } catch (Throwable e) {
                        logger.error("Error invoking resource method", e);
                        result = e + ": " + e.getMessage();
                    }
                    McpSchema.ResourceContents contents = new McpSchema.TextResourceContents(
                        resource.uri(), resource.mimeType(), result.toString()
                    );
                    return new McpSchema.ReadResourceResult(List.of(contents));
                }));
            }
        }
    }

    private void registerTools(McpSyncServer server) {
        Set<Class<?>> toolClasses = reflections.getTypesAnnotatedWith(McpTools.class);
        for (Class<?> toolClass : toolClasses) {
            Set<Method> methods = ReflectionHelper.getMethodsAnnotatedWith(toolClass, McpTool.class);
            for (Method method : methods) {
                McpTool toolMethod = method.getAnnotation(McpTool.class);
                McpSchema.JsonSchema paramSchema = createJsonSchema(method);
                final String toolName = toolMethod.name().isBlank() ? method.getName() : toolMethod.name();
                McpSchema.Tool tool = new McpSchema.Tool(toolName, toolMethod.description(), paramSchema);
                server.addTool(new McpServerFeatures.SyncToolSpecification(tool, (exchange, params) -> {
                    Object result;
                    boolean isError = false;
                    try {
                        result = ReflectionHelper.invokeMethod(toolClass, method, paramSchema, params);
                    } catch (Throwable e) {
                        logger.error("Error invoking tool method", e);
                        result = e + ": " + e.getMessage();
                        isError = true;
                    }
                    McpSchema.Content content = new McpSchema.TextContent(result.toString());
                    return new McpSchema.CallToolResult(List.of(content), isError);
                }));
            }
        }
    }

    private McpSchema.JsonSchema createJsonSchema(Method method) {
        Map<String, Object> properties = new HashMap<>();
        List<String> required = new ArrayList<>();

        Set<Parameter> parameters = ReflectionHelper.getParametersAnnotatedWith(method, McpToolParam.class);
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
