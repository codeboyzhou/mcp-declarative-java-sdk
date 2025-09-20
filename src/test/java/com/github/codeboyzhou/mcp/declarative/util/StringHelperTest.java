package com.github.codeboyzhou.mcp.declarative.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class StringHelperTest {

  @Test
  void testConstructor_shouldThrowException() {
    assertThrows(UnsupportedOperationException.class, StringHelper::new);
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
