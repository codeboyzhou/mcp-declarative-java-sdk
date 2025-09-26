package com.github.codeboyzhou.mcp.declarative.util;

import com.github.codeboyzhou.mcp.declarative.enums.JsonSchemaDataType;
import org.jetbrains.annotations.VisibleForTesting;

/**
 * Helper class for Java and JSON schema type conversion.
 *
 * @author codeboyzhou
 */
public final class TypeConverter {

  /**
   * Private constructor to prevent instantiation of the utility class.
   *
   * @throws UnsupportedOperationException if instantiation is attempted
   */
  @VisibleForTesting
  TypeConverter() {
    throw new UnsupportedOperationException("Utility class should not be instantiated");
  }

  /**
   * Converts the given value to the specified target type. If the value is null, returns the
   * default value for the target type.
   *
   * @param value the value to convert
   * @param targetType the target type to convert to
   * @return the converted value
   */
  public static Object convert(Object value, Class<?> targetType) {
    if (value == null) {
      return getDefaultValue(targetType);
    }

    final String valueAsString = value.toString();

    if (targetType == String.class) {
      return valueAsString;
    }
    if (targetType == int.class || targetType == Integer.class) {
      return Integer.parseInt(valueAsString);
    }
    if (targetType == long.class || targetType == Long.class) {
      return Long.parseLong(valueAsString);
    }
    if (targetType == float.class || targetType == Float.class) {
      return Float.parseFloat(valueAsString);
    }
    if (targetType == double.class || targetType == Double.class) {
      return Double.parseDouble(valueAsString);
    }
    if (targetType == boolean.class || targetType == Boolean.class) {
      return Boolean.parseBoolean(valueAsString);
    }

    return valueAsString;
  }

  /**
   * Converts the given value to the specified target type based on the JSON schema type. If the
   * value is null, returns the default value for the JSON schema type.
   *
   * @param value the value to convert
   * @param jsonSchemaType the JSON schema type to convert to
   * @return the converted value
   */
  public static Object convert(Object value, String jsonSchemaType) {
    if (value == null) {
      return getDefaultValue(jsonSchemaType);
    }

    final String valueAsString = value.toString();

    if (JsonSchemaDataType.STRING.getType().equals(jsonSchemaType)) {
      return valueAsString;
    }
    if (JsonSchemaDataType.INTEGER.getType().equals(jsonSchemaType)) {
      return Integer.parseInt(valueAsString);
    }
    if (JsonSchemaDataType.NUMBER.getType().equals(jsonSchemaType)) {
      return Double.parseDouble(valueAsString);
    }
    if (JsonSchemaDataType.BOOLEAN.getType().equals(jsonSchemaType)) {
      return Boolean.parseBoolean(valueAsString);
    }

    return valueAsString;
  }

  /**
   * Returns the default value for the specified type.
   *
   * @param type the type to get the default value for
   * @return the default value for the specified type
   */
  private static Object getDefaultValue(Class<?> type) {
    if (type == String.class) {
      return StringHelper.EMPTY;
    }
    if (type == int.class || type == Integer.class) {
      return 0;
    }
    if (type == long.class || type == Long.class) {
      return 0L;
    }
    if (type == float.class || type == Float.class) {
      return 0.0f;
    }
    if (type == double.class || type == Double.class) {
      return 0.0;
    }
    if (type == boolean.class || type == Boolean.class) {
      return false;
    }
    return null;
  }

  /**
   * Returns the default value for the specified JSON schema type.
   *
   * @param jsonSchemaType the JSON schema type to get the default value for
   * @return the default value for the specified JSON schema type
   */
  private static Object getDefaultValue(String jsonSchemaType) {
    if (JsonSchemaDataType.STRING.getType().equalsIgnoreCase(jsonSchemaType)) {
      return StringHelper.EMPTY;
    }
    if (JsonSchemaDataType.INTEGER.getType().equalsIgnoreCase(jsonSchemaType)) {
      return 0;
    }
    if (JsonSchemaDataType.NUMBER.getType().equalsIgnoreCase(jsonSchemaType)) {
      return 0.0;
    }
    if (JsonSchemaDataType.BOOLEAN.getType().equalsIgnoreCase(jsonSchemaType)) {
      return false;
    }
    return null;
  }
}
