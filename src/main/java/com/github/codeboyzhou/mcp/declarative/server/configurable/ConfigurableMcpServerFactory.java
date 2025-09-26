package com.github.codeboyzhou.mcp.declarative.server.configurable;

import com.github.codeboyzhou.mcp.declarative.configuration.McpServerConfiguration;

/**
 * This factory class is used to create instances of {@link AbstractConfigurableMcpServer}
 * implementations based on the specified {@link McpServerConfiguration}.
 *
 * @author codeboyzhou
 */
public final class ConfigurableMcpServerFactory {

  /** Factory class should not be instantiated */
  private ConfigurableMcpServerFactory() {
    throw new UnsupportedOperationException("Factory class should not be instantiated");
  }

  /**
   * Creates a new instance of {@link AbstractConfigurableMcpServer} implementations based on the
   * specified {@link McpServerConfiguration}.
   *
   * @param config the configuration to use for the server
   * @return a new instance of {@link AbstractConfigurableMcpServer}
   */
  public static AbstractConfigurableMcpServer getServer(McpServerConfiguration config) {
    return switch (config.mode()) {
      case STDIO -> new ConfigurableMcpStdioServer(config);
      case SSE -> new ConfigurableMcpSseServer(config);
      case STREAMABLE -> new ConfigurableMcpStreamableServer(config);
    };
  }
}
