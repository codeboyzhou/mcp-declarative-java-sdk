package com.github.codeboyzhou.mcp.declarative.test;

import com.github.codeboyzhou.mcp.declarative.annotation.McpTool;
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
}
