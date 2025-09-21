package com.github.codeboyzhou.mcp.declarative.test;

import com.github.codeboyzhou.mcp.declarative.annotation.McpResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestMcpResources {

  private static final Logger log = LoggerFactory.getLogger(TestMcpResources.class);

  @McpResource(
      uri = "test://resource1",
      name = "resource1_name",
      title = "resource1_title",
      description = "resource1_description")
  public String resource1() {
    log.debug("calling resource1");
    return "resource1_content";
  }
}
