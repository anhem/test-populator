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
import java.util.List;
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
        objectFactory.constructor(MyClass.class, 2);
        objectFactory.value("myString");
        objectFactory.value(1);

        assertThat(objectFactory.toTop().build()).isEqualTo("public static final MyClass myClass0 = new MyClass(\"myString\", 1);");
    }

    @Test
    void createObjectUsingSetter() {
        objectFactory.setter(MyClass.class);
        objectFactory.method("setString", 1);
        objectFactory.value("myString");
        objectFactory.method("setInteger", 1);
        objectFactory.value(1);

        assertThat(objectFactory.toTop().build()).isEqualTo(String.format(
                "public static final MyClass myClass0 = new MyClass();%s" +
                        "myClass0.setString(\"myString\");%s" +
                        "myClass0.setInteger(1);",
                LS, LS));
    }

    @Test
    void createObjectUsingBuilder() {
        objectFactory.builder(MyClass.class, 2);
        objectFactory.method("string", 1);
        objectFactory.value("myString");
        objectFactory.method("integer", 1);
        objectFactory.value(1);

        assertThat(objectFactory.toTop().buildByBuildType()).isEqualTo(List.of(
                "public static final MyClass myClass0 = MyClass.builder()",
                ".string(\"myString\")",
                ".integer(1)",
                ".build();"
        ));
    }

    @Test
    void createSetOf() {
        objectFactory.constructor(MyClass.class, 1);
        objectFactory.setOf();
        objectFactory.value("myString");

        assertThat(objectFactory.toTop().buildByBuildType()).isEqualTo(List.of(
                "public static final Set<String> set0 = Set.of(\"myString\");",
                "public static final MyClass myClass0 = new MyClass(set0);"
        ));
    }

    @Test
    void createSet() {
        objectFactory.constructor(MyClass.class, 1);
        objectFactory.set(ArrayList.class);
        objectFactory.value("myString");

        assertThat(objectFactory.toTop().buildByBuildType()).isEqualTo(List.of(
                "public static final ArrayList<String> arrayList0 = new ArrayList<>();",
                "arrayList0.add(\"myString\");",
                "public static final MyClass myClass0 = new MyClass(arrayList0);"
        ));
    }

    @Test
    void createMapOf() {
        objectFactory.constructor(MyClass.class, 1);
        objectFactory.mapOf();
        objectFactory.value("myKey");
        objectFactory.value("myValue");

        assertThat(objectFactory.toTop().buildByBuildType()).isEqualTo(List.of(
                "public static final Map<String, String> map0 = Map.of(\"myKey\", \"myValue\");",
                "public static final MyClass myClass0 = new MyClass(map0);"
        ));
    }

    @Test
    void createMap() {
        objectFactory.constructor(MyClass.class, 1);
        objectFactory.map(HashMap.class);
        objectFactory.value("myKey");
        objectFactory.value("myValue");

        assertThat(objectFactory.toTop().buildByBuildType()).isEqualTo(List.of(
                "public static final HashMap<String, String> hashMap0 = new HashMap<>();",
                "hashMap0.put(\"myKey\", \"myValue\");",
                "public static final MyClass myClass0 = new MyClass(hashMap0);"
        ));
    }

    @Test
    void createListOf() {
        objectFactory.constructor(MyClass.class, 1);
        objectFactory.listOf();
        objectFactory.value("myString");

        assertThat(objectFactory.toTop().buildByBuildType()).isEqualTo(List.of(
                "public static final List<String> list0 = List.of(\"myString\");",
                "public static final MyClass myClass0 = new MyClass(list0);"
        ));
    }

    @Test
    void createList() {
        objectFactory.constructor(MyClass.class, 1);
        objectFactory.list(ArrayList.class);
        objectFactory.value("myString");

        assertThat(objectFactory.toTop().buildByBuildType()).isEqualTo(List.of(
                "public static final ArrayList<String> arrayList0 = new ArrayList<>();",
                "arrayList0.add(\"myString\");",
                "public static final MyClass myClass0 = new MyClass(arrayList0);"
        ));
    }

    @Test
    void createArray() {
        objectFactory.constructor(MyClass.class, 1);
        objectFactory.array(Boolean.class);
        objectFactory.value(true);
        assertThat(objectFactory.toTop().buildByBuildType()).isEqualTo(List.of(
                "public static final Boolean[] boolean0 = new Boolean[]{true};",
                "public static final MyClass myClass0 = new MyClass(boolean0);"
        ));
    }

    @Test
    void overrideValue() {
        objectFactory.overridePopulate(UUID.class, new MyUUIDOverride());

        assertThat(objectFactory.toTop().buildByBuildType()).isEqualTo(List.of(
                "public static final UUID uUID0 = UUID.fromString(\"156585fd-4fe5-4ed4-8d59-d8d70d8b96f5\");"
        ));
    }

    @Test
    void value() {
        objectFactory.value("myString");
        assertThat(objectFactory.toTop().build()).isEqualTo("public static final String string0 = \"myString\";");
    }

    @Test
    void allValues() {
        objectFactory.constructor(MyClass.class, 13);
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

        assertThat(objectFactory.toTop().buildByBuildType()).isEqualTo(List.of(
                "public static final MyClass myClass0 = new MyClass(" +
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
                        ");"));
    }

    @Test
    void valueThrowsException() {
        Assertions.assertThrows(ObjectException.class, () -> objectFactory.value(Pojo.class));
    }

    private static class MyClass {
    }

}
