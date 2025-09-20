package com.github.codeboyzhou.mcp.declarative.server.converter;

import com.github.codeboyzhou.mcp.declarative.annotation.McpToolParam;
import com.github.codeboyzhou.mcp.declarative.util.TypeConverter;
import java.lang.reflect.Parameter;
import java.util.Map;

public class McpToolParameterConverter extends AbstractParameterConverter<McpToolParam> {
  @Override
  public Object convert(Parameter parameter, McpToolParam annotation, Map<String, Object> args) {
    Object rawValue = args.get(annotation.name());
    return TypeConverter.convert(rawValue, parameter.getType());
  }

  @Override
  public Class<McpToolParam> getAnnotationType() {
    return McpToolParam.class;
  }
}
