package com.github.codeboyzhou.mcp.declarative.listener;

import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.spec.McpSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultMcpSyncHttpServerStatusListener implements McpHttpServerStatusListener<McpSyncServer> {

    private static final Logger logger = LoggerFactory.getLogger(DefaultMcpSyncHttpServerStatusListener.class);

    @Override
    public void onStarted(McpSyncServer mcpServer) {
        McpSchema.Implementation serverInfo = mcpServer.getServerInfo();
        logger.info("MCP server [{}] {} started successfully in HTTP SSE mode", serverInfo.name(), serverInfo.version());
    }

    @Override
    public void onStopped(McpSyncServer mcpServer) {
        mcpServer.closeGracefully();
        McpSchema.Implementation serverInfo = mcpServer.getServerInfo();
        logger.info("MCP server [{}] {} closed gracefully", serverInfo.name(), serverInfo.version());
    }

    @Override
    public void onError(McpSyncServer mcpServer, Throwable throwable) {
        mcpServer.close();
        McpSchema.Implementation serverInfo = mcpServer.getServerInfo();
        logger.info("MCP server [{}] {} closed", serverInfo.name(), serverInfo.version());
    }

}
