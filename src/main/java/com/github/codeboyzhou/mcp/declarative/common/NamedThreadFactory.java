package com.github.codeboyzhou.mcp.declarative.common;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A thread factory that creates threads with a specified name prefix.
 *
 * @author codeboyzhou
 */
public final class NamedThreadFactory implements ThreadFactory {

  private static final Logger log = LoggerFactory.getLogger(NamedThreadFactory.class);

  /** The pool number counter for threads created by this factory. */
  private static final AtomicInteger poolNumber = new AtomicInteger(1);

  /** The thread number counter for threads created by this factory. */
  private final AtomicInteger threadNumber = new AtomicInteger(1);

  /** The name prefix for threads created by this factory. */
  private final String namePrefix;

  /**
   * Creates a new instance of {@code NamedThreadFactory} with the specified name prefix.
   *
   * @param namePrefix the name prefix for threads created by this factory
   */
  public NamedThreadFactory(String namePrefix) {
    this.namePrefix = namePrefix + "-" + poolNumber.getAndIncrement() + "-thread-";
  }

  @Override
  public Thread newThread(@NotNull Runnable runnable) {
    Thread thread = new Thread(runnable, namePrefix + threadNumber.getAndIncrement());
    thread.setUncaughtExceptionHandler(this::handleUncaughtException);
    thread.setDaemon(true);
    return thread;
  }

  /**
   * Handles uncaught exceptions for threads created by this factory.
   *
   * @param t the thread that threw the exception
   * @param e the exception that was thrown
   */
  private void handleUncaughtException(Thread t, Throwable e) {
    log.error("Thread {} uncaught exception", t.getName(), e);
  }
}
