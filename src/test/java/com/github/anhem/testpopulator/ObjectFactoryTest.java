package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.Strategy;
import com.github.anhem.testpopulator.model.java.ArbitraryEnum;
import com.github.anhem.testpopulator.model.java.Pojo;
import com.github.anhem.testpopulator.model.java.override.MyUUIDOverride;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class ObjectFactoryTest {

    public static final String LS = System.lineSeparator();
    private ObjectFactory objectFactory;

    @BeforeEach
    void setUp() {
        objectFactory = new ObjectFactory();
    }

    @Test
    void createObjectUsingConstructor() {
        objectFactory.startConstructor(MyClass.class);
        objectFactory.parameterDividerForConstructor(0);
        objectFactory.addValue("myString");
        objectFactory.parameterDividerForConstructor(1);
        objectFactory.addValue(1);
        objectFactory.endConstructor();

        assertThat(objectFactory.getTopObjectBuilder().build()).isEqualTo("public static final MyClass myClass0 = new MyClass(\"myString\", 1);");
    }

    @Test
    void createObjectUsingSetter() {
        objectFactory.startSetter(MyClass.class);
        objectFactory.startMethod(Strategy.SETTER, "setString");
        objectFactory.addValue("myString");
        objectFactory.endMethod(Strategy.SETTER);
        objectFactory.startMethod(Strategy.SETTER, "setInteger");
        objectFactory.addValue(1);
        objectFactory.endMethod(Strategy.SETTER);
        objectFactory.endSetter();

        assertThat(objectFactory.getTopObjectBuilder().build()).isEqualTo(String.format(
                "public static final MyClass myClass0 = new MyClass();%s" +
                        "myClass0.setString(\"myString\");%s" +
                        "myClass0.setInteger(1);",
                LS, LS));
    }

    @Test
    void createObjectUsingLombokBuilder() {
        objectFactory.startBuilder(MyClass.class);
        objectFactory.startMethod(Strategy.BUILDER, "string");
        objectFactory.addValue("myString");
        objectFactory.endMethod(Strategy.BUILDER);
        objectFactory.startMethod(Strategy.BUILDER, "integer");
        objectFactory.addValue(1);
        objectFactory.endMethod(Strategy.BUILDER);
        objectFactory.endBuilder();

        assertThat(objectFactory.getTopObjectBuilder().build()).isEqualTo(String.format(
                "public static final MyClass myClass0 = MyClass.builder()%s" +
                        ".string(\"myString\")%s" +
                        ".integer(1)%s" +
                        ".build();",
                LS, LS, LS));
    }

    @Test
    void createObjectUsingImmutablesBuilder() {
        objectFactory.startBuilder(MyClass.class, ImmutableMyClass.class);
        objectFactory.startMethod(Strategy.BUILDER, "string");
        objectFactory.addValue("myString");
        objectFactory.endMethod(Strategy.BUILDER);
        objectFactory.startMethod(Strategy.BUILDER, "integer");
        objectFactory.addValue(1);
        objectFactory.endMethod(Strategy.BUILDER);
        objectFactory.endBuilder();

        assertThat(objectFactory.getTopObjectBuilder().build()).isEqualTo(String.format(
                "public static final MyClass myClass0 = ImmutableMyClass.builder()%s" +
                        ".string(\"myString\")%s" +
                        ".integer(1)%s" +
                        ".build();",
                LS, LS, LS));
    }

    @Test
    void startMethodThrowsExceptionWhenWrongStrategy() {
        Assertions.assertThrows(ObjectException.class, () -> objectFactory.startMethod(Strategy.FIELD, "methodName"));
    }

    @Test
    void endMethodThrowsExceptionWhenWrongStrategy() {
        Assertions.assertThrows(ObjectException.class, () -> objectFactory.endMethod(Strategy.FIELD));
    }

    @Test
    void createSetOf() {
        objectFactory.startConstructor(MyClass.class);
        objectFactory.parameterDividerForConstructor(0);
        objectFactory.startSetOf();
        objectFactory.addValue("myString");
        objectFactory.endSetOf();
        objectFactory.endConstructor();

        assertThat(objectFactory.getTopObjectBuilder().build()).isEqualTo("public static final MyClass myClass0 = new MyClass(Set.of(\"myString\"));");
    }

    @Test
    void createSet() {
        objectFactory.startConstructor(MyClass.class);
        objectFactory.parameterDividerForConstructor(0);
        objectFactory.startSet(ArrayList.class, String.class);
        objectFactory.addValue("myString");
        objectFactory.endSet();
        objectFactory.endConstructor();

        assertThat(objectFactory.getTopObjectBuilder().build()).isEqualTo(String.format(
                "public static final ArrayList<String> arrayList0 = new ArrayList();%s" +
                        "arrayList0.add(\"myString\");%s" +
                        "public static final MyClass myClass0 = new MyClass(arrayList0);",
                LS, LS));
    }

    @Test
    void createMapOf() {
        objectFactory.startConstructor(MyClass.class);
        objectFactory.parameterDividerForConstructor(0);
        objectFactory.startMapOf();
        objectFactory.addValue("myKey");
        objectFactory.keyValueDividerForMapOf();
        objectFactory.addValue("myValue");
        objectFactory.endMapOf();
        objectFactory.endConstructor();

        assertThat(objectFactory.getTopObjectBuilder().build()).isEqualTo("public static final MyClass myClass0 = new MyClass(Map.of(\"myKey\", \"myValue\"));");
    }

    @Test
    void createMap() {
        objectFactory.startConstructor(MyClass.class);
        objectFactory.parameterDividerForConstructor(0);
        objectFactory.startMap(HashMap.class, String.class, String.class);
        objectFactory.startPutMap();
        objectFactory.addValue("myKey");
        objectFactory.keyValueDividerForPutMap();
        objectFactory.addValue("myValue");
        objectFactory.endPutMap();
        objectFactory.endMap();
        objectFactory.endConstructor();

        assertThat(objectFactory.getTopObjectBuilder().build()).isEqualTo(String.format(
                "public static final HashMap<String, String> hashMap0 = new HashMap();%s" +
                        "hashMap0.put(\"myKey\", \"myValue\");%s" +
                        "public static final MyClass myClass0 = new MyClass(hashMap0);",
                LS, LS));
    }

    @Test
    void createListOf() {
        objectFactory.startConstructor(MyClass.class);
        objectFactory.parameterDividerForConstructor(0);
        objectFactory.startListOf();
        objectFactory.addValue("myString");
        objectFactory.endListOf();
        objectFactory.endConstructor();

        assertThat(objectFactory.getTopObjectBuilder().build()).isEqualTo("public static final MyClass myClass0 = new MyClass(List.of(\"myString\"));");
    }

    @Test
    void createList() {
        objectFactory.startConstructor(MyClass.class);
        objectFactory.parameterDividerForConstructor(0);
        objectFactory.startList(ArrayList.class, String.class);
        objectFactory.addValue("myString");
        objectFactory.endList();
        objectFactory.endConstructor();

        assertThat(objectFactory.getTopObjectBuilder().build()).isEqualTo(String.format(
                "public static final ArrayList<String> arrayList0 = new ArrayList();%s" +
                        "arrayList0.add(\"myString\");%s" +
                        "public static final MyClass myClass0 = new MyClass(arrayList0);",
                LS, LS));
    }

    @Test
    void createArray() {
        objectFactory.startConstructor(MyClass.class);
        objectFactory.startArray(Boolean.class);
        objectFactory.addValue(true);
        objectFactory.endArray();
        objectFactory.parameterDividerForConstructor(0);
        objectFactory.endConstructor();

        assertThat(objectFactory.getTopObjectBuilder().build()).isEqualTo("public static final MyClass myClass0 = new MyClass(new Boolean[]{true});");
    }

    @Test
    void overrideValue() {
        objectFactory.addOverridePopulate(UUID.class, new MyUUIDOverride());

        assertThat(objectFactory.getTopObjectBuilder().build()).isEqualTo("public static final UUID uUID0 = UUID.fromString(\"156585fd-4fe5-4ed4-8d59-d8d70d8b96f5\");");
    }

    @Test
    void value() {
        objectFactory.addValue("myString");
        assertThat(objectFactory.getTopObjectBuilder().build()).isEqualTo("public static final String string0 = \"myString\"");
    }

    @Test
    void allValues() {
        objectFactory.startConstructor(MyClass.class);
        objectFactory.parameterDividerForConstructor(0);
        objectFactory.addValue(ArbitraryEnum.A);
        objectFactory.parameterDividerForConstructor(1);
        objectFactory.addValue(1);
        objectFactory.parameterDividerForConstructor(2);
        objectFactory.addValue(2L);
        objectFactory.parameterDividerForConstructor(3);
        objectFactory.addValue(3D);
        objectFactory.parameterDividerForConstructor(4);
        objectFactory.addValue(true);
        objectFactory.parameterDividerForConstructor(5);
        objectFactory.addValue(BigDecimal.ONE);
        objectFactory.parameterDividerForConstructor(6);
        objectFactory.addValue("myString");
        objectFactory.parameterDividerForConstructor(7);
        objectFactory.addValue(LocalDate.EPOCH);
        objectFactory.parameterDividerForConstructor(8);
        objectFactory.addValue(LocalDate.EPOCH.atTime(0, 0, 0));
        objectFactory.parameterDividerForConstructor(9);
        objectFactory.addValue(LocalDate.EPOCH.atTime(0, 0, 0).atZone(ZoneId.of("UTC")));
        objectFactory.parameterDividerForConstructor(10);
        objectFactory.addValue(LocalDate.EPOCH.atTime(0, 0, 0).atZone(ZoneId.of("UTC")).toInstant());
        objectFactory.parameterDividerForConstructor(11);
        objectFactory.addValue('c');
        objectFactory.parameterDividerForConstructor(12);
        objectFactory.addValue(UUID.fromString("82e8962f-885d-4845-914b-c206a42d7c91"));
        objectFactory.endConstructor();

        assertThat(objectFactory.getTopObjectBuilder().build()).isEqualTo("public static final MyClass myClass0 = new MyClass(" +
                "A, " +
                "1, " +
                "2L, " +
                "3.0, " +
                "true, " +
                "BigDecimal.valueOf(1), " +
                "\"myString\", " +
                "LocalDate.parse(\"1970-01-01\"), " +
                "LocalDateTime.parse(\"1970-01-01T00:00\"), " +
                "ZonedDateTime.parse(\"1970-01-01T00:00Z[UTC]\"), " +
                "Instant.parse(\"1970-01-01T00:00:00Z\"), " +
                "'c', " +
                "UUID.fromString(82e8962f-885d-4845-914b-c206a42d7c91)" +
                ");");
    }

    @Test
    void valueThrowsException() {
        Assertions.assertThrows(ObjectException.class, () -> objectFactory.addValue(Pojo.class));
    }


    private static class MyClass {
    }

    private static class ImmutableMyClass {
    }

}
