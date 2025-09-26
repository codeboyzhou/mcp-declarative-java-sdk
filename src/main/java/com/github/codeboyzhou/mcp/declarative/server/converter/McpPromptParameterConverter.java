package com.github.codeboyzhou.mcp.declarative.server.converter;

import com.github.codeboyzhou.mcp.declarative.annotation.McpPromptParam;
import com.github.codeboyzhou.mcp.declarative.util.TypeConverter;
import java.lang.reflect.Parameter;
import java.util.Map;

/**
 * This class is used to convert the value of a parameter annotated with {@link McpPromptParam} to
 * the required type.
 *
 * @author codeboyzhou
 */
public class McpPromptParameterConverter extends AbstractParameterConverter<McpPromptParam> {
  /**
   * Converts the value of the parameter annotated with {@link McpPromptParam} to the required type.
   *
   * @param parameter the parameter annotated with {@link McpPromptParam}
   * @param annotation the annotation instance
   * @param args the arguments passed to the method
   * @return the converted value of the parameter
   */
  @Override
  public Object convert(Parameter parameter, McpPromptParam annotation, Map<String, Object> args) {
    Object rawValue = args.get(annotation.name());
    return TypeConverter.convert(rawValue, parameter.getType());
  }

  /**
   * Returns the type of the annotation this converter handles.
   *
   * @return the type of the annotation this converter handles
   */
  @Override
  public Class<McpPromptParam> getAnnotationType() {
    return McpPromptParam.class;
  }
}
