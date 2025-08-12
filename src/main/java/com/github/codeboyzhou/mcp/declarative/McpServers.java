package com.github.codeboyzhou.mcp.declarative;

import com.github.codeboyzhou.mcp.declarative.common.GuiceInjectorModule;
import com.github.codeboyzhou.mcp.declarative.configuration.McpServerConfiguration;
import com.github.codeboyzhou.mcp.declarative.configuration.YAMLConfigurationLoader;
import com.github.codeboyzhou.mcp.declarative.server.McpServerInfo;
import com.github.codeboyzhou.mcp.declarative.server.McpSseServerInfo;
import com.github.codeboyzhou.mcp.declarative.server.McpStreamableServerInfo;
import com.github.codeboyzhou.mcp.declarative.server.factory.ConfigurableMcpHttpSseServerFactory;
import com.github.codeboyzhou.mcp.declarative.server.factory.ConfigurableMcpServerFactory;
import com.github.codeboyzhou.mcp.declarative.server.factory.ConfigurableMcpStdioServerFactory;
import com.github.codeboyzhou.mcp.declarative.server.factory.McpHttpSseServerFactory;
import com.github.codeboyzhou.mcp.declarative.server.factory.McpHttpStreamableServerFactory;
import com.github.codeboyzhou.mcp.declarative.server.factory.McpServerPromptFactory;
import com.github.codeboyzhou.mcp.declarative.server.factory.McpServerResourceFactory;
import com.github.codeboyzhou.mcp.declarative.server.factory.McpServerToolFactory;
import com.github.codeboyzhou.mcp.declarative.server.factory.McpStdioServerFactory;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.modelcontextprotocol.server.McpAsyncServer;
import io.modelcontextprotocol.spec.McpServerTransportProvider;
import io.modelcontextprotocol.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class McpServers {

  private static final Logger logger = LoggerFactory.getLogger(McpServers.class);

  private static final McpServers INSTANCE = new McpServers();

  private static Injector injector;

  private McpServers() {
    // Using singleton design pattern should have private constructor
  }

  public static McpServers run(Class<?> applicationMainClass, String[] args) {
    injector = Guice.createInjector(new GuiceInjectorModule(applicationMainClass));
    return INSTANCE;
  }

  public void startStdioServer(McpServerInfo serverInfo) {
    McpStdioServerFactory factory = new McpStdioServerFactory();
    McpAsyncServer server = factory.create(serverInfo);
    registerComponents(server);
  }

  public void startSseServer(McpSseServerInfo serverInfo) {
    McpHttpSseServerFactory factory = new McpHttpSseServerFactory();
    McpAsyncServer server = factory.create(serverInfo);
    registerComponents(server);
  }

  public void startStreamableServer(McpStreamableServerInfo serverInfo) {
    McpHttpStreamableServerFactory factory = new McpHttpStreamableServerFactory();
    McpAsyncServer server = factory.create(serverInfo);
    registerComponents(server);
  }

  public void startServer(String configFileName) {
    Assert.notNull(configFileName, "configFileName must not be null");
    YAMLConfigurationLoader configLoader = new YAMLConfigurationLoader(configFileName);
    doStartServer(configLoader.loadConfig());
  }

  public void startServer() {
    YAMLConfigurationLoader configLoader = new YAMLConfigurationLoader();
    doStartServer(configLoader.loadConfig());
  }

  private void doStartServer(McpServerConfiguration configuration) {
    if (!configuration.enabled()) {
      logger.warn("MCP server is disabled, please check your configuration file.");
      return;
    }

    ConfigurableMcpServerFactory<? extends McpServerTransportProvider> factory;
    if (configuration.stdio()) {
      factory = new ConfigurableMcpStdioServerFactory(configuration);
    } else {
      factory = new ConfigurableMcpHttpSseServerFactory(configuration);
    }
    McpAsyncServer server = factory.create();
    registerComponents(server);
  }

  private void registerComponents(McpAsyncServer server) {
    injector.getInstance(McpServerResourceFactory.class).registerTo(server);
    injector.getInstance(McpServerPromptFactory.class).registerTo(server);
    injector.getInstance(McpServerToolFactory.class).registerTo(server);
  }
}
