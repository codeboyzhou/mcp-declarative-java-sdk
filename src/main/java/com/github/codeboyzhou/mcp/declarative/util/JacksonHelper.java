package com.github.codeboyzhou.mcp.declarative.util;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.codeboyzhou.mcp.declarative.exception.McpServerConfigurationException;
import com.github.codeboyzhou.mcp.declarative.exception.McpServerJsonProcessingException;
import java.io.File;
import java.io.IOException;
import org.jetbrains.annotations.VisibleForTesting;

public final class JacksonHelper {

  private static final ObjectMapper JSON = new ObjectMapper(new JsonFactory());

  private static final ObjectMapper YAML = new ObjectMapper(new YAMLFactory());

  @VisibleForTesting
  JacksonHelper() {
    throw new UnsupportedOperationException("Utility class should not be instantiated");
  }

  public static String toJsonString(Object object) {
    try {
      return JSON.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new McpServerJsonProcessingException("Error converting object to JSON", e);
    }
  }

  public static <T> T fromYaml(File yamlFile, Class<T> valueType) {
    try {
      return YAML.readValue(yamlFile, valueType);
    } catch (IOException e) {
      final String path = yamlFile.getAbsolutePath();
      throw new McpServerConfigurationException("Error reading YAML file: " + path, e);
    }
  }
}
