package com.github.anhem.testpopulator.internal.object.builder;

import com.github.anhem.testpopulator.internal.object.BuildType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.github.anhem.testpopulator.internal.object.BuildType.LIST;
import static com.github.anhem.testpopulator.internal.object.BuildType.SETTER;
import static org.assertj.core.api.Assertions.assertThat;

class ContainerObjectBuilderTest {

    private static final String TYPED_COLLECTION_TEMPLATE = "%1$s %2$s<%3$s> %4$s = new %2$s<>();";
    private static final String SETTER_TEMPLATE = "%1$s %2$s %4$s = new %2$s();";

    @Test
    void buildCollection() {
        ContainerObjectBuilder builder = ContainerObjectBuilder.builder()
                .clazz(ArrayList.class)
                .name("arrayList")
                .buildType(LIST)
                .template(CodeTemplate.TYPED_COLLECTION.getFormat())
                .useFullyQualifiedName(false)
                .expectedChildren(1)
                .parameterized(true)
                .build();
        MethodBuilder method = new MethodBuilder("add", 1);
        TemplateObjectBuilder val = TemplateObjectBuilder.builder()
                .clazz(String.class)
                .name("string_0")
                .buildType(BuildType.VALUE)
                .expectedChildren(0)
                .build();
        val.setValue("\"foo\"");
        method.addChild(val);
        builder.addChild(method);

        List<String> result = builder.build();

        assertThat(result).containsExactly(
                "public static final ArrayList<String> arrayList = new ArrayList<>();",
                "static {",
                "arrayList.add(\"foo\");",
                "}"
        );
    }

    @Test
    void buildCollectionWithNullValues() {
        ContainerObjectBuilder builder = ContainerObjectBuilder.builder()
                .clazz(ArrayList.class)
                .name("arrayList")
                .buildType(LIST)
                .template(CodeTemplate.TYPED_COLLECTION.getFormat())
                .useFullyQualifiedName(false)
                .expectedChildren(1)
                .parameterized(true)
                .build();
        MethodBuilder method = new MethodBuilder("add", 1);
        TemplateObjectBuilder val = TemplateObjectBuilder.builder()
                .clazz(String.class)
                .name("string_0")
                .buildType(BuildType.VALUE)
                .expectedChildren(0)
                .build();
        val.setValue("null");
        method.addChild(val);
        builder.addChild(method);

        List<String> result = builder.build();

        assertThat(result).containsExactly("public static final ArrayList<String> arrayList = new ArrayList<>();");
    }

    @Test
    void buildSetter() {
        ContainerObjectBuilder builder = ContainerObjectBuilder.builder()
                .clazz(Object.class)
                .name("myObj")
                .buildType(SETTER)
                .template(CodeTemplate.SETTER.getFormat())
                .useFullyQualifiedName(false)
                .expectedChildren(1)
                .parameterized(false)
                .build();
        MethodBuilder method = new MethodBuilder("setFoo", 1);
        TemplateObjectBuilder val = TemplateObjectBuilder.builder()
                .clazz(String.class)
                .name("string_0")
                .buildType(BuildType.VALUE)
                .expectedChildren(0)
                .build();
        val.setValue("\"bar\"");
        method.addChild(val);
        builder.addChild(method);

        List<String> result = builder.build();

        assertThat(result).containsExactly(
                "public static final Object myObj = new Object();",
                "static {",
                "myObj.setFoo(\"bar\");",
                "}"
        );
    }
}
