package com.github.codeboyzhou.mcp.declarative.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class StringHelperTest {

  @Test
  void testConstructor_shouldThrowException() {
    assertThrows(UnsupportedOperationException.class, StringHelper::new);
  }

  @Test
  void testIsBlank_shouldReturnTrueWhenStrIsBlank() {
    assertTrue(StringHelper.isBlank(StringHelper.EMPTY));
    assertTrue(StringHelper.isBlank(StringHelper.SPACE));
  }

  @Test
  void testIsBlank_shouldReturnFalseWhenStrIsNotBlank() {
    assertFalse(StringHelper.isBlank("test"));
  }

  @Test
  void testDefaultIfBlank_shouldReturnDefaultValueWhenStrIsNull() {
    assertEquals("default", StringHelper.defaultIfBlank(null, "default"));
  }

  @Test
  void testDefaultIfBlank_shouldReturnDefaultValueWhenStrIsBlank() {
    assertEquals("default", StringHelper.defaultIfBlank(StringHelper.SPACE, "default"));
  }

  @Test
  void testDefaultIfBlank_shouldReturnDefaultValueWhenStrIsEmpty() {
    assertEquals("default", StringHelper.defaultIfBlank(StringHelper.EMPTY, "default"));
  }

  @Test
  void testDefaultIfBlank_shouldReturnStrWhenStrIsNotBlank() {
    assertEquals("test", StringHelper.defaultIfBlank("test", "default"));
  }
}
