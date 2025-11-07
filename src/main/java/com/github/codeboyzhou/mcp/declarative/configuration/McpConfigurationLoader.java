package com.github.codeboyzhou.mcp.declarative.configuration;

import com.github.codeboyzhou.mcp.declarative.exception.McpServerConfigurationException;
import com.github.codeboyzhou.mcp.declarative.util.JacksonHelper;
import com.github.codeboyzhou.mcp.declarative.util.StringHelper;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This record represents a YAML configuration loader for MCP (Model Context Protocol) server
 * configuration.
 *
 * <p>It loads the server configuration from a specified YAML file. If no file name is provided, the
 * default file name "mcp-server.yml" will be used.
 *
 * @see <a href="https://codeboyzhou.github.io/mcp-declarative-java-sdk/getting-started">MCP
 *     Declarative Java SDK Documentation</a>
 * @author codeboyzhou
 */
public record McpConfigurationLoader(String configFileName) {

  private static final Logger log = LoggerFactory.getLogger(McpConfigurationLoader.class);

  /** The default file name for the MCP server configuration file. */
  private static final String DEFAULT_CONFIG_FILE_NAME = "mcp-server.yml";

  /** Constructs a YAMLConfigurationLoader with the default configuration file name. */
  public McpConfigurationLoader() {
    this(DEFAULT_CONFIG_FILE_NAME);
  }

  /**
   * Loads the MCP server configuration from the specified YAML file.
   *
   * @return the loaded MCP server configuration
   * @throws McpServerConfigurationException if the configuration file cannot be loaded
   */
  public McpServerConfiguration loadConfig() {
    Path configFilePath = getConfigFilePath(configFileName);
    File file = configFilePath.toFile();
    McpServerConfiguration baseConfig = JacksonHelper.fromYaml(file, McpServerConfiguration.class);
    log.info("Configuration loaded successfully from file: {}", configFileName);

    final String profile = baseConfig.profile();
    if (StringHelper.isBlank(profile)) {
      log.info("No profile specified in configuration file: {}", configFileName);
      McpConfigurationChecker.check(baseConfig);
      return baseConfig;
    }

    final String profileConfigFileName = configFileName.replace(".yml", "-" + profile + ".yml");
    Path profileConfigFilePath = getConfigFilePath(profileConfigFileName);
    File profileConfigFile = profileConfigFilePath.toFile();
    McpServerConfiguration profileConfig =
        JacksonHelper.fromYaml(profileConfigFile, McpServerConfiguration.class);
    log.info("Profile configuration loaded successfully from file: {}", profileConfigFileName);

    return McpConfigurationMerger.merge(baseConfig, profileConfig);
  }

  /**
   * Returns the file path of the configuration file.
   *
   * @param fileName the name of the configuration file
   * @return the file path of the configuration file
   * @throws McpServerConfigurationException if the configuration file cannot be found
   */
  private Path getConfigFilePath(String fileName) {
    try {
      ClassLoader classLoader = McpConfigurationLoader.class.getClassLoader();
      URL configFileUrl = classLoader.getResource(fileName);
      if (configFileUrl == null) {
        throw new McpServerConfigurationException("Configuration file not found: " + fileName);
      }
      return Paths.get(configFileUrl.toURI());
    } catch (URISyntaxException e) {
      // should never happen
      throw new McpServerConfigurationException("Invalid configuration file: " + fileName, e);
    }
  }
}
