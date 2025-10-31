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
public record YAMLConfigurationLoader(String configFileName) {

  private static final Logger log = LoggerFactory.getLogger(YAMLConfigurationLoader.class);

  /** The default file name for the MCP server configuration file. */
  private static final String DEFAULT_CONFIG_FILE_NAME = "mcp-server.yml";

  /** Constructs a YAMLConfigurationLoader with the default configuration file name. */
  public YAMLConfigurationLoader() {
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
      return baseConfig;
    }

    final String profileConfigFileName = configFileName.replace(".yml", "-" + profile + ".yml");
    Path profileConfigFilePath = getConfigFilePath(profileConfigFileName);
    File profileConfigFile = profileConfigFilePath.toFile();
    McpServerConfiguration profileConfig =
        JacksonHelper.fromYaml(profileConfigFile, McpServerConfiguration.class);
    log.info("Profile configuration loaded successfully from file: {}", profileConfigFileName);

    return mergeConfigurations(baseConfig, profileConfig);
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
      ClassLoader classLoader = YAMLConfigurationLoader.class.getClassLoader();
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

  /**
   * Merges the base configuration with the profile configuration.
   *
   * @param base the base configuration
   * @param profile the profile configuration
   * @return the merged configuration
   */
  private McpServerConfiguration mergeConfigurations(
      McpServerConfiguration base, McpServerConfiguration profile) {
    return new McpServerConfiguration(
        base.profile(),
        mergeValue(base.enabled(), profile.enabled()),
        mergeValue(base.mode(), profile.mode()),
        mergeString(base.name(), profile.name()),
        mergeString(base.version(), profile.version()),
        mergeValue(base.type(), profile.type()),
        mergeString(base.instructions(), profile.instructions()),
        mergeValue(base.requestTimeout(), profile.requestTimeout()),
        mergeCapabilities(base.capabilities(), profile.capabilities()),
        mergeChangeNotification(base.changeNotification(), profile.changeNotification()),
        mergeSSE(base.sse(), profile.sse()),
        mergeStreamable(base.streamable(), profile.streamable()));
  }

  /**
   * Merges the base value with the profile value.
   *
   * @param baseValue the base value
   * @param profileValue the profile value
   * @return the merged value
   */
  private <T> T mergeValue(T baseValue, T profileValue) {
    return profileValue == null ? baseValue : profileValue;
  }

  /**
   * Merges the base string with the profile string.
   *
   * @param baseValue the base string
   * @param profileValue the profile string
   * @return the merged string
   */
  private String mergeString(String baseValue, String profileValue) {
    return StringHelper.isBlank(profileValue) ? baseValue : profileValue;
  }

  /**
   * Merges the base capabilities with the profile capabilities.
   *
   * @param base the base capabilities
   * @param profile the profile capabilities
   * @return the merged capabilities
   */
  private McpServerCapabilities mergeCapabilities(
      McpServerCapabilities base, McpServerCapabilities profile) {

    if (base == null) {
      return profile;
    }

    if (profile == null) {
      return base;
    }

    return new McpServerCapabilities(
        mergeValue(base.resource(), profile.resource()),
        mergeValue(base.prompt(), profile.prompt()),
        mergeValue(base.tool(), profile.tool()));
  }

  /**
   * Merges the base change notification with the profile change notification.
   *
   * @param base the base change notification
   * @param profile the profile change notification
   * @return the merged change notification
   */
  private McpServerChangeNotification mergeChangeNotification(
      McpServerChangeNotification base, McpServerChangeNotification profile) {

    if (base == null) {
      return profile;
    }

    if (profile == null) {
      return base;
    }

    return new McpServerChangeNotification(
        mergeValue(base.resource(), profile.resource()),
        mergeValue(base.prompt(), profile.prompt()),
        mergeValue(base.tool(), profile.tool()));
  }

  /**
   * Merges the base SSE with the profile SSE.
   *
   * @param base the base SSE
   * @param profile the profile SSE
   * @return the merged SSE
   */
  private McpServerSSE mergeSSE(McpServerSSE base, McpServerSSE profile) {

    if (base == null) {
      return profile;
    }

    if (profile == null) {
      return base;
    }

    return new McpServerSSE(
        mergeString(base.messageEndpoint(), profile.messageEndpoint()),
        mergeString(base.endpoint(), profile.endpoint()),
        mergeString(base.baseUrl(), profile.baseUrl()),
        mergeValue(base.port(), profile.port()));
  }

  /**
   * Merges the base streamable with the profile streamable.
   *
   * @param base the base streamable
   * @param profile the profile streamable
   * @return the merged streamable
   */
  private McpServerStreamable mergeStreamable(
      McpServerStreamable base, McpServerStreamable profile) {

    if (base == null) {
      return profile;
    }

    if (profile == null) {
      return base;
    }

    return new McpServerStreamable(
        mergeString(base.mcpEndpoint(), profile.mcpEndpoint()),
        mergeValue(base.disallowDelete(), profile.disallowDelete()),
        mergeValue(base.keepAliveInterval(), profile.keepAliveInterval()),
        mergeValue(base.port(), profile.port()));
  }
}
