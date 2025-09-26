package com.github.codeboyzhou.mcp.declarative.server.configurable;

import com.github.codeboyzhou.mcp.declarative.configuration.McpServerConfiguration;
import io.modelcontextprotocol.json.McpJsonMapper;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;

/**
 * This class is used to create a new instance of {@link ConfigurableMcpStdioServer} based on the
 * specified {@link McpServerConfiguration} in STDIO mode.
 *
 * @author codeboyzhou
 */
public class ConfigurableMcpStdioServer extends AbstractConfigurableMcpServer {

  /**
   * Creates a new instance of {@link ConfigurableMcpStdioServer} with the specified configuration.
   *
   * @param configuration the configuration for the MCP server
   */
  public ConfigurableMcpStdioServer(McpServerConfiguration configuration) {
    super(configuration);
  }

  /**
   * Returns the sync specification for the MCP server in STDIO mode.
   *
   * <p>This method returns the sync specification for the MCP server in STDIO mode. The sync
   * specification is used to start the MCP server in STDIO mode.
   *
   * @return the sync specification for the MCP server in STDIO mode
   */
  @Override
  public McpServer.SyncSpecification<?> sync() {
    return McpServer.sync(new StdioServerTransportProvider(McpJsonMapper.getDefault()));
  }
}
