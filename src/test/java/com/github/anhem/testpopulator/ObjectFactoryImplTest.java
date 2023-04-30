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


public class ObjectFactoryImplTest {

    private ObjectFactoryImpl objectFactoryImpl;

    @BeforeEach
    void setUp() {
        objectFactoryImpl = new ObjectFactoryImpl();
    }

    @Test
    void createObjectUsingConstructor() {
        objectFactoryImpl.constructor(MyClass.class, 2);
        objectFactoryImpl.value("myString");
        objectFactoryImpl.value(1);

        assertThat(objectFactoryImpl.build()).isEqualTo(List.of("public static final MyClass myClass0 = new MyClass(\"myString\", 1);"));
    }

    @Test
    void createObjectUsingSetter() {
        objectFactoryImpl.setter(MyClass.class);
        objectFactoryImpl.method("setString", 1);
        objectFactoryImpl.value("myString");
        objectFactoryImpl.method("setInteger", 1);
        objectFactoryImpl.value(1);

        assertThat(objectFactoryImpl.build()).isEqualTo(List.of(
                "public static final MyClass myClass0 = new MyClass();",
                "myClass0.setString(\"myString\");",
                "myClass0.setInteger(1);"
        ));
    }

    @Test
    void createObjectUsingBuilder() {
        objectFactoryImpl.builder(MyClass.class, 2);
        objectFactoryImpl.method("string", 1);
        objectFactoryImpl.value("myString");
        objectFactoryImpl.method("integer", 1);
        objectFactoryImpl.value(1);

        assertThat(objectFactoryImpl.build()).isEqualTo(List.of(
                "public static final MyClass myClass0 = MyClass.builder()",
                ".string(\"myString\")",
                ".integer(1)",
                ".build();"
        ));
    }

    @Test
    void createSetOf() {
        objectFactoryImpl.constructor(MyClass.class, 1);
        objectFactoryImpl.setOf();
        objectFactoryImpl.value("myString");

        assertThat(objectFactoryImpl.build()).isEqualTo(List.of(
                "public static final Set<String> set0 = Set.of(\"myString\");",
                "public static final MyClass myClass0 = new MyClass(set0);"
        ));
    }

    @Test
    void createSet() {
        objectFactoryImpl.constructor(MyClass.class, 1);
        objectFactoryImpl.set(ArrayList.class);
        objectFactoryImpl.value("myString");

        assertThat(objectFactoryImpl.build()).isEqualTo(List.of(
                "public static final ArrayList<String> arrayList0 = new ArrayList<>();",
                "arrayList0.add(\"myString\");",
                "public static final MyClass myClass0 = new MyClass(arrayList0);"
        ));
    }

    @Test
    void createMapOf() {
        objectFactoryImpl.constructor(MyClass.class, 1);
        objectFactoryImpl.mapOf();
        objectFactoryImpl.value("myKey");
        objectFactoryImpl.value("myValue");

        assertThat(objectFactoryImpl.build()).isEqualTo(List.of(
                "public static final Map<String, String> map0 = Map.of(\"myKey\", \"myValue\");",
                "public static final MyClass myClass0 = new MyClass(map0);"
        ));
    }

    @Test
    void createMap() {
        objectFactoryImpl.constructor(MyClass.class, 1);
        objectFactoryImpl.map(HashMap.class);
        objectFactoryImpl.value("myKey");
        objectFactoryImpl.value("myValue");

        assertThat(objectFactoryImpl.build()).isEqualTo(List.of(
                "public static final HashMap<String, String> hashMap0 = new HashMap<>();",
                "hashMap0.put(\"myKey\", \"myValue\");",
                "public static final MyClass myClass0 = new MyClass(hashMap0);"
        ));
    }

    @Test
    void createListOf() {
        objectFactoryImpl.constructor(MyClass.class, 1);
        objectFactoryImpl.listOf();
        objectFactoryImpl.value("myString");

        assertThat(objectFactoryImpl.build()).isEqualTo(List.of(
                "public static final List<String> list0 = List.of(\"myString\");",
                "public static final MyClass myClass0 = new MyClass(list0);"
        ));
    }

    @Test
    void createList() {
        objectFactoryImpl.constructor(MyClass.class, 1);
        objectFactoryImpl.list(ArrayList.class);
        objectFactoryImpl.value("myString");

        assertThat(objectFactoryImpl.build()).isEqualTo(List.of(
                "public static final ArrayList<String> arrayList0 = new ArrayList<>();",
                "arrayList0.add(\"myString\");",
                "public static final MyClass myClass0 = new MyClass(arrayList0);"
        ));
    }

    @Test
    void createArray() {
        objectFactoryImpl.constructor(MyClass.class, 1);
        objectFactoryImpl.array(Boolean.class);
        objectFactoryImpl.value(true);
        assertThat(objectFactoryImpl.build()).isEqualTo(List.of(
                "public static final Boolean[] boolean0 = new Boolean[]{true};",
                "public static final MyClass myClass0 = new MyClass(boolean0);"
        ));
    }

    @Test
    void overrideValue() {
        objectFactoryImpl.overridePopulate(UUID.class, new MyUUIDOverride());

        assertThat(objectFactoryImpl.build()).isEqualTo(List.of(
                "public static final UUID uUID0 = UUID.fromString(\"156585fd-4fe5-4ed4-8d59-d8d70d8b96f5\");"
        ));
    }

    @Test
    void value() {
        objectFactoryImpl.value("myString");
        assertThat(objectFactoryImpl.build()).isEqualTo(List.of("public static final String string0 = \"myString\";"));
    }

    @Test
    void allValues() {
        objectFactoryImpl.constructor(MyClass.class, 13);
        objectFactoryImpl.value(ArbitraryEnum.A);
        objectFactoryImpl.value(1);
        objectFactoryImpl.value(2L);
        objectFactoryImpl.value(3D);
        objectFactoryImpl.value(true);
        objectFactoryImpl.value(BigDecimal.ONE);
        objectFactoryImpl.value("myString");
        objectFactoryImpl.value(LocalDate.EPOCH);
        objectFactoryImpl.value(LocalDate.EPOCH.atTime(0, 0, 0));
        objectFactoryImpl.value(LocalDate.EPOCH.atTime(0, 0, 0).atZone(ZoneId.of("UTC")));
        objectFactoryImpl.value(LocalDate.EPOCH.atTime(0, 0, 0).atZone(ZoneId.of("UTC")).toInstant());
        objectFactoryImpl.value('c');
        objectFactoryImpl.value(UUID.fromString("82e8962f-885d-4845-914b-c206a42d7c91"));

        assertThat(objectFactoryImpl.build()).isEqualTo(List.of(
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
        Assertions.assertThrows(ObjectException.class, () -> objectFactoryImpl.value(Pojo.class));
    }

    private static class MyClass {
    }

}
