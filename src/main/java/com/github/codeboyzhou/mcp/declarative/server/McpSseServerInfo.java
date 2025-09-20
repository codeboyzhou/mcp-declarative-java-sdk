package com.github.codeboyzhou.mcp.declarative.server;

import com.github.codeboyzhou.mcp.declarative.util.StringHelper;

public class McpSseServerInfo extends McpServerInfo {

  private final String baseUrl;

  private final String messageEndpoint;

  private final String sseEndpoint;

  private final int port;

  private McpSseServerInfo(McpSseServerInfo.Builder builder) {
    super(builder);
    this.baseUrl = builder.baseUrl;
    this.messageEndpoint = builder.messageEndpoint;
    this.sseEndpoint = builder.sseEndpoint;
    this.port = builder.port;
  }

  public static McpSseServerInfo.Builder builder() {
    return new McpSseServerInfo.Builder();
  }

  public String baseUrl() {
    return baseUrl;
  }

  public String messageEndpoint() {
    return messageEndpoint;
  }

  public String sseEndpoint() {
    return sseEndpoint;
  }

  public int port() {
    return port;
  }

  public static class Builder extends McpServerInfo.Builder<McpSseServerInfo.Builder> {

    private String baseUrl = StringHelper.EMPTY;

    private String messageEndpoint = "/mcp/message";

    private String sseEndpoint = "/sse";

    private int port = 8080;

    @Override
    protected McpSseServerInfo.Builder self() {
      return this;
    }

    @Override
    public McpSseServerInfo build() {
      return new McpSseServerInfo(this);
    }

    public McpSseServerInfo.Builder baseUrl(String baseUrl) {
      this.baseUrl = baseUrl;
      return self();
    }

    public McpSseServerInfo.Builder messageEndpoint(String messageEndpoint) {
      this.messageEndpoint = messageEndpoint;
      return self();
    }

    public McpSseServerInfo.Builder sseEndpoint(String sseEndpoint) {
      this.sseEndpoint = sseEndpoint;
      return self();
    }

    public McpSseServerInfo.Builder port(int port) {
      this.port = port;
      return self();
    }
  }
}
