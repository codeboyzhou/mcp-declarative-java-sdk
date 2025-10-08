package com.github.codeboyzhou.mcp.declarative.test;

import com.github.codeboyzhou.mcp.declarative.annotation.McpJsonSchemaProperty;
import com.github.codeboyzhou.mcp.declarative.annotation.McpTool;
import com.github.codeboyzhou.mcp.declarative.server.McpStructuredContent;

public class TestMcpToolsStructuredContent {

  public record TestStructuredContent(
      @McpJsonSchemaProperty(description = "test int", required = true) int testInt,
      @McpJsonSchemaProperty(description = "test integer") Integer testInteger,
      @McpJsonSchemaProperty(description = "test long", required = true) long testLong,
      @McpJsonSchemaProperty(description = "test long class") Long testLongClass,
      @McpJsonSchemaProperty(description = "test float", required = true) float testFloat,
      @McpJsonSchemaProperty(description = "test float class") Float testFloatClass,
      @McpJsonSchemaProperty(description = "test double") double testDouble,
      @McpJsonSchemaProperty(description = "test double class") Double testDoubleClass)
      implements McpStructuredContent {

    @Override
    public String asTextContent() {
      return String.format(
          "testInt: %d, testInteger: %d, testLong: %d, testLongClass: %d, testFloat: %f, testFloatClass: %f, testDouble: %f, testDoubleClass: %f",
          testInt,
          testInteger,
          testLong,
          testLongClass,
          testFloat,
          testFloatClass,
          testDouble,
          testDoubleClass);
    }
  }

  @McpTool
  public TestStructuredContent toolWithReturnStructuredContent() {
    return new TestStructuredContent(1, 2, 3L, 4L, 5.0F, 6.0F, 7.0, 8.0);
  }
}
