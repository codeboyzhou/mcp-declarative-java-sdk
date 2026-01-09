package com.github.thought2code.mcp.annotated.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

/**
 * Helper class for Java and JSON schema type conversion.
 *
 * @author codeboyzhou
 */
public final class TypeConverter {

  /** Map of Java classes to their corresponding type conversion functions. */
  private static final Map<Class<?>, Function<@NotNull String, @NotNull Object>> CLASS_CONVERTERS;

  /** Map of Java classes to their default values. */
  private static final Map<Class<?>, Object> DEFAULT_VALUES;

  static {
    CLASS_CONVERTERS = new ConcurrentHashMap<>();
    DEFAULT_VALUES = new ConcurrentHashMap<>();
    initializeClassConverters();
    initializeDefaultValues();
  }

  /**
   * Private constructor to prevent instantiation of the utility class.
   *
   * @throws UnsupportedOperationException if instantiation is attempted
   */
  @VisibleForTesting
  TypeConverter() {
    throw new UnsupportedOperationException("Utility class should not be instantiated");
  }

  /** Initializes the map of Java classes to their corresponding type conversion functions. */
  private static void initializeClassConverters() {
    CLASS_CONVERTERS.put(String.class, value -> value);
    CLASS_CONVERTERS.put(int.class, Integer::parseInt);
    CLASS_CONVERTERS.put(Integer.class, Integer::parseInt);
    CLASS_CONVERTERS.put(long.class, Long::parseLong);
    CLASS_CONVERTERS.put(Long.class, Long::parseLong);
    CLASS_CONVERTERS.put(float.class, Float::parseFloat);
    CLASS_CONVERTERS.put(Float.class, Float::parseFloat);
    CLASS_CONVERTERS.put(double.class, Double::parseDouble);
    CLASS_CONVERTERS.put(Double.class, Double::parseDouble);
    CLASS_CONVERTERS.put(Number.class, TypeConverter::parseNumber);
    CLASS_CONVERTERS.put(boolean.class, Boolean::parseBoolean);
    CLASS_CONVERTERS.put(Boolean.class, Boolean::parseBoolean);
  }

  /** Initializes the map of Java classes to their default values. */
  private static void initializeDefaultValues() {
    DEFAULT_VALUES.put(String.class, StringHelper.EMPTY);
    DEFAULT_VALUES.put(int.class, 0);
    DEFAULT_VALUES.put(Integer.class, 0);
    DEFAULT_VALUES.put(long.class, 0L);
    DEFAULT_VALUES.put(Long.class, 0L);
    DEFAULT_VALUES.put(float.class, 0.0F);
    DEFAULT_VALUES.put(Float.class, 0.0F);
    DEFAULT_VALUES.put(double.class, 0.0);
    DEFAULT_VALUES.put(Double.class, 0.0);
    DEFAULT_VALUES.put(Number.class, 0.0);
    DEFAULT_VALUES.put(boolean.class, false);
    DEFAULT_VALUES.put(Boolean.class, false);
  }

  /**
   * Parses the given string as a number, preferring double precision if the string contains a dot,
   * and falling back to integer or long if not.
   *
   * @param number the string representation of the number
   * @return the parsed number
   */
  private static Number parseNumber(@NotNull String number) {
    // Parse as a double if it contains a dot
    if (number.contains(StringHelper.DOT)) {
      return Double.parseDouble(number);
    }

    // If it doesn't contain a dot, try to parse as an integer first
    try {
      return Integer.parseInt(number);
    } catch (NumberFormatException e) {
      // If it's not an integer, try to parse as a long
      return Long.parseLong(number);
    }
  }

  /**
   * Converts the given value to the specified target type. If the value is null, returns the
   * default value for the target type.
   *
   * @param value the value to convert
   * @param targetType the target type to convert to
   * @return the converted value
   */
  public static Object convert(@Nullable Object value, Class<?> targetType) {
    if (value == null) {
      return DEFAULT_VALUES.get(targetType);
    }

    if (CLASS_CONVERTERS.containsKey(targetType)) {
      Function<String, Object> converter = CLASS_CONVERTERS.get(targetType);
      return converter.apply(value.toString());
    }

    return value;
  }
}
