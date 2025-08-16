package com.github.anhem.testpopulator.model.java.stc;

import lombok.Value;

import java.io.InputStream;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Value
public class MultipleStaticMethods {

    private static final List<String> LIST = List.of("");
    private static final Map<String, Integer> MAP = Map.of("", 0);
    private static final Instant NOW = Instant.now();
    private static final UUID UUID = java.util.UUID.randomUUID();

    int id;
    String name;
    double value;
    List<String> tags;
    Map<String, Integer> properties;
    Instant creationTimestamp;
    UUID uniqueId;

    private MultipleStaticMethods(
            int id,
            String name,
            double value,
            List<String> tags,
            Map<String, Integer> properties,
            Instant creationTimestamp,
            UUID uniqueId
    ) {
        this.id = id;
        this.name = name;
        this.value = value;
        this.tags = tags;
        this.properties = properties;
        this.creationTimestamp = creationTimestamp;
        this.uniqueId = uniqueId;
    }

    public static MultipleStaticMethods fromIdAndName(int id, String name) {
        return new MultipleStaticMethods(id, name, 0.0, LIST, MAP, NOW, UUID);
    }

    public static MultipleStaticMethods createFull(int id, String name, double value, List<String> tags, Map<String, Integer> properties, UUID uuid) {
        return new MultipleStaticMethods(id, name, value, tags, properties, NOW, uuid);
    }

    public static MultipleStaticMethods fromTimestamp(Instant timestamp) {
        return new MultipleStaticMethods(0, "from-timestamp", 0.0, LIST, MAP, timestamp, UUID);
    }

    public static MultipleStaticMethods fromCsvRecord(String[] record) {
        return new MultipleStaticMethods(0, record[0], 0.0, LIST, MAP, NOW, UUID);
    }

    public static MultipleStaticMethods fromInputStream(InputStream stream) {
        return new MultipleStaticMethods(0, "from-stream", 0.0, LIST, MAP, NOW, UUID);
    }

    public static MultipleStaticMethods createDefault() {
        return new MultipleStaticMethods(0, "default", 0.0, LIST, MAP, NOW, UUID);
    }

    public static MultipleStaticMethods fromExisting(MultipleStaticMethods other) {
        return new MultipleStaticMethods(other.id, other.name, other.value, other.tags, other.properties, other.creationTimestamp, other.uniqueId);
    }

    public static String getFactorySchema(int id) {
        return "id:int, name:String, value:double";
    }

    public void updateValue(double newValue) {
    }

    private static void internalHelper(int id) {
    }
}
