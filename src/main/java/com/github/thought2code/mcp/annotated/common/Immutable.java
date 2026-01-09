package com.github.thought2code.mcp.annotated.common;

/**
 * A simple immutable wrapper class for a single value. Instances of this class are immutable,
 * meaning that once created, their value cannot be changed.
 *
 * @param <T> the type of the value
 * @author codeboyzhou
 */
public record Immutable<T>(T value) {

  /**
   * Creates a new instance of {@code Immutable} with the specified value.
   *
   * @param value the value to be wrapped
   * @param <T> the type of the value
   * @return a new instance of {@code Immutable} with the specified value
   */
  public static <T> Immutable<T> of(T value) {
    return new Immutable<>(value);
  }

  /**
   * Returns the value wrapped by this {@code Immutable} instance. It can also use {@link #value()}
   * to get the value, but {@code #get()} is more readable.
   *
   * @return the value wrapped by this {@code Immutable} instance
   */
  public T get() {
    return value;
  }
}
