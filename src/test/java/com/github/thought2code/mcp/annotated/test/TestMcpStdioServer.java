package com.github.thought2code.mcp.annotated.test;

import com.github.thought2code.mcp.annotated.McpServers;
import com.github.thought2code.mcp.annotated.server.McpServerInfo;
import java.time.Duration;

public class TestMcpStdioServer {

  public static void main(String[] args) {
    McpServerInfo serverInfo =
        McpServerInfo.builder()
            .name("mcp-server")
            .version("1.0.0")
            .instructions("test")
            .requestTimeout(Duration.ofSeconds(60))
            .build();
    McpServers.run(TestMcpStdioServer.class, args).startStdioServer(serverInfo);
  }
}
