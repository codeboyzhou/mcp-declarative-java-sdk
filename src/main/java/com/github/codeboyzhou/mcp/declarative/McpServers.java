package com.github.codeboyzhou.mcp.declarative;

import com.github.codeboyzhou.mcp.declarative.configuration.McpServerConfiguration;
import com.github.codeboyzhou.mcp.declarative.configuration.YAMLConfigurationLoader;
import com.github.codeboyzhou.mcp.declarative.di.DependencyInjector;
import com.github.codeboyzhou.mcp.declarative.di.DependencyInjectorProvider;
import com.github.codeboyzhou.mcp.declarative.di.GuiceDependencyInjector;
import com.github.codeboyzhou.mcp.declarative.di.GuiceInjectorModule;
import com.github.codeboyzhou.mcp.declarative.server.McpServerInfo;
import com.github.codeboyzhou.mcp.declarative.server.McpSseServer;
import com.github.codeboyzhou.mcp.declarative.server.McpSseServerInfo;
import com.github.codeboyzhou.mcp.declarative.server.McpStdioServer;
import com.github.codeboyzhou.mcp.declarative.server.McpStreamableServer;
import com.github.codeboyzhou.mcp.declarative.server.McpStreamableServerInfo;
import com.github.codeboyzhou.mcp.declarative.server.configurable.ConfigurableMcpServerFactory;
import com.google.inject.Guice;
import io.modelcontextprotocol.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is a singleton that provides methods to start MCP servers.
 *
 * @apiNote Example usage:
 *     <pre>{@code
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
 * @see <a href="https://codeboyzhou.github.io/mcp-declarative-java-sdk/getting-started">MCP
 *     Declarative Java SDK Documentation</a>
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
  public void startStdioServer(McpServerInfo serverInfo) {
    injector.getInstance(McpStdioServer.class).start(serverInfo);
  }

  /**
   * Starts a http server-sent events (sse) server with the specified server info.
   *
   * @param serverInfo the server info for the sse server
   */
  public void startSseServer(McpSseServerInfo serverInfo) {
    injector.getInstance(McpSseServer.class).start(serverInfo);
  }

  /**
   * Starts a streamable http server with the specified server info.
   *
   * @param serverInfo the server info for the streamable server
   */
  public void startStreamableServer(McpStreamableServerInfo serverInfo) {
    injector.getInstance(McpStreamableServer.class).start(serverInfo);
  }

  /**
   * Starts a server with the specified configuration file name.
   *
   * @param configFileName the name of the configuration file
   */
  public void startServer(String configFileName) {
    Assert.notNull(configFileName, "configFileName must not be null");
    YAMLConfigurationLoader configLoader = new YAMLConfigurationLoader(configFileName);
    doStartServer(configLoader.loadConfig());
  }

  /** Starts a server with the default configuration file name. */
  public void startServer() {
    YAMLConfigurationLoader configLoader = new YAMLConfigurationLoader();
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
