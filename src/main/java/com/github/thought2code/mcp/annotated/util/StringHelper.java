package com.github.thought2code.mcp.annotated.util;

import org.jetbrains.annotations.VisibleForTesting;

/**
 * Helper class for string operations.
 *
 * @author codeboyzhou
 */
public final class StringHelper {

  /** The empty string constant. */
  public static final String EMPTY = "";

  /** The dot character constant. */
  public static final String DOT = ".";

  /** The space character constant. */
  public static final String SPACE = " ";

  /** The new line character constant. */
  public static final String NewLine = "\n";

  /**
   * Private constructor to prevent instantiation of the utility class.
   *
   * @throws UnsupportedOperationException if instantiation is attempted
   */
  @VisibleForTesting
  StringHelper() {
    throw new UnsupportedOperationException("Utility class should not be instantiated");
  }

  /**
   * Checks if the given string is blank.
   *
   * @param str the string to check
   * @return {@code true} if the string is blank, {@code false} otherwise
   */
  public static boolean isBlank(String str) {
    return str == null || str.isBlank();
  }

  /**
   * Returns the default value if the given string is blank, otherwise returns the original string.
   *
   * @param str the string to check
   * @param defaultValue the default value to return if the string is blank
   * @return the original string if it is not blank, otherwise the default value
   */
  public static String defaultIfBlank(String str, String defaultValue) {
    return isBlank(str) ? defaultValue : str;
  }
}
