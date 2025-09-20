package com.github.codeboyzhou.mcp.declarative.configuration;

import com.github.codeboyzhou.mcp.declarative.exception.McpServerConfigurationException;
import com.github.codeboyzhou.mcp.declarative.util.JacksonHelper;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record YAMLConfigurationLoader(String configFileName) {

  private static final Logger log = LoggerFactory.getLogger(YAMLConfigurationLoader.class);

  private static final String DEFAULT_CONFIG_FILE_NAME = "mcp-server.yml";

  public YAMLConfigurationLoader() {
    this(DEFAULT_CONFIG_FILE_NAME);
  }

  public McpServerConfiguration loadConfig() {
    Path configFilePath = getConfigFilePath(configFileName);
    File file = configFilePath.toFile();
    McpServerConfiguration config = JacksonHelper.fromYaml(file, McpServerConfiguration.class);
    log.info("Configuration loaded successfully from file: {}", configFileName);
    return config;
  }

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
}
