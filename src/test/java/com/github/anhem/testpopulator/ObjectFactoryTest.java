package com.github.anhem.testpopulator;

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
        objectFactory.constructor(MyClass.class);
        objectFactory.value("myString");
        objectFactory.value(1);

        assertThat(objectFactory.toTop().build()).isEqualTo("public static final MyClass myClass0 = new MyClass(\"myString\", 1);");
    }

    @Test
    void createObjectUsingSetter() {
        objectFactory.setter(MyClass.class);
        objectFactory.method("setString");
        objectFactory.value("myString");
        objectFactory.method("setInteger");
        objectFactory.value(1);

        assertThat(objectFactory.toTop().build()).isEqualTo(String.format(
                "public static final MyClass myClass0 = new MyClass();%s" +
                        "myClass0.setString(\"myString\");%s" +
                        "myClass0.setInteger(1);",
                LS, LS));
    }

    @Test
    void createObjectUsingLombokBuilder() {
        objectFactory.builder(MyClass.class);
        objectFactory.method("string");
        objectFactory.value("myString");
        objectFactory.method("integer");
        objectFactory.value(1);

        assertThat(objectFactory.toTop().build()).isEqualTo(String.format(
                "public static final MyClass myClass0 = MyClass.builder()%s" +
                        ".string(\"myString\")%s" +
                        ".integer(1)%s" +
                        ".build();",
                LS, LS, LS));
    }

    @Test
    void createObjectUsingImmutablesBuilder() {
        objectFactory.builder(ImmutableMyClass.class);
        objectFactory.method("string");
        objectFactory.value("myString");
        objectFactory.method("integer");
        objectFactory.value(1);

        assertThat(objectFactory.toTop().build()).isEqualTo(String.format(
                "public static final MyClass myClass0 = ImmutableMyClass.builder()%s" +
                        ".string(\"myString\")%s" +
                        ".integer(1)%s" +
                        ".build();",
                LS, LS, LS));
    }

    @Test
    void createSetOf() {
        objectFactory.constructor(MyClass.class);
        objectFactory.setOf();
        objectFactory.value("myString");

        assertThat(objectFactory.toTop().build()).isEqualTo("public static final MyClass myClass0 = new MyClass(Set.of(\"myString\"));");
    }

    @Test
    void createSet() {
        objectFactory.constructor(MyClass.class);
        objectFactory.set(ArrayList.class);
        objectFactory.value("myString");

        assertThat(objectFactory.toTop().build()).isEqualTo(String.format(
                "public static final ArrayList<String> arrayList0 = new ArrayList();%s" +
                        "arrayList0.add(\"myString\");%s" +
                        "public static final MyClass myClass0 = new MyClass(arrayList0);",
                LS, LS));
    }

    @Test
    void createMapOf() {
        objectFactory.constructor(MyClass.class);
        objectFactory.mapOf();
        objectFactory.value("myKey");
        objectFactory.value("myValue");

        assertThat(objectFactory.toTop().build()).isEqualTo("public static final MyClass myClass0 = new MyClass(Map.of(\"myKey\", \"myValue\"));");
    }

    @Test
    void createMap() {
        objectFactory.constructor(MyClass.class);
        objectFactory.map(HashMap.class);
        objectFactory.value("myKey");
        objectFactory.value("myValue");

        assertThat(objectFactory.toTop().build()).isEqualTo(String.format(
                "public static final HashMap<String, String> hashMap0 = new HashMap();%s" +
                        "hashMap0.put(\"myKey\", \"myValue\");%s" +
                        "public static final MyClass myClass0 = new MyClass(hashMap0);",
                LS, LS));
    }

    @Test
    void createListOf() {
        objectFactory.constructor(MyClass.class);
        objectFactory.listOf();
        objectFactory.value("myString");

        assertThat(objectFactory.toTop().build()).isEqualTo("public static final MyClass myClass0 = new MyClass(List.of(\"myString\"));");
    }

    @Test
    void createList() {
        objectFactory.constructor(MyClass.class);
        objectFactory.list(ArrayList.class);
        objectFactory.value("myString");

        assertThat(objectFactory.toTop().build()).isEqualTo(String.format(
                "public static final ArrayList<String> arrayList0 = new ArrayList();%s" +
                        "arrayList0.add(\"myString\");%s" +
                        "public static final MyClass myClass0 = new MyClass(arrayList0);",
                LS, LS));
    }

    @Test
    void createArray() {
        objectFactory.constructor(MyClass.class);
        objectFactory.array(Boolean.class);
        objectFactory.value(true);

        assertThat(objectFactory.toTop().build()).isEqualTo("public static final MyClass myClass0 = new MyClass(new Boolean[]{true});");
    }

    @Test
    void overrideValue() {
        objectFactory.overridePopulate(UUID.class, new MyUUIDOverride());

        assertThat(objectFactory.toTop().build()).isEqualTo("public static final UUID uUID0 = UUID.fromString(\"156585fd-4fe5-4ed4-8d59-d8d70d8b96f5\");");
    }

    @Test
    void value() {
        objectFactory.value("myString");
        assertThat(objectFactory.toTop().build()).isEqualTo("public static final String string0 = \"myString\";");
    }

    @Test
    void allValues() {
        objectFactory.constructor(MyClass.class);
        objectFactory.value(ArbitraryEnum.A);
        objectFactory.value(1);
        objectFactory.value(2L);
        objectFactory.value(3D);
        objectFactory.value(true);
        objectFactory.value(BigDecimal.ONE);
        objectFactory.value("myString");
        objectFactory.value(LocalDate.EPOCH);
        objectFactory.value(LocalDate.EPOCH.atTime(0, 0, 0));
        objectFactory.value(LocalDate.EPOCH.atTime(0, 0, 0).atZone(ZoneId.of("UTC")));
        objectFactory.value(LocalDate.EPOCH.atTime(0, 0, 0).atZone(ZoneId.of("UTC")).toInstant());
        objectFactory.value('c');
        objectFactory.value(UUID.fromString("82e8962f-885d-4845-914b-c206a42d7c91"));

        assertThat(objectFactory.toTop().build()).isEqualTo("public static final MyClass myClass0 = new MyClass(" +
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
        Assertions.assertThrows(ObjectException.class, () -> objectFactory.value(Pojo.class));
    }


    private static class MyClass {
    }

    private static class ImmutableMyClass {
    }

}
