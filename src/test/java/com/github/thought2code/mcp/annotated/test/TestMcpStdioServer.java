package com.github.thought2code.mcp.annotated.test;

import com.github.thought2code.mcp.annotated.McpServers;
import com.github.thought2code.mcp.annotated.configuration.McpServerConfiguration;

public class TestMcpStdioServer {

  public static void main(String[] args) {
    McpServerConfiguration.Builder configuration =
        McpServerConfiguration.builder()
            .name("mcp-server")
            .version("1.0.0")
            .instructions("test")
            .requestTimeout(60000L);
    McpServers.run(TestMcpStdioServer.class, args).startStdioServer(configuration);
  }
}
