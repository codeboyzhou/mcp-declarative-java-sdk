package com.github.codeboyzhou.mcp.declarative.server;

import com.github.codeboyzhou.mcp.declarative.util.Strings;
import java.time.Duration;

public class McpServerInfo {

  private final String name;

  private final String version;

  private final String instructions;

  private final Duration requestTimeout;

  protected McpServerInfo(Builder<?> builder) {
    this.name = builder.name;
    this.version = builder.version;
    this.instructions = builder.instructions;
    this.requestTimeout = builder.requestTimeout;
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

  @SuppressWarnings("unchecked")
  public static class Builder<T extends Builder<T>> {

    protected String name = "mcp-server";

    protected String version = "1.0.0";

    protected String instructions = Strings.EMPTY;

    protected Duration requestTimeout = Duration.ofSeconds(20);

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
  }
}
