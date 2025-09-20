package com.github.codeboyzhou.mcp.declarative.util;

import org.jetbrains.annotations.VisibleForTesting;

public final class StringHelper {

  public static final String EMPTY = "";

  public static final String SPACE = " ";

  @VisibleForTesting
  StringHelper() {
    throw new UnsupportedOperationException("Utility class should not be instantiated");
  }

  public static String defaultIfBlank(String str, String defaultValue) {
    return str == null || str.isBlank() ? defaultValue : str;
  }
}
