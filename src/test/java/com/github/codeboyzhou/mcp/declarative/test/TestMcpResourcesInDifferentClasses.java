package com.github.codeboyzhou.mcp.declarative.test;

import com.github.codeboyzhou.mcp.declarative.annotation.McpResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestMcpResourcesInDifferentClasses {

  private static final Logger log =
      LoggerFactory.getLogger(TestMcpResourcesInDifferentClasses.class);

  @McpResource(
      uri = "test://resource2",
      name = "resource2_name",
      title = "resource2_title",
      description = "resource2_description")
  public String resource2() {
    log.debug("calling resource2");
    return "resource2_content";
  }
}
