package com.github.codeboyzhou.mcp.declarative;

import com.github.codeboyzhou.mcp.declarative.annotation.McpTools;
import com.github.codeboyzhou.mcp.declarative.server.McpServerInfo;
import com.github.codeboyzhou.mcp.declarative.server.McpSseServerInfo;
import com.github.codeboyzhou.mcp.declarative.server.TestMcpComponentScanBasePackageClass;
import com.github.codeboyzhou.mcp.declarative.server.TestMcpComponentScanBasePackageString;
import com.github.codeboyzhou.mcp.declarative.server.TestMcpComponentScanDefault;
import com.github.codeboyzhou.mcp.declarative.server.TestMcpComponentScanIsNull;
import com.github.codeboyzhou.mcp.declarative.server.TestMcpTools;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class McpServersTest {

    static final String[] EMPTY_ARGS = new String[]{};

    Reflections reflections;

    @AfterEach
    void tearDown() throws NoSuchFieldException, IllegalAccessException {
        reflections = getReflectionsField();
        assertNotNull(reflections);
        Map<String, Set<String>> scannedClasses = reflections.getStore().get(Scanners.TypesAnnotated.name());
        Set<String> scannedToolClass = scannedClasses.get(McpTools.class.getName());
        assertEquals(1, scannedToolClass.size());
        assertEquals(scannedToolClass.iterator().next(), TestMcpTools.class.getName());
        reflections = null;
    }

    @ParameterizedTest
    @ValueSource(classes = {
        TestMcpComponentScanIsNull.class,
        TestMcpComponentScanBasePackageString.class,
        TestMcpComponentScanBasePackageClass.class,
        TestMcpComponentScanDefault.class
    })
    void testRun(Class<?> applicationMainClass) {
        McpServers.run(applicationMainClass, EMPTY_ARGS);
    }

    @Test
    void testStartSyncStdioServer() {
        assertDoesNotThrow(() -> {
            McpServers servers = McpServers.run(TestMcpComponentScanIsNull.class, EMPTY_ARGS);
            McpServerInfo serverInfo = McpServerInfo.builder()
                .instructions("test-mcp-sync-stdio-server-instructions")
                .name("test-mcp-sync-stdio-server")
                .version("1.0.0")
                .build();
            servers.startSyncStdioServer(serverInfo);
        });
    }

    @Test
    void testStartSyncSseServer() {
        System.setProperty("mcp.declarative.java.sdk.testing", "true");
        McpServers servers = McpServers.run(TestMcpComponentScanIsNull.class, EMPTY_ARGS);
        assertDoesNotThrow(() -> {
            McpSseServerInfo serverInfo = McpSseServerInfo.builder()
                .instructions("test-mcp-sync-sse-server-instructions")
                .baseUrl("http://127.0.0.1:8080")
                .messageEndpoint("/message")
                .sseEndpoint("/sse")
                .port(8080)
                .name("test-mcp-sync-sse-server")
                .version("1.0.0")
                .build();
            servers.startSyncSseServer(serverInfo);
        });
    }

    private Reflections getReflectionsField() throws NoSuchFieldException, IllegalAccessException {
        Field reflectionsField = McpServers.class.getDeclaredField("reflections");
        reflectionsField.setAccessible(true);
        Reflections reflections = (Reflections) reflectionsField.get(null);
        reflectionsField.setAccessible(false);
        return reflections;
    }

}
