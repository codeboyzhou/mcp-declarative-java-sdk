package com.github.codeboyzhou.mcp.declarative.listener;

public interface McpHttpServerStatusListener<T> {

    void onStarted(T mcpServer);

    void onStopped(T mcpServer);

    void onError(T mcpServer, Throwable throwable);
}
