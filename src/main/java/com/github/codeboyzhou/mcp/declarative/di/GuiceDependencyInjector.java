package com.github.codeboyzhou.mcp.declarative.di;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;

/**
 * This class implements the {@link DependencyInjector} interface using Google Guice.
 *
 * @author codeboyzhou
 */
public final class GuiceDependencyInjector implements DependencyInjector {

  /** The Guice {@link Injector} used for dependency injection. */
  private final Injector injector;

  /**
   * Creates a new instance of {@code GuiceDependencyInjector} with the specified Guice {@link
   * Injector}.
   *
   * @param injector the Guice {@link Injector} to use for dependency injection
   */
  public GuiceDependencyInjector(Injector injector) {
    this.injector = injector;
  }

  @Override
  public <T> T getInstance(Class<T> type) {
    if (isInitialized()) {
      return injector.getInstance(type);
    }
    throw new IllegalStateException("GuiceDependencyInjector is not initialized");
  }

  @Override
  public <T> T getVariable(Class<T> type, String name) {
    if (isInitialized()) {
      return injector.getInstance(Key.get(type, Names.named(name)));
    }
    throw new IllegalStateException("GuiceDependencyInjector is not initialized");
  }

  @Override
  public boolean isInitialized() {
    return injector != null;
  }
}
