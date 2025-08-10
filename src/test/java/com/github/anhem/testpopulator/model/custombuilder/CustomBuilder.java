package com.github.anhem.testpopulator.model.custombuilder;

import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class CustomBuilder {
    private final String string;
    private final double doubleValue;
    private final int intValue;
    private final List<String> strings;
    private final boolean booleanValue;
    private final Map<String, String> stringStringMap;
    private final Map<Integer, String> integerStringMap;

    private CustomBuilder(CustomBuilderBuilder builder) {
        this.string = builder.string;
        this.doubleValue = builder.doubleValue;
        this.intValue = builder.intValue;
        this.strings = builder.strings;
        this.booleanValue = builder.booleanValue;
        this.stringStringMap = builder.stringStringMap;
        this.integerStringMap = builder.integerStringMap;
    }

    public static CustomBuilderBuilder builder() {
        return new CustomBuilderBuilder();
    }

    public static class CustomBuilderBuilder {
        private String string;
        private double doubleValue;
        private int intValue;
        private List<String> strings;
        private boolean booleanValue;
        private Map<String, String> stringStringMap;
        private Map<Integer, String> integerStringMap = new HashMap<>();

        public CustomBuilderBuilder string(String string) {
            this.string = string;
            return this;
        }

        public CustomBuilderBuilder doubleValue(double doubleValue) {
            this.doubleValue = doubleValue;
            return this;
        }

        public CustomBuilderBuilder intValue(int intValue) {
            this.intValue = intValue;
            return this;
        }

        public CustomBuilderBuilder strings(List<String> strings) {
            this.strings = strings;
            return this;
        }

        public CustomBuilderBuilder available(boolean booleanValue) {
            this.booleanValue = booleanValue;
            return this;
        }

        public CustomBuilderBuilder stringStringMap(Map<String, String> stringStringMap) {
            this.stringStringMap = stringStringMap;
            return this;
        }

        public CustomBuilderBuilder addIntStringMap(Integer integer, String string) {
            this.integerStringMap.put(integer, string);
            return this;
        }

        public CustomBuilder build() {
            return new CustomBuilder(this);
        }
    }
}
