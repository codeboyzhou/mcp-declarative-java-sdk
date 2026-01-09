package com.github.thought2code.mcp.annotated.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.thought2code.mcp.annotated.exception.McpServerConfigurationException;
import com.github.thought2code.mcp.annotated.exception.McpServerJsonProcessingException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import org.junit.jupiter.api.Test;

class JacksonHelperTest {

  static class CircularReference {
    private final CircularReference self = this;
  }

  record Person(String name, int age) {}

  @Test
  void testConstructor_shouldThrowException() {
    assertThrows(UnsupportedOperationException.class, JacksonHelper::new);
  }

  @Test
  void testToJsonString_shouldSucceed() {
    String json = JacksonHelper.toJsonString(new Person("test", 25));
    assertTrue(json.contains("\"name\":\"test\""));
    assertTrue(json.contains("\"age\":25"));
  }

  @Test
  void testToJsonString_shouldThrowException() {
    assertThrows(
        McpServerJsonProcessingException.class,
        () -> JacksonHelper.toJsonString(new CircularReference()));
  }

  @Test
  void testFromJson_shouldSucceed() {
    String json = "{\"name\":\"test\",\"age\":25}";
    Person person = JacksonHelper.fromJson(json, Person.class);
    assertEquals("test", person.name);
    assertEquals(25, person.age);
  }

  @Test
  void testFromJson_shouldThrowException() {
    String json = "{\"name\":\"test\",\"age\":25}";
    assertThrows(
        McpServerJsonProcessingException.class,
        () -> JacksonHelper.fromJson(json, CircularReference.class));
  }

  @Test
  void testFromYaml_shouldSucceed() throws IOException {
    String yamlContent = "name: test\nage: 25";
    File tempYaml = File.createTempFile("test", ".yaml");
    try (FileWriter writer = new FileWriter(tempYaml)) {
      writer.write(yamlContent);
    }
    Person person = JacksonHelper.fromYaml(tempYaml, Person.class);
    assertEquals("test", person.name);
    assertEquals(25, person.age);
  }

  @Test
  void testFromYaml_shouldThrowException() {
    File f = new File("non-existent.yaml");
    assertThrows(McpServerConfigurationException.class, () -> JacksonHelper.fromYaml(f, Map.class));
  }
}
