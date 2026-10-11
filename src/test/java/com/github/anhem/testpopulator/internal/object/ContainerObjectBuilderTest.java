package com.github.anhem.testpopulator.internal.object;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.github.anhem.testpopulator.internal.object.BuildType.*;
import static org.assertj.core.api.Assertions.assertThat;

class ContainerObjectBuilderTest {

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
        TemplateObjectBuilder method = TemplateObjectBuilder.builder()
                .name("add")
                .buildType(METHOD)
                .expectedChildren(1)
                .build();
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
        TemplateObjectBuilder method = TemplateObjectBuilder.builder()
                .name("add")
                .buildType(METHOD)
                .expectedChildren(1)
                .build();
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
        TemplateObjectBuilder method = TemplateObjectBuilder.builder()
                .name("setFoo")
                .buildType(METHOD)
                .expectedChildren(1)
                .build();
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

    @Test
    void buildPublicField() {
        ContainerObjectBuilder builder = ContainerObjectBuilder.builder()
                .clazz(Object.class)
                .name("myObj")
                .buildType(SETTER)
                .template(CodeTemplate.SETTER.getFormat())
                .useFullyQualifiedName(false)
                .expectedChildren(1)
                .parameterized(false)
                .build();
        TemplateObjectBuilder field = TemplateObjectBuilder.builder()
                .name("x")
                .buildType(PUBLIC_FIELD)
                .expectedChildren(1)
                .build();
        TemplateObjectBuilder val = TemplateObjectBuilder.builder()
                .clazz(Float.class)
                .name("float_0")
                .buildType(BuildType.VALUE)
                .expectedChildren(0)
                .build();
        val.setValue("1.0f");
        field.addChild(val);
        builder.addChild(field);

        List<String> result = builder.build();

        assertThat(result).containsExactly(
                "public static final Object myObj = new Object();",
                "static {",
                "myObj.x = 1.0f;",
                "}"
        );
    }

    @Test
    void buildPublicFieldKotlin() {
        ContainerObjectBuilder builder = ContainerObjectBuilder.builder()
                .clazz(Object.class)
                .name("myObj")
                .buildType(SETTER)
                .template(CodeTemplate.SETTER.getFormat(true))
                .useFullyQualifiedName(false)
                .expectedChildren(1)
                .parameterized(false)
                .isKotlinSupport(true)
                .build();
        TemplateObjectBuilder field = TemplateObjectBuilder.builder()
                .name("x")
                .buildType(PUBLIC_FIELD)
                .expectedChildren(1)
                .isKotlinSupport(true)
                .build();
        TemplateObjectBuilder val = TemplateObjectBuilder.builder()
                .clazz(Float.class)
                .name("float_0")
                .buildType(BuildType.VALUE)
                .expectedChildren(0)
                .isKotlinSupport(true)
                .build();
        val.setValue("1.0f");
        field.addChild(val);
        builder.addChild(field);

        List<String> result = builder.build();

        assertThat(result).containsExactly(
                "public static final myObj: Object = Object()",
                "static {",
                "myObj.x = 1.0f",
                "}"
        );
    }

    @Test
    void buildGetterWithPublicField() {
        ContainerObjectBuilder builder = ContainerObjectBuilder.builder()
                .clazz(Object.class)
                .name("myObj")
                .buildType(SETTER)
                .template(CodeTemplate.SETTER.getFormat(false))
                .useFullyQualifiedName(false)
                .expectedChildren(1)
                .parameterized(false)
                .isKotlinSupport(false)
                .build();
        TemplateObjectBuilder getter = TemplateObjectBuilder.builder()
                .name("getTarget")
                .buildType(GETTER)
                .expectedChildren(1)
                .isKotlinSupport(false)
                .build();
        TemplateObjectBuilder field = TemplateObjectBuilder.builder()
                .name("x")
                .buildType(PUBLIC_FIELD)
                .expectedChildren(1)
                .isKotlinSupport(false)
                .build();
        TemplateObjectBuilder val = TemplateObjectBuilder.builder()
                .clazz(Float.class)
                .name("float_0")
                .buildType(BuildType.VALUE)
                .expectedChildren(0)
                .isKotlinSupport(false)
                .build();
        val.setValue("1.0f");
        field.addChild(val);
        field.setParent(getter);
        getter.addChild(field);
        getter.setParent(builder);
        builder.addChild(getter);

        List<String> result = builder.build();

        assertThat(result).containsExactly(
                "public static final Object myObj = new Object();",
                "static {",
                "myObj.getTarget().x = 1.0f;",
                "}"
        );
    }

    @Test
    void buildGetterWithPublicFieldKotlin() {
        ContainerObjectBuilder builder = ContainerObjectBuilder.builder()
                .clazz(Object.class)
                .name("myObj")
                .buildType(SETTER)
                .template(CodeTemplate.SETTER.getFormat(true))
                .useFullyQualifiedName(false)
                .expectedChildren(1)
                .parameterized(false)
                .isKotlinSupport(true)
                .build();
        TemplateObjectBuilder getter = TemplateObjectBuilder.builder()
                .name("getTarget")
                .buildType(GETTER)
                .expectedChildren(1)
                .isKotlinSupport(true)
                .build();
        TemplateObjectBuilder field = TemplateObjectBuilder.builder()
                .name("x")
                .buildType(PUBLIC_FIELD)
                .expectedChildren(1)
                .isKotlinSupport(true)
                .build();
        TemplateObjectBuilder val = TemplateObjectBuilder.builder()
                .clazz(Float.class)
                .name("float_0")
                .buildType(BuildType.VALUE)
                .expectedChildren(0)
                .isKotlinSupport(true)
                .build();
        val.setValue("1.0f");
        field.addChild(val);
        field.setParent(getter);
        getter.addChild(field);
        getter.setParent(builder);
        builder.addChild(getter);

        List<String> result = builder.build();

        assertThat(result).containsExactly(
                "public static final myObj: Object = Object()",
                "static {",
                "myObj.target.x = 1.0f",
                "}"
        );
    }
}
