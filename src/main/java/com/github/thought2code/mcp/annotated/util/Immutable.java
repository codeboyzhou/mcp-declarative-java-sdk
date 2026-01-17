package com.github.thought2code.mcp.annotated.util;

import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

/**
 * A wrapper class that provides immutable access to a value through a supplier.
 *
 * <p>This class encapsulates a value and provides thread-safe, immutable access to it through a
 * {@link Supplier}. Once created, the value cannot be modified, ensuring immutability and thread
 * safety in concurrent environments.
 *
 * <p>The class is generic and can wrap any type {@code T}. The value is stored internally as a
 * supplier that always returns the same value, providing a consistent interface for value access.
 *
 * <p>Use cases include:
 *
 * <ul>
 *   <li>Ensuring thread-safe access to shared values
 *   <li>Preventing modification of critical data
 *   <li>Providing a consistent supplier interface for value access
 * </ul>
 *
 * @param <T> the type of the wrapped value
 * @author codeboyzhou
 * @see Supplier
 */
public final class Immutable<T> {
  /** The supplier that provides access to the immutable value. */
  private final Supplier<T> value;

  /**
   * Private constructor that wraps the value in a supplier.
   *
   * <p>This constructor creates a supplier that always returns the provided value, ensuring that
   * the value cannot be modified after construction.
   *
   * @param value the value to wrap in an immutable container
   */
  private Immutable(T value) {
    this.value = () -> value;
  }

  /**
   * Creates a new {@code Immutable} instance wrapping the specified value.
   *
   * <p>This static factory method creates an immutable wrapper around the provided value. The
   * wrapped value cannot be modified after creation, ensuring thread-safe access.
   *
   * <p>The method is annotated with {@code @NotNull} to indicate that it never returns null.
   *
   * @param <T> the type of the value to wrap
   * @param value the value to wrap in an immutable container
   * @return a new Immutable instance containing the specified value
   * @see #Immutable(Object)
   */
  @NotNull
  public static <T> Immutable<T> of(T value) {
    return new Immutable<>(value);
  }

  /**
   * Returns the wrapped value.
   *
   * <p>This method retrieves the value stored in this immutable container. The value is returned
   * through the internal supplier, ensuring consistent access semantics.
   *
   * <p>The returned value is the same object that was provided to the {@link #of(Object)} factory
   * method.
   *
   * @return the wrapped value
   * @see Supplier#get()
   */
  public T get() {
    return value.get();
  }
}
