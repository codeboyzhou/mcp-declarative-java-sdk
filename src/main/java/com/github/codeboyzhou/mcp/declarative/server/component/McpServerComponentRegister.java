package com.github.codeboyzhou.mcp.declarative.server.component;

import com.github.codeboyzhou.mcp.declarative.annotation.McpPrompt;
import com.github.codeboyzhou.mcp.declarative.annotation.McpPromptCompletion;
import com.github.codeboyzhou.mcp.declarative.annotation.McpResource;
import com.github.codeboyzhou.mcp.declarative.annotation.McpResourceCompletion;
import com.github.codeboyzhou.mcp.declarative.annotation.McpTool;
import com.github.codeboyzhou.mcp.declarative.di.DependencyInjector;
import com.github.codeboyzhou.mcp.declarative.di.DependencyInjectorProvider;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import org.reflections.Reflections;

/**
 * Central registry for MCP server components including resources, prompts, tools, and completions.
 *
 * <p>This class is responsible for discovering, creating, and registering all MCP server components
 * with the appropriate server instances. It uses reflection to scan for methods annotated with MCP
 * annotations and creates the corresponding server components using dependency injection.
 *
 * <p>The register supports the following component types:
 *
 * <ul>
 *   <li>{@link McpResource} - Server resources that can be accessed by clients
 *   <li>{@link McpPrompt} - Interactive prompts for user input
 *   <li>{@link McpTool} - Executable tools that perform specific operations
 *   <li>{@link McpPromptCompletion} - Auto-completion for prompts
 *   <li>{@link McpResourceCompletion} - Auto-completion for resources
 * </ul>
 *
 * <p>This class is designed to be used once during server initialization to register all available
 * components. It leverages the dependency injection system to obtain necessary dependencies and
 * create component instances.
 *
 * @author codeboyzhou
 */
public final class McpServerComponentRegister {
  /** The dependency injector for MCP server components. */
  private final DependencyInjector injector;

  /** The reflections instance for MCP server components. */
  private final Reflections reflections;

  /**
   * Creates a new instance of {@code McpServerComponentRegister}.
   *
   * <p>This constructor initializes the dependency injector and reflections instance that will be
   * used for component discovery and creation. The dependency injector is obtained from the {@link
   * DependencyInjectorProvider} and is used to create component instances and obtain the
   * reflections scanner.
   */
  public McpServerComponentRegister() {
    this.injector = DependencyInjectorProvider.INSTANCE.getInjector();
    this.reflections = injector.getInstance(Reflections.class);
  }

  /**
   * Registers all completion specifications for prompts and resources.
   *
   * <p>This method scans for methods annotated with {@link McpPromptCompletion} and {@link
   * McpResourceCompletion} annotations, creates the corresponding completion specifications, and
   * returns them as a list. The returned specifications can be used to configure the server's
   * completion capabilities.
   *
   * <p>The method uses reflection to discover all annotated methods and creates completion
   * specifications using the {@link McpServerCompletion} component.
   *
   * @return a list of synchronous completion specifications for all discovered prompt and resource
   *     completion methods
   * @throws RuntimeException if component creation fails for any method
   */
  public List<McpServerFeatures.SyncCompletionSpecification> registerCompletions() {
    List<McpServerFeatures.SyncCompletionSpecification> completions = new ArrayList<>();
    completions.addAll(register(McpPromptCompletion.class));
    completions.addAll(register(McpResourceCompletion.class));
    return completions;
  }

  /**
   * Registers all MCP server components (resources, prompts, and tools) with the specified server.
   *
   * <p>This method is the main entry point for component registration. It scans for methods
   * annotated with {@link McpResource}, {@link McpPrompt}, and {@link McpTool} annotations, creates
   * the corresponding server components, and registers them with the provided MCP server instance.
   *
   * <p>Each component type is processed by its respective server component class:
   *
   * <ul>
   *   <li>Resources are handled by {@link McpServerResource}
   *   <li>Prompts are handled by {@link McpServerPrompt}
   *   <li>Tools are handled by {@link McpServerTool}
   * </ul>
   *
   * @param server the MCP server to register components with
   * @throws IllegalArgumentException if server is null
   * @throws RuntimeException if component registration fails
   */
  public void registerComponents(McpSyncServer server) {
    register(server, McpResource.class, McpServerResource.class, McpSyncServer::addResource);
    register(server, McpPrompt.class, McpServerPrompt.class, McpSyncServer::addPrompt);
    register(server, McpTool.class, McpServerTool.class, McpSyncServer::addTool);
  }

  /**
   * Registers MCP server components of a specific type with the specified server.
   *
   * <p>This generic method handles the registration of a specific component type by:
   *
   * <ol>
   *   <li>Scanning for methods annotated with the specified annotation
   *   <li>Creating an instance of the appropriate component class
   *   <li>Creating server components for each discovered method
   *   <li>Registering each component with the server using the provided consumer
   * </ol>
   *
   * <p>This method is used internally by {@link #registerComponents(McpSyncServer)} and provides a
   * template for component registration that can be extended to support additional component types
   * in the future.
   *
   * @param server the MCP server to register components with
   * @param annotationClass the annotation class to use for component discovery
   * @param componentClass the component class to use for component creation
   * @param serverAddComponentConsumer the method reference for adding components to the server
   * @param <T> the type of the component to register
   */
  private <T> void register(
      McpSyncServer server,
      Class<? extends Annotation> annotationClass,
      Class<? extends McpServerComponent<T>> componentClass,
      BiConsumer<McpSyncServer, T> serverAddComponentConsumer) {

    Set<Method> methods = reflections.getMethodsAnnotatedWith(annotationClass);
    McpServerComponent<T> component = injector.getInstance(componentClass);
    for (Method method : methods) {
      serverAddComponentConsumer.accept(server, component.create(method));
    }
  }

  /**
   * Registers completion specifications for a specific annotation type.
   *
   * <p>This overloaded private method handles the registration of completion specifications for a
   * specific annotation type (either {@link McpPromptCompletion} or {@link McpResourceCompletion}).
   * It scans for methods annotated with the specified annotation, creates completion specifications
   * using {@link McpServerCompletion}, and returns them as a list.
   *
   * <p>This method is used internally by {@link #registerCompletions()} to separate the
   * registration logic for different completion types while maintaining a common implementation
   * pattern.
   *
   * @param annotationClass the annotation class to scan for (prompt or resource completion)
   * @return a list of completion specifications for all methods annotated with the specified
   *     annotation
   */
  private List<McpServerFeatures.SyncCompletionSpecification> register(
      Class<? extends Annotation> annotationClass) {

    Set<Method> methods = reflections.getMethodsAnnotatedWith(annotationClass);
    McpServerComponent<McpServerFeatures.SyncCompletionSpecification> completion =
        injector.getInstance(McpServerCompletion.class);
    List<McpServerFeatures.SyncCompletionSpecification> completions = new ArrayList<>();
    for (Method method : methods) {
      completions.add(completion.create(method));
    }
    return completions;
  }
}
