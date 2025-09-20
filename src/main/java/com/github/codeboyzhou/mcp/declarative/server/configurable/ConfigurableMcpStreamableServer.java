package com.github.codeboyzhou.mcp.declarative.server.configurable;

import com.github.codeboyzhou.mcp.declarative.configuration.McpServerConfiguration;
import com.github.codeboyzhou.mcp.declarative.configuration.McpServerStreamable;
import com.github.codeboyzhou.mcp.declarative.server.EmbeddedJettyServer;
import io.modelcontextprotocol.json.McpJsonMapper;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.transport.HttpServletStreamableServerTransportProvider;
import java.time.Duration;

public class ConfigurableMcpStreamableServer extends AbstractConfigurableMcpServer {

  public ConfigurableMcpStreamableServer(McpServerConfiguration configuration) {
    super(configuration);
  }

  @Override
  public McpServer.SyncSpecification<?> sync() {
    McpServerStreamable streamable = configuration.streamable();
    HttpServletStreamableServerTransportProvider transportProvider =
        HttpServletStreamableServerTransportProvider.builder()
            .jsonMapper(McpJsonMapper.getDefault())
            .mcpEndpoint(streamable.mcpEndpoint())
            .disallowDelete(streamable.disallowDelete())
            .keepAliveInterval(Duration.ofMillis(streamable.keepAliveInterval()))
            .build();
    EmbeddedJettyServer httpserver = new EmbeddedJettyServer();
    httpserver.use(transportProvider).bind(streamable.port()).start();
    return McpServer.sync(transportProvider);
  }
}
