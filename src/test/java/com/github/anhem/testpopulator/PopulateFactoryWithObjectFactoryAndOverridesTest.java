package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.OverridePopulate;
import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.model.java.override.MyUUID;
import com.github.anhem.testpopulator.model.java.setter.Pojo;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static com.github.anhem.testpopulator.testutil.GeneratedCodeUtil.assertGeneratedCode;
import static org.assertj.core.api.Assertions.assertThat;

public class PopulateFactoryWithObjectFactoryAndOverridesTest {

    @Test
    void canGenerateCodeWithMethodsImportsAndStaticImports() {
        String uuidString = "82e8962f-885d-4845-914b-c206a42d7c91";
        MyUUID myUUID = new MyUUID(uuidString);
        
        PopulateConfig populateConfig = PopulateConfig.builder()
                .objectFactoryEnabled(true)
                .addOverride(MyUUID.class, new OverridePopulate<MyUUID>() {
                    @Override
                    public MyUUID create() {
                        return myUUID;
                    }

                    @Override
                    public String createCode() {
                        return "createMyUUID(singletonList(\"" + uuidString + "\").get(0))";
                    }

                    @Override
                    public Set<String> createMethods() {
                        return Set.of(
                                "\tprivate static MyUUID createMyUUID(String value) {\n" +
                                "\t\treturn new MyUUID(value);\n" +
                                "\t}"
                        );
                    }

                    @Override
                    public Set<String> createImports() {
                        return Set.of("com.github.anhem.testpopulator.model.java.override.MyUUID");
                    }

                    @Override
                    public Set<String> createStaticImports() {
                        return Set.of("java.util.Collections.singletonList");
                    }
                })
                .build();

        PopulateFactory populateFactory = new PopulateFactory(populateConfig);
        MyUUID result = populateFactory.populate(MyUUID.class);

        assertThat(result).isEqualTo(myUUID);
        assertGeneratedCode(result, populateConfig);
    }

    @Test
    void canGenerateCodeForPojoWithMultipleOverridesAndMethods() {
        String customString = "customStringValue";
        Integer customInt = 888;
        List<String> customList = List.of("A", "B");

        PopulateConfig populateConfig = PopulateConfig.builder()
                .objectFactoryEnabled(true)
                .addOverride("setStringValue", String.class, new OverridePopulate<String>() {
                    @Override
                    public String create() {
                        return customString;
                    }

                    @Override
                    public String createCode() {
                        return "createString()";
                    }

                    @Override
                    public Set<String> createMethods() {
                        return Set.of("\tprivate static String createString() { return \"customStringValue\"; }");
                    }
                })
                .addOverride("setIntegerValue", Integer.class, new OverridePopulate<Integer>() {
                    @Override
                    public Integer create() {
                        return customInt;
                    }

                    @Override
                    public String createCode() {
                        return "createInt()";
                    }

                    @Override
                    public Set<String> createMethods() {
                        return Set.of("\tprivate static Integer createInt() { return 888; }");
                    }
                })
                .addOverride("setListOfStrings", List.class, new OverridePopulate<List<String>>() {
                    @Override
                    public List<String> create() {
                        return customList;
                    }

                    @Override
                    public String createCode() {
                        return "createList()";
                    }

                    @Override
                    public Set<String> createMethods() {
                        return Set.of(
                                "\tprivate static List<String> createList() {\n" +
                                "\t\treturn Arrays.asList(\"A\", \"B\");\n" +
                                "\t}"
                        );
                    }

                    @Override
                    public Set<String> createImports() {
                        return Set.of("java.util.Arrays", "java.util.List");
                    }
                })
                .build();

        PopulateFactory populateFactory = new PopulateFactory(populateConfig);
        Pojo result = populateFactory.populate(Pojo.class);

        assertThat(result.getStringValue()).isEqualTo(customString);
        assertThat(result.getIntegerValue()).isEqualTo(customInt);
        assertThat(result.getListOfStrings()).isEqualTo(customList);
        assertThat(result.getOptionalInteger()).isPresent();
        assertThat(result.getOptionalString()).isPresent();
        com.github.anhem.testpopulator.testutil.GeneratedCodeUtil.assertGeneratedCodeContains(result, populateConfig,
                "private static String createString() { return \"customStringValue\"; }",
                "private static Integer createInt() { return 888; }",
                "private static List<String> createList() {",
                "return Arrays.asList(\"A\", \"B\");",
                "Optional.ofNullable("
        );
        assertGeneratedCode(result, populateConfig);
    }
}
