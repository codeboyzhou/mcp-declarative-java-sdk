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

/**
 * Helper class for Jackson JSON and YAML serialization and deserialization.
 *
 * <p>This class provides static methods for serializing and deserializing objects to and from JSON
 * and YAML formats using Jackson.
 *
 * @author codeboyzhou
 */
public final class JacksonHelper {

  /** JSON ObjectMapper instance. */
  private static final ObjectMapper JSON = new ObjectMapper(new JsonFactory());

  /** YAML ObjectMapper instance. */
  private static final ObjectMapper YAML = new ObjectMapper(new YAMLFactory());

  /**
   * Private constructor to prevent instantiation of the utility class.
   *
   * @throws UnsupportedOperationException if instantiation is attempted
   */
  @VisibleForTesting
  JacksonHelper() {
    throw new UnsupportedOperationException("Utility class should not be instantiated");
  }

  /**
   * Serialize an object to a JSON string.
   *
   * @param object the object to serialize
   * @return the JSON string representation of the object
   */
  public static String toJsonString(Object object) {
    try {
      return JSON.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new McpServerJsonProcessingException("Error converting object to JSON", e);
    }
  }

  /**
   * Deserialize a JSON string to an object of the specified type.
   *
   * @param json the JSON string to deserialize
   * @param valueType the class of the object to deserialize to
   * @param <T> the type of the object to deserialize to
   * @return the deserialized object
   */
  public static <T> T fromJson(String json, Class<T> valueType) {
    try {
      return JSON.readValue(json, valueType);
    } catch (JsonProcessingException e) {
      throw new McpServerJsonProcessingException("Error converting JSON to object", e);
    }
  }

  /**
   * Deserialize a YAML file to an object of the specified type.
   *
   * @param yamlFile the YAML file to deserialize
   * @param valueType the class of the object to deserialize to
   * @param <T> the type of the object to deserialize to
   * @return the deserialized object
   */
  public static <T> T fromYaml(File yamlFile, Class<T> valueType) {
    try {
      return YAML.readValue(yamlFile, valueType);
    } catch (IOException e) {
      final String path = yamlFile.getAbsolutePath();
      throw new McpServerConfigurationException("Error reading YAML file: " + path, e);
    }
  }
}
