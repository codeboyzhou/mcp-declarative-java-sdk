package com.github.codeboyzhou.mcp.declarative.server.converter;

import com.github.codeboyzhou.mcp.declarative.reflect.MethodMetadata;
import com.github.codeboyzhou.mcp.declarative.util.TypeConverter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractParameterConverter<A extends Annotation>
    implements ParameterConverter<A> {

  public List<Object> convertAllParameters(MethodMetadata metadata, Map<String, Object> args) {
    Parameter[] params = metadata.getParameters();
    List<Object> result = new ArrayList<>(params.length);

    for (Parameter param : params) {
      A annotation = param.getAnnotation(getAnnotationType());
      Object converted;
      // Fill in a default value when the parameter is not specified or unannotated
      // to ensure that the parameter type is correct when calling method.invoke()
      if (annotation == null) {
        converted = TypeConverter.convert(null, param.getType());
      } else {
        converted = convert(param, annotation, args);
      }
      result.add(converted);
    }

    return result;
  }
}
