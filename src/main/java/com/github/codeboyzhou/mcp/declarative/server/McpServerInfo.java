package com.github.codeboyzhou.mcp.declarative.server;

import com.github.codeboyzhou.mcp.declarative.configuration.McpServerCapabilities;
import com.github.codeboyzhou.mcp.declarative.configuration.McpServerChangeNotification;
import com.github.codeboyzhou.mcp.declarative.util.StringHelper;
import java.time.Duration;

public class McpServerInfo {

  private final String name;

  private final String version;

  private final String instructions;

  private final Duration requestTimeout;

  private final McpServerCapabilities capabilities;

  private final McpServerChangeNotification changeNotification;

  protected McpServerInfo(Builder<?> builder) {
    this.name = builder.name;
    this.version = builder.version;
    this.instructions = builder.instructions;
    this.requestTimeout = builder.requestTimeout;
    this.capabilities = builder.capabilities;
    this.changeNotification = builder.changeNotification;
  }

  public static Builder<?> builder() {
    return new Builder<>();
  }

  public String name() {
    return name;
  }

  public String version() {
    return version;
  }

  public String instructions() {
    return instructions;
  }

  public Duration requestTimeout() {
    return requestTimeout;
  }

  public McpServerCapabilities capabilities() {
    return capabilities;
  }

  public McpServerChangeNotification changeNotification() {
    return changeNotification;
  }

  @SuppressWarnings("unchecked")
  public static class Builder<T extends Builder<T>> {

    protected String name = "mcp-server";

    protected String version = "1.0.0";

    protected String instructions = StringHelper.EMPTY;

    protected Duration requestTimeout = Duration.ofSeconds(20);

    protected McpServerCapabilities capabilities = new McpServerCapabilities();

    protected McpServerChangeNotification changeNotification = new McpServerChangeNotification();

    protected T self() {
      return (T) this;
    }

    public McpServerInfo build() {
      return new McpServerInfo(this);
    }

    public T name(String name) {
      this.name = name;
      return self();
    }

    public T version(String version) {
      this.version = version;
      return self();
    }

    public T instructions(String instructions) {
      this.instructions = instructions;
      return self();
    }

    public T requestTimeout(Duration requestTimeout) {
      this.requestTimeout = requestTimeout;
      return self();
    }

    public T capabilities(McpServerCapabilities capabilities) {
      this.capabilities = capabilities;
      return self();
    }

    public T changeNotification(McpServerChangeNotification changeNotification) {
      this.changeNotification = changeNotification;
      return self();
    }
  }
}
