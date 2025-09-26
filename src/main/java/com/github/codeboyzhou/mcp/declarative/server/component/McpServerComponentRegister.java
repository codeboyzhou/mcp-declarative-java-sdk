package com.github.codeboyzhou.mcp.declarative.server.component;

import com.github.codeboyzhou.mcp.declarative.annotation.McpPrompt;
import com.github.codeboyzhou.mcp.declarative.annotation.McpResource;
import com.github.codeboyzhou.mcp.declarative.annotation.McpTool;
import com.github.codeboyzhou.mcp.declarative.common.Immutable;
import com.github.codeboyzhou.mcp.declarative.di.DependencyInjector;
import com.github.codeboyzhou.mcp.declarative.di.DependencyInjectorProvider;
import io.modelcontextprotocol.server.McpSyncServer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.function.BiConsumer;
import org.reflections.Reflections;

/**
 * Registers MCP server components (resources, prompts, and tools) with the specified server.
 *
 * @author codeboyzhou
 */
public final class McpServerComponentRegister {

  /** The dependency injector for MCP server components. */
  private final DependencyInjector injector;

  /** The reflections instance for MCP server components. */
  private final Reflections reflections;

  /** The MCP sync server to register components with. */
  private final Immutable<McpSyncServer> server;

  /**
   * Creates a new instance of {@code McpServerComponentRegister} with the specified server.
   *
   * @param server the MCP sync server to register components with
   */
  public McpServerComponentRegister(McpSyncServer server) {
    this.injector = DependencyInjectorProvider.INSTANCE.getInjector();
    this.reflections = injector.getInstance(Reflections.class);
    this.server = Immutable.of(server);
  }

  /**
   * Creates a new instance of {@code McpServerComponentRegister} with the specified server.
   *
   * @param server the MCP sync server to register components with
   * @return a new instance of {@code McpServerComponentRegister} with the specified server
   */
  public static McpServerComponentRegister of(McpSyncServer server) {
    return new McpServerComponentRegister(server);
  }

  /** Registers MCP server components (resources, prompts, and tools) with the specified server. */
  public void registerComponents() {
    register(McpResource.class, McpServerResource.class, McpSyncServer::addResource);
    register(McpPrompt.class, McpServerPrompt.class, McpSyncServer::addPrompt);
    register(McpTool.class, McpServerTool.class, McpSyncServer::addTool);
  }

  /**
   * Registers MCP server components with the specified server.
   *
   * @param annotationClass the annotation class to use for component discovery
   * @param componentClass the component class to use for component creation
   * @param serverAddComponent the method to use for adding components to the server
   * @param <T> the type of the component to register
   */
  private <T> void register(
      Class<? extends Annotation> annotationClass,
      Class<? extends McpServerComponent<T>> componentClass,
      BiConsumer<McpSyncServer, T> serverAddComponent) {

    Set<Method> methods = reflections.getMethodsAnnotatedWith(annotationClass);
    McpServerComponent<T> component = injector.getInstance(componentClass);
    for (Method method : methods) {
      serverAddComponent.accept(server.get(), component.create(method));
    }
  }
}
