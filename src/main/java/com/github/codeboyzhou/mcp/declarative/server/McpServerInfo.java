package com.github.codeboyzhou.mcp.declarative.server;

public class McpServerInfo {

    private final String name;

    private final String version;

    private final String instructions;

    protected McpServerInfo(Builder<?> builder) {
        this.name = builder.name;
        this.version = builder.version;
        this.instructions = builder.instructions;
    }

    public static Builder<?> builder() {
        return new Builder<>();
    }

    public String name() {
        return name;
    }

    public String version() {
        return version;
    }

    public String instructions() {
        return instructions;
    }

    @SuppressWarnings("unchecked")
    public static class Builder<T extends Builder<T>> {

        protected String name;

        protected String version;

        protected String instructions;

        protected T self() {
            return (T) this;
        }

        public McpServerInfo build() {
            return new McpServerInfo(this);
        }

        public T name(String name) {
            this.name = name;
            return self();
        }

        public T version(String version) {
            this.version = version;
            return self();
        }

        public T instructions(String instructions) {
            this.instructions = instructions;
            return self();
        }

    }

}
