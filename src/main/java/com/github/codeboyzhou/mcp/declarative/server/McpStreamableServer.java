package com.github.codeboyzhou.mcp.declarative.server;

import io.modelcontextprotocol.json.McpJsonMapper;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.transport.HttpServletStreamableServerTransportProvider;

public class McpStreamableServer extends AbstractMcpServer<McpStreamableServerInfo> {
  @Override
  public McpServer.SyncSpecification<?> sync(McpStreamableServerInfo info) {
    HttpServletStreamableServerTransportProvider transportProvider =
        HttpServletStreamableServerTransportProvider.builder()
            .jsonMapper(McpJsonMapper.getDefault())
            .mcpEndpoint(info.mcpEndpoint())
            .disallowDelete(info.disallowDelete())
            .contextExtractor(info.contextExtractor())
            .keepAliveInterval(info.keepAliveInterval())
            .build();
    EmbeddedJettyServer httpserver = new EmbeddedJettyServer();
    httpserver.use(transportProvider).bind(info.port()).start();
    return McpServer.sync(transportProvider);
  }
}
