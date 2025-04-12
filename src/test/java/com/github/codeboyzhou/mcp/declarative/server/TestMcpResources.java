package com.github.codeboyzhou.mcp.declarative.server;

import com.github.codeboyzhou.mcp.declarative.annotation.McpResource;
import com.github.codeboyzhou.mcp.declarative.annotation.McpResources;

@McpResources
public class TestMcpResources {

    @McpResource(uri = "test://resource1", name = "resource1", description = "resource1")
    public String resource1() {
        return System.lineSeparator();
    }

    @McpResource(uri = "test://resource2", description = "resource2")
    public String resource2() {
        return System.lineSeparator();
    }

}
