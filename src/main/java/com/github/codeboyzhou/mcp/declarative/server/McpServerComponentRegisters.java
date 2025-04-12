package com.github.codeboyzhou.mcp.declarative.server;

import com.github.codeboyzhou.mcp.declarative.annotation.McpResources;
import com.github.codeboyzhou.mcp.declarative.annotation.McpTools;
import io.modelcontextprotocol.server.McpSyncServer;
import org.reflections.Reflections;

import java.util.Set;

public final class McpServerComponentRegisters {

    public static void registerAllTo(McpSyncServer server, Reflections reflections) {
        Set<Class<?>> resourceClasses = reflections.getTypesAnnotatedWith(McpResources.class);
        new McpSyncServerResourceRegister(resourceClasses).registerTo(server);

        Set<Class<?>> toolClasses = reflections.getTypesAnnotatedWith(McpTools.class);
        new McpSyncServerToolRegister(toolClasses).registerTo(server);
    }

}
