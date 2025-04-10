package com.github.codeboyzhou.mcp.declarative;

import com.github.codeboyzhou.mcp.declarative.annotation.McpTools;
import com.github.codeboyzhou.mcp.declarative.server.TestMcpTools;
import com.github.codeboyzhou.mcp.declarative.server.TestMcpComponentScanBasePackageClass;
import com.github.codeboyzhou.mcp.declarative.server.TestMcpComponentScanBasePackageString;
import com.github.codeboyzhou.mcp.declarative.server.TestMcpComponentScanDefault;
import com.github.codeboyzhou.mcp.declarative.server.TestMcpComponentScanIsNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class McpServersTest {

    static final String[] EMPTY_ARGS = new String[]{};

    Reflections reflections;

    @BeforeEach
    void setUp() {
        reflections = null;
    }

    @AfterEach
    void tearDown() throws NoSuchFieldException, IllegalAccessException {
        reflections = getReflectionsField();
        assertNotNull(reflections);
        Map<String, Set<String>> scannedClasses = reflections.getStore().get(Scanners.TypesAnnotated.name());
        Set<String> scannedToolClass = scannedClasses.get(McpTools.class.getName());
        assertEquals(1, scannedToolClass.size());
        assertEquals(scannedToolClass.iterator().next(), TestMcpTools.class.getName());
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

    private Reflections getReflectionsField() throws NoSuchFieldException, IllegalAccessException {
        Field reflectionsField = McpServers.class.getDeclaredField("reflections");
        reflectionsField.setAccessible(true);
        Reflections reflections = (Reflections) reflectionsField.get(null);
        reflectionsField.setAccessible(false);
        return reflections;
    }

}
