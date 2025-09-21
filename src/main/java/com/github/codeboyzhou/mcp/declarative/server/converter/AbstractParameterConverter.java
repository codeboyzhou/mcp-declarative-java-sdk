package com.github.codeboyzhou.mcp.declarative.server.converter;

import com.github.codeboyzhou.mcp.declarative.util.TypeConverter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractParameterConverter<A extends Annotation>
    implements ParameterConverter<A> {

  public List<Object> convertAll(Parameter[] methodParameters, Map<String, Object> args) {
    List<Object> convertedParameters = new ArrayList<>(methodParameters.length);

    for (Parameter param : methodParameters) {
      A annotation = param.getAnnotation(getAnnotationType());
      Object converted;
      // Fill in a default value when the parameter is not specified or unannotated
      // to ensure that the parameter type is correct when calling method.invoke()
      if (annotation == null) {
        converted = TypeConverter.convert(null, param.getType());
      } else {
        converted = convert(param, annotation, args);
      }
      convertedParameters.add(converted);
    }

    return convertedParameters;
  }
}
