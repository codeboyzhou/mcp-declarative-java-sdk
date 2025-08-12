package com.github.codeboyzhou.mcp.declarative.server.factory;

import com.github.codeboyzhou.mcp.declarative.server.McpServerInfo;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpServerTransportProviderBase;

public abstract class AbstractMcpServerFactory<
        T extends McpServerTransportProviderBase, S extends McpServerInfo>
    implements McpServerFactory<T, S> {

  @Override
  public McpSchema.ServerCapabilities serverCapabilities() {
    return McpSchema.ServerCapabilities.builder()
        .resources(true, true)
        .prompts(true)
        .tools(true)
        .build();
  }
}
