package com.github.codeboyzhou.mcp.declarative.server;

import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.spec.McpServerTransportProvider;

public class McpSyncServerFactory implements McpServerFactory<McpSyncServer> {

    @Override
    public McpSyncServer create(McpServerInfo serverInfo, McpServerTransportProvider transportProvider) {
        return McpServer.sync(transportProvider)
            .instructions(serverInfo.instructions())
            .capabilities(configureServerCapabilities())
            .serverInfo(serverInfo.name(), serverInfo.version())
            .requestTimeout(serverInfo.requestTimeout())
            .build();
    }

}
