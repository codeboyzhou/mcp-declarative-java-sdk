package com.github.thought2code.mcp.annotated.di;

/**
 * This interface defines the contract for a dependency injector.
 *
 * <p>It provides methods to retrieve instances and variables of a specific type.
 *
 * @author codeboyzhou
 */
public interface DependencyInjector {
  /**
   * Returns an instance of the specified type.
   *
   * @param type the class of the instance to return
   * @param <T> the type of the instance to return
   * @return an instance of the specified type
   */
  <T> T getInstance(Class<T> type);

  /**
   * Returns the value of the specified variable.
   *
   * @param type the class of the variable to return
   * @param name the name of the variable to return
   * @param <T> the type of the variable to return
   * @return the value of the specified variable
   */
  <T> T getVariable(Class<T> type, String name);

  /**
   * Returns whether the dependency injector is initialized.
   *
   * @return {@code true} if the dependency injector is initialized, {@code false} otherwise
   */
  boolean isInitialized();
}
