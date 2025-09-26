package com.github.codeboyzhou.mcp.declarative.server.converter;

import com.github.codeboyzhou.mcp.declarative.util.TypeConverter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This abstract class is used to convert the value of a parameter annotated with {@link A} to the
 * required type, and provides a default implementation for the {@link #convertAll(Parameter[],
 * Map)} method.
 *
 * @param <A> the type of the annotation
 * @author codeboyzhou
 */
public abstract class AbstractParameterConverter<A extends Annotation>
    implements ParameterConverter<A> {

  /**
   * Converts the values of all parameters annotated with the specified annotation to the required
   * types.
   *
   * @param methodParameters the parameters of the method
   * @param args the arguments passed to the method
   * @return the converted values of all parameters
   */
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
