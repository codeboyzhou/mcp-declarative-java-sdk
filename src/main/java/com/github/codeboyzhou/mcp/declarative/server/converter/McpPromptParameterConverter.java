package com.github.codeboyzhou.mcp.declarative.server.converter;

import com.github.codeboyzhou.mcp.declarative.annotation.McpPromptParam;
import com.github.codeboyzhou.mcp.declarative.util.TypeConverter;
import java.lang.reflect.Parameter;
import java.util.Map;

public class McpPromptParameterConverter extends AbstractParameterConverter<McpPromptParam> {
  @Override
  public Object convert(Parameter parameter, McpPromptParam annotation, Map<String, Object> args) {
    Object rawValue = args.get(annotation.name());
    return TypeConverter.convert(rawValue, parameter.getType());
  }

  @Override
  public Class<McpPromptParam> getAnnotationType() {
    return McpPromptParam.class;
  }
}
