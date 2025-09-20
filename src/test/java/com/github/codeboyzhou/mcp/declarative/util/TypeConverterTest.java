package com.github.codeboyzhou.mcp.declarative.util;

import static org.junit.jupiter.api.Assertions.*;

import com.github.codeboyzhou.mcp.declarative.enums.JsonSchemaDataType;
import org.junit.jupiter.api.Test;

class TypeConverterTest {

  @Test
  void testConstructor_shouldThrowException() {
    assertThrows(UnsupportedOperationException.class, TypeConverter::new);
  }

  @Test
  void testConvertTargetType_shouldReturnDefaultValueWhenValueIsNull() {
    assertEquals(StringHelper.EMPTY, TypeConverter.convert(null, String.class));
    assertEquals(0, TypeConverter.convert(null, int.class));
    assertEquals(0, TypeConverter.convert(null, Integer.class));
    assertEquals(0L, TypeConverter.convert(null, long.class));
    assertEquals(0L, TypeConverter.convert(null, Long.class));
    assertEquals(0.0f, TypeConverter.convert(null, float.class));
    assertEquals(0.0f, TypeConverter.convert(null, Float.class));
    assertEquals(0.0, TypeConverter.convert(null, double.class));
    assertEquals(0.0, TypeConverter.convert(null, Double.class));
    assertEquals(false, TypeConverter.convert(null, boolean.class));
    assertEquals(false, TypeConverter.convert(null, Boolean.class));
    assertNull(TypeConverter.convert(null, Object.class));
  }

  @Test
  void testConvertTargetType_shouldReturnStrWhenTargetTypeIsStr() {
    assertEquals("test", TypeConverter.convert("test", String.class));
  }

  @Test
  void testConvertTargetType_shouldReturnIntWhenTargetTypeIsInt() {
    assertEquals(1, TypeConverter.convert("1", int.class));
    assertEquals(1, TypeConverter.convert("1", Integer.class));
  }

  @Test
  void testConvertTargetType_shouldReturnLongWhenTargetTypeIsLong() {
    assertEquals(1L, TypeConverter.convert("1", long.class));
    assertEquals(1L, TypeConverter.convert("1", Long.class));
  }

  @Test
  void testConvertTargetType_shouldReturnFloatWhenTargetTypeIsFloat() {
    assertEquals(1.0f, TypeConverter.convert("1", float.class));
    assertEquals(1.0f, TypeConverter.convert("1", Float.class));
  }

  @Test
  void testConvertTargetType_shouldReturnDoubleWhenTargetTypeIsDouble() {
    assertEquals(1.0, TypeConverter.convert("1", double.class));
    assertEquals(1.0, TypeConverter.convert("1", Double.class));
  }

  @Test
  void testConvertTargetType_shouldReturnBooleanWhenTargetTypeIsBoolean() {
    assertEquals(true, TypeConverter.convert("true", boolean.class));
    assertEquals(true, TypeConverter.convert("true", Boolean.class));
  }

  @Test
  void testConvertTargetType_shouldReturnValueAsStringWhenTargetTypeIsNotSupported() {
    assertEquals("test", TypeConverter.convert("test", Object.class));
  }

  @Test
  void testConvertJsonSchemaType_shouldReturnDefaultValueWhenValueIsNull() {
    assertEquals(
        StringHelper.EMPTY, TypeConverter.convert(null, JsonSchemaDataType.STRING.getType()));
    assertEquals(0, TypeConverter.convert(null, JsonSchemaDataType.INTEGER.getType()));
    assertEquals(0.0, TypeConverter.convert(null, JsonSchemaDataType.NUMBER.getType()));
    assertEquals(false, TypeConverter.convert(null, JsonSchemaDataType.BOOLEAN.getType()));
    assertNull(TypeConverter.convert(null, JsonSchemaDataType.OBJECT.getType()));
  }

  @Test
  void testConvertJsonSchemaType_shouldReturnDefaultValueWhenJsonSchemaTypeIsString() {
    assertEquals("test", TypeConverter.convert("test", JsonSchemaDataType.STRING.getType()));
  }

  @Test
  void testConvertJsonSchemaType_shouldReturnDefaultValueWhenJsonSchemaTypeIsInteger() {
    assertEquals(1, TypeConverter.convert("1", JsonSchemaDataType.INTEGER.getType()));
  }

  @Test
  void testConvertJsonSchemaType_shouldReturnDefaultValueWhenJsonSchemaTypeIsNumber() {
    assertEquals(1.0, TypeConverter.convert("1", JsonSchemaDataType.NUMBER.getType()));
  }

  @Test
  void testConvertJsonSchemaType_shouldReturnDefaultValueWhenJsonSchemaTypeIsBoolean() {
    assertEquals(true, TypeConverter.convert("true", JsonSchemaDataType.BOOLEAN.getType()));
  }

  @Test
  void testConvertJsonSchemaType_shouldReturnValueAsStringWhenJsonSchemaTypeIsNotSupported() {
    assertEquals("test", TypeConverter.convert("test", JsonSchemaDataType.OBJECT.getType()));
  }
}
