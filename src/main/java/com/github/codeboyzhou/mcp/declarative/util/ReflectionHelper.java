package com.github.codeboyzhou.mcp.declarative.util;

import io.modelcontextprotocol.spec.McpSchema;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public final class ReflectionHelper {

    /**
     * has to use list as result to make order correct
     * @param clazz
     * @param annotation
     * @return
     */
    public static List<Method> getMethodsAnnotatedWith(Class<?> clazz, Class<? extends Annotation> annotation) {
        Method[] methods = clazz.getMethods();
        return Stream.of(methods).filter(m -> m.isAnnotationPresent(annotation)).toList();
    }

    /**
     * has to use list as result to make order correct
     * @param method
     * @param annotation
     * @return
     */
    public static List<Parameter> getParametersAnnotatedWith(Method method, Class<? extends Annotation> annotation) {
        Parameter[] parameters = method.getParameters();
        return Stream.of(parameters).filter(p -> p.isAnnotationPresent(annotation)).toList();
    }

    public static Object invokeMethod(Class<?> clazz, Method method) throws Exception {
        Object object = clazz.getDeclaredConstructor().newInstance();
        return method.invoke(object);
    }

    public static Object invokeMethod(Class<?> clazz, Method method, Map<String, Object> parameters) throws Exception {
        Object object = clazz.getDeclaredConstructor().newInstance();
        return method.invoke(object, parameters.values().toArray());
    }

    public static Object invokeMethod(Class<?> clazz, Method method, McpSchema.JsonSchema schema, Map<String, Object> parameters) throws Exception {
        Object object = clazz.getDeclaredConstructor().newInstance();
        Map<String, Object> typedParameters = asTypedParameters(schema, parameters);
        return method.invoke(object, typedParameters.values().toArray());
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> asTypedParameters(McpSchema.JsonSchema schema, Map<String, Object> parameters) {
        Map<String, Object> properties = schema.properties();
        Map<String, Object> typedParameters = new LinkedHashMap<>(properties.size());

        properties.forEach((parameterName, parameterProperties) -> {
            Object parameterValue = parameters.get(parameterName);
            if (parameterValue == null) {
                Map<String, Object> map = (Map<String, Object>) parameterProperties;
                final String parameterType = map.get("type").toString();
                if (isTypeOf(String.class, parameterType)) {
                    typedParameters.put(parameterName, "");
                } else if (isTypeOf(Integer.class, parameterType)) {
                    typedParameters.put(parameterName, 0);
                } else if (isTypeOf(Number.class, parameterType)) {
                    typedParameters.put(parameterName, 0.0);
                } else if (isTypeOf(Boolean.class, parameterType)) {
                    typedParameters.put(parameterName, false);
                }
            } else {
                typedParameters.put(parameterName, parameterValue);
            }
        });

        return typedParameters;
    }

    private static boolean isTypeOf(Class<?> clazz, String jsonSchemaType) {
        return clazz.getName().equalsIgnoreCase(jsonSchemaType) || clazz.getSimpleName().equalsIgnoreCase(jsonSchemaType);
    }

}
