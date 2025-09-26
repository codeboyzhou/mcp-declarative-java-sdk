package com.github.codeboyzhou.mcp.declarative.di;

/**
 * This enum provides a singleton instance of {@link DependencyInjectorProvider} that can be used to
 * initialize and retrieve the {@link DependencyInjector} instance.
 *
 * @author codeboyzhou
 */
public enum DependencyInjectorProvider {

  /** The singleton instance of {@link DependencyInjectorProvider}. */
  INSTANCE;

  /** The {@link DependencyInjector} instance. */
  private volatile DependencyInjector injector;

  /**
   * Initializes the {@link DependencyInjectorProvider} with the specified {@link
   * DependencyInjector}.
   *
   * @param injector the {@link DependencyInjector} to initialize with
   * @return the {@link DependencyInjectorProvider} instance
   */
  public DependencyInjectorProvider initialize(DependencyInjector injector) {
    if (this.injector == null) {
      synchronized (this) {
        if (this.injector == null) {
          this.injector = injector;
        }
      }
    }
    return this;
  }

  /**
   * Returns the {@link DependencyInjector} instance.
   *
   * @return the {@link DependencyInjector} instance
   * @throws IllegalStateException if the {@link DependencyInjector} has not been initialized yet
   */
  public DependencyInjector getInjector() {
    DependencyInjector current = this.injector;
    if (current == null) {
      throw new IllegalStateException("DependencyInjector has not been initialized yet");
    }
    return current;
  }
}
