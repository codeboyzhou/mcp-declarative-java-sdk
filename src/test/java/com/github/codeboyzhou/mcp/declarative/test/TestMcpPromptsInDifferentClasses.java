package com.github.codeboyzhou.mcp.declarative.test;

import com.github.codeboyzhou.mcp.declarative.annotation.McpPrompt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestMcpPromptsInDifferentClasses {

  private static final Logger log = LoggerFactory.getLogger(TestMcpPromptsInDifferentClasses.class);

  @McpPrompt
  public void promptWithVoidReturn() {
    log.debug("calling promptWithVoidReturn");
  }

  @McpPrompt
  public String promptWithReturnNull() {
    log.debug("calling promptWithReturnNull");
    return null;
  }
}
