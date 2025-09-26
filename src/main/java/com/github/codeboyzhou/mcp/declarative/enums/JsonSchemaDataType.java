package com.github.codeboyzhou.mcp.declarative.enums;

/**
 * This enum represents the data type of JSON schema.
 *
 * @author codeboyzhou
 */
public enum JsonSchemaDataType {

  /** The JSON schema data type is {@code string}. */
  STRING("string"),

  /** The JSON schema data type is {@code number}. */
  NUMBER("number"),

  /** The JSON schema data type is {@code float}. */
  FLOAT("number"),

  /** The JSON schema data type is {@code double}. */
  DOUBLE("number"),

  /** The JSON schema data type is {@code integer}. */
  INTEGER("integer"),

  /** The JSON schema data type is {@code boolean}. */
  BOOLEAN("boolean"),

  /** The JSON schema data type is {@code object}. */
  OBJECT("object"),
  ;

  /** The JSON schema data type. */
  private final String type;

  /**
   * Creates a new instance of {@code JsonSchemaDataType} with the specified JSON schema data type.
   *
   * @param type the JSON schema data type
   */
  JsonSchemaDataType(String type) {
    this.type = type;
  }

  /**
   * Returns the JSON schema data type.
   *
   * @return the JSON schema data type
   */
  public String getType() {
    return type;
  }

  /**
   * Returns the JSON schema data type for the specified Java class. If the Java class is not
   * supported or mapped to a JSON schema data type, {@link #STRING} will be returned.
   *
   * @param javaType the Java class
   * @return the JSON schema data type
   */
  public static JsonSchemaDataType fromJavaType(Class<?> javaType) {
    JsonSchemaDataType[] values = values();
    for (JsonSchemaDataType dataType : values) {
      if (dataType.name().equalsIgnoreCase(javaType.getSimpleName())) {
        return dataType;
      }
    }
    return STRING;
  }
}
