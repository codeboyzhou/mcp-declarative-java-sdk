package com.github.codeboyzhou.mcp.declarative.annotation;

import com.github.codeboyzhou.mcp.declarative.util.StringHelper;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface McpServerApplication {
  String basePackage() default StringHelper.EMPTY;

  Class<?> basePackageClass() default Object.class;
}
