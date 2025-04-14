package com.github.codeboyzhou.mcp.declarative.util;

import io.modelcontextprotocol.spec.McpSchema;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

public final class ReflectionHelper {

    public static Set<Method> getMethodsAnnotatedWith(Class<?> clazz, Class<? extends Annotation> annotation) {
        Method[] methods = clazz.getMethods();
        return Set.of(methods).stream().filter(m -> m.isAnnotationPresent(annotation)).collect(toSet());
    }

    public static Set<Parameter> getParametersAnnotatedWith(Method method, Class<? extends Annotation> annotation) {
        Parameter[] parameters = method.getParameters();
        return Set.of(parameters).stream().filter(p -> p.isAnnotationPresent(annotation)).collect(toSet());
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
