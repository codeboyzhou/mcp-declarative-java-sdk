package com.github.thought2code.mcp.annotated.server;

import com.github.thought2code.mcp.annotated.configuration.McpServerCapabilities;
import com.github.thought2code.mcp.annotated.configuration.McpServerChangeNotification;
import com.github.thought2code.mcp.annotated.server.component.McpServerComponentRegister;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.spec.McpSchema;

/**
 * This abstract class is used to provide a base implementation for an MCP server.
 *
 * @author codeboyzhou
 */
public abstract class AbstractMcpServer<S extends McpServerInfo> implements McpServer<S> {

  /**
   * Warms up the MCP server with the specified server info.
   *
   * @param serverInfo the server info
   */
  public void warmup(S serverInfo) {
    McpServerComponentRegister register = new McpServerComponentRegister();
    McpSyncServer server =
        sync(serverInfo)
            .serverInfo(serverInfo.name(), serverInfo.version())
            .capabilities(serverCapabilities(serverInfo))
            .instructions(serverInfo.instructions())
            .requestTimeout(serverInfo.requestTimeout())
            .completions(register.registerCompletions())
            .build();
    register.registerComponents(server);
  }

  /**
   * Returns the server capabilities for the MCP server.
   *
   * @param serverInfo the server info
   * @return the server capabilities for the MCP server
   */
  private McpSchema.ServerCapabilities serverCapabilities(S serverInfo) {
    McpSchema.ServerCapabilities.Builder capabilities = McpSchema.ServerCapabilities.builder();
    McpServerCapabilities capabilitiesConfig = serverInfo.capabilities();
    McpServerChangeNotification serverChangeNotification = serverInfo.changeNotification();
    if (capabilitiesConfig.resource()) {
      final Boolean subscribe = capabilitiesConfig.subscribeResource();
      final Boolean changeNotification = serverChangeNotification.resource();
      capabilities.resources(subscribe, changeNotification);
    }
    if (capabilitiesConfig.prompt()) {
      capabilities.prompts(serverChangeNotification.prompt());
    }
    if (capabilitiesConfig.tool()) {
      capabilities.tools(serverChangeNotification.tool());
    }
    if (capabilitiesConfig.completion()) {
      capabilities.completions();
    }
    return capabilities.build();
  }
}
