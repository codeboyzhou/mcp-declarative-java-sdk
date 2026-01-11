package com.github.thought2code.mcp.annotated;

import com.github.thought2code.mcp.annotated.configuration.McpConfigurationLoader;
import com.github.thought2code.mcp.annotated.configuration.McpServerConfiguration;
import com.github.thought2code.mcp.annotated.di.DependencyInjector;
import com.github.thought2code.mcp.annotated.di.DependencyInjectorProvider;
import com.github.thought2code.mcp.annotated.di.GuiceDependencyInjector;
import com.github.thought2code.mcp.annotated.di.GuiceInjectorModule;
import com.github.thought2code.mcp.annotated.enums.ServerMode;
import com.github.thought2code.mcp.annotated.server.McpServerInfo;
import com.github.thought2code.mcp.annotated.server.McpSseServer;
import com.github.thought2code.mcp.annotated.server.McpSseServerInfo;
import com.github.thought2code.mcp.annotated.server.McpStdioServer;
import com.github.thought2code.mcp.annotated.server.McpStreamableServer;
import com.github.thought2code.mcp.annotated.server.McpStreamableServerInfo;
import com.github.thought2code.mcp.annotated.server.configurable.ConfigurableMcpServerFactory;
import com.google.inject.Guice;
import io.modelcontextprotocol.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is a singleton that provides methods to start MCP servers.
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * McpServerInfo serverInfo = McpServerInfo.builder().build();
 * McpServers.run(MyApplication.class, args).startStdioServer(serverInfo);
 *
 * McpSseServerInfo sseServerInfo = McpSseServerInfo.builder().build();
 * McpServers.run(MyApplication.class, args).startSseServer(sseServerInfo);
 *
 * McpStreamableServerInfo streamableServerInfo = McpStreamableServerInfo.builder().build();
 * McpServers.run(MyApplication.class, args).startStreamableServer(streamableServerInfo);
 *
 * McpServers.run(MyApplication.class, args).startServer("mcp-server-config.yml");
 *
 * McpServers.run(MyApplication.class, args).startServer();
 * }</pre>
 *
 * @see <a href="https://thought2code.github.io/mcp-annotated-java-sdk/getting-started">MCP
 *     Annotated Java SDK Documentation</a>
 * @author codeboyzhou
 */
public final class McpServers {

  private static final Logger log = LoggerFactory.getLogger(McpServers.class);

  /** The singleton instance of McpServers. */
  private static final McpServers INSTANCE = new McpServers();

  /** The dependency injector used to inject server components. */
  private static DependencyInjector injector;

  /**
   * The constructor of McpServers. Using singleton design pattern should have private constructor.
   */
  private McpServers() {}

  /**
   * Initializes the McpServers instance with the specified application main class and arguments.
   *
   * @param applicationMainClass the main class of the application
   * @param args the arguments passed to the application
   * @return the singleton instance of McpServers
   */
  public static McpServers run(Class<?> applicationMainClass, String[] args) {
    GuiceInjectorModule module = new GuiceInjectorModule(applicationMainClass);
    DependencyInjector injector = new GuiceDependencyInjector(Guice.createInjector(module));
    DependencyInjectorProvider.INSTANCE.initialize(injector);
    McpServers.injector = injector;
    return INSTANCE;
  }

  /**
   * Starts a standard input/output (stdio) server with the specified server info.
   *
   * @param serverInfo the server info for the stdio server
   */
  @Deprecated(since = "0.11.0", forRemoval = true)
  public void startStdioServer(McpServerInfo serverInfo) {
    injector.getInstance(McpStdioServer.class).start(serverInfo);
  }

  /**
   * Starts a standard input/output (stdio) server with the specified server configuration.
   *
   * @param configuration the server configuration builder for the stdio server
   */
  public void startStdioServer(McpServerConfiguration.Builder configuration) {
    configuration.enabled(true).mode(ServerMode.STDIO);
    doStartServer(configuration.build());
  }

  /**
   * Starts an http server-sent events (sse) server with the specified server info.
   *
   * @param serverInfo the server info for the sse server
   */
  @Deprecated(since = "0.11.0", forRemoval = true)
  public void startSseServer(McpSseServerInfo serverInfo) {
    injector.getInstance(McpSseServer.class).start(serverInfo);
  }

  /**
   * Starts an http server-sent events (sse) server with the specified server configuration.
   *
   * @param configuration the server configuration builder for the sse server
   */
  public void startSseServer(McpServerConfiguration.Builder configuration) {
    configuration.enabled(true).mode(ServerMode.SSE);
    doStartServer(configuration.build());
  }

  /**
   * Starts a streamable http server with the specified server info.
   *
   * @param serverInfo the server info for the streamable server
   */
  @Deprecated(since = "0.11.0", forRemoval = true)
  public void startStreamableServer(McpStreamableServerInfo serverInfo) {
    injector.getInstance(McpStreamableServer.class).start(serverInfo);
  }

  /**
   * Starts a streamable http server with the specified server configuration.
   *
   * @param configuration the server configuration builder for the streamable server
   */
  public void startStreamableServer(McpServerConfiguration.Builder configuration) {
    configuration.enabled(true).mode(ServerMode.STREAMABLE);
    doStartServer(configuration.build());
  }

  /**
   * Starts a server with the specified configuration file name.
   *
   * @param configFileName the name of the configuration file
   */
  public void startServer(String configFileName) {
    Assert.notNull(configFileName, "configFileName must not be null");
    McpConfigurationLoader configLoader = new McpConfigurationLoader(configFileName);
    doStartServer(configLoader.loadConfig());
  }

  /** Starts a server with the default configuration file name. */
  public void startServer() {
    McpConfigurationLoader configLoader = new McpConfigurationLoader();
    doStartServer(configLoader.loadConfig());
  }

  /**
   * Starts a server with the specified server configuration.
   *
   * @param configuration the server configuration
   */
  private void doStartServer(McpServerConfiguration configuration) {
    if (configuration.enabled()) {
      ConfigurableMcpServerFactory.getServer(configuration).startServer();
    } else {
      log.warn("MCP server is disabled, please check your configuration file.");
    }
  }
}
