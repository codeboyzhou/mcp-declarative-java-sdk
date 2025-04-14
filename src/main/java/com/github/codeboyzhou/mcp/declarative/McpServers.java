package com.github.codeboyzhou.mcp.declarative;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.codeboyzhou.mcp.declarative.annotation.McpComponentScan;
import com.github.codeboyzhou.mcp.declarative.listener.DefaultMcpSyncHttpServerStatusListener;
import com.github.codeboyzhou.mcp.declarative.listener.McpHttpServerStatusListener;
import com.github.codeboyzhou.mcp.declarative.server.McpHttpServer;
import com.github.codeboyzhou.mcp.declarative.server.McpServerComponentRegisters;
import com.github.codeboyzhou.mcp.declarative.server.McpServerFactory;
import com.github.codeboyzhou.mcp.declarative.server.McpServerInfo;
import com.github.codeboyzhou.mcp.declarative.server.McpSseServerInfo;
import com.github.codeboyzhou.mcp.declarative.server.McpSyncServerFactory;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.HttpServletSseServerTransportProvider;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;
import io.modelcontextprotocol.spec.McpServerTransportProvider;
import org.reflections.Reflections;

public class McpServers {

    private static final McpServers INSTANCE = new McpServers();

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final String DEFAULT_MESSAGE_ENDPOINT = "/message";

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

    public void startSyncStdioServer(String name, String version, String instructions) {
        McpServerFactory<McpSyncServer> factory = new McpSyncServerFactory();
        McpServerInfo serverInfo = McpServerInfo.builder().name(name).version(version).instructions(instructions).build();
        McpServerTransportProvider transportProvider = new StdioServerTransportProvider();
        McpSyncServer server = factory.create(serverInfo, transportProvider);
        McpServerComponentRegisters.registerAllTo(server, reflections);
    }

    public void startSyncStdioServer(McpServerInfo serverInfo) {
        startSyncStdioServer(serverInfo.name(), serverInfo.version(), serverInfo.instructions());
    }

    public void startSyncSseServer(McpSseServerInfo serverInfo, McpHttpServerStatusListener<McpSyncServer> listener) {
        McpServerFactory<McpSyncServer> factory = new McpSyncServerFactory();
        HttpServletSseServerTransportProvider transportProvider = new HttpServletSseServerTransportProvider(
            OBJECT_MAPPER, serverInfo.baseUrl(), serverInfo.messageEndpoint(), serverInfo.sseEndpoint()
        );
        McpSyncServer server = factory.create(serverInfo, transportProvider);
        McpServerComponentRegisters.registerAllTo(server, reflections);
        McpHttpServer<McpSyncServer> httpServer = new McpHttpServer<>();
        httpServer.with(transportProvider).with(serverInfo).with(listener).attach(server).start();
    }

    public void startSyncSseServer(McpSseServerInfo serverInfo) {
        startSyncSseServer(serverInfo, new DefaultMcpSyncHttpServerStatusListener());
    }

}
