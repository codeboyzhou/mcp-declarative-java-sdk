package com.github.codeboyzhou.mcp.declarative.test;

import com.github.codeboyzhou.mcp.declarative.annotation.McpTool;
import com.github.codeboyzhou.mcp.declarative.annotation.McpToolParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestMcpToolsInDifferentClasses {

  private static final Logger log = LoggerFactory.getLogger(TestMcpToolsInDifferentClasses.class);

  @McpTool
  public void toolWithVoidReturn() {
    log.debug("calling toolWithVoidReturn");
  }

  @McpTool
  public String toolWithReturnNull() {
    log.debug("calling toolWithReturnNull");
    return null;
  }

  @McpTool
  public String toolWithIntegerParam(@McpToolParam(name = "param", required = true) Integer param) {
    log.debug("calling toolWithIntegerParam with param: {}", param);
    return "toolWithIntegerParam is called with param: " + param;
  }

  @McpTool
  public String toolWithLongParam(@McpToolParam(name = "param", required = true) Long param) {
    log.debug("calling toolWithLongParam with param: {}", param);
    return "toolWithLongParam is called with param: " + param;
  }

  @McpTool
  public String toolWithFloatParam(@McpToolParam(name = "param", required = true) Float param) {
    log.debug("calling toolWithFloatParam with param: {}", param);
    return "toolWithFloatParam is called with param: " + param;
  }

  @McpTool
  public String toolWithDoubleParam(@McpToolParam(name = "param", required = true) Double param) {
    log.debug("calling toolWithDoubleParam with param: {}", param);
    return "toolWithDoubleParam is called with param: " + param;
  }

  @McpTool
  public String toolWithNumberParam(@McpToolParam(name = "param", required = true) Number param) {
    log.debug("calling toolWithNumberParam with param: {}", param);
    return "toolWithNumberParam is called with param: " + param;
  }

  @McpTool
  public String toolWithBooleanParam(@McpToolParam(name = "param", required = true) Boolean param) {
    log.debug("calling toolWithBooleanParam with param: {}", param);
    return "toolWithBooleanParam is called with param: " + param;
  }
}
