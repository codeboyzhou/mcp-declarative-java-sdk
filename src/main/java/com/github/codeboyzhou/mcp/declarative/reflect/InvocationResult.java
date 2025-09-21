package com.github.codeboyzhou.mcp.declarative.reflect;

import com.github.codeboyzhou.mcp.declarative.common.Immutable;
import org.jetbrains.annotations.NotNull;

public record InvocationResult(@NotNull Object result, Immutable<Exception> exception) {

  public boolean isError() {
    return exception != null && exception.get() != null;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {

    private Object result;

    private Immutable<Exception> exception;

    public Builder result(Object result) {
      this.result = result;
      return this;
    }

    public Builder exception(Exception exception) {
      this.exception = Immutable.of(exception);
      return this;
    }

    public InvocationResult build() {
      return new InvocationResult(result, exception);
    }
  }
}
