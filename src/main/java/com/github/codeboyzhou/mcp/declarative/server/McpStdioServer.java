package com.github.codeboyzhou.mcp.declarative.server;

import io.modelcontextprotocol.json.McpJsonMapper;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;

public class McpStdioServer extends AbstractMcpServer<McpServerInfo> {
  @Override
  public McpServer.SyncSpecification<?> sync(McpServerInfo serverInfo) {
    return McpServer.sync(new StdioServerTransportProvider(McpJsonMapper.getDefault()));
  }
}
