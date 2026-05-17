package com.github.anhem.testpopulator.testutil;

import com.github.anhem.testpopulator.model.circular.A;
import com.github.anhem.testpopulator.model.circular.B;
import com.github.anhem.testpopulator.model.circular.C;
import com.github.anhem.testpopulator.model.circular.D;
import org.assertj.core.api.recursive.assertion.DefaultRecursiveAssertionIntrospectionStrategy;
import org.assertj.core.api.recursive.assertion.RecursiveAssertionConfiguration;
import org.assertj.core.api.recursive.assertion.RecursiveAssertionNode;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class AssertTestUtil {

    private static final String PROTOBUF_MESSAGE_CLASS = "com.google.protobuf.MessageLite";
    private static final String PROTOBUF_PACKAGE = "com.google.protobuf";

    public static final RecursiveAssertionConfiguration RECURSIVE_ASSERTION_CONFIGURATION = RecursiveAssertionConfiguration.builder()
            .withIntrospectionStrategy(new TypeIgnoringIntrospectionStrategy())
            .build();

    public static <T> void assertRandomlyPopulatedValues(T value1, T value2) {
        assertThat(value1).isNotNull();
        assertThat(value2).isNotNull();
        if (!isProtobufMessage(value1)) {
            assertThat(value1).hasNoNullFieldsOrProperties();
            assertThat(value2).hasNoNullFieldsOrProperties();
            assertThat(value1).usingRecursiveAssertion(RECURSIVE_ASSERTION_CONFIGURATION)
                    .hasNoNullFields();
            assertThat(value2).usingRecursiveAssertion(RECURSIVE_ASSERTION_CONFIGURATION)
                    .hasNoNullFields();
        }
        assertThat(value1).isNotEqualTo(value2);
        assertThat(value1).usingRecursiveAssertion(RECURSIVE_ASSERTION_CONFIGURATION)
                .isNotEqualTo(value2);
    }

    public static <T> void assertRandomlyPopulatedValues(T value1, T value2, String... ignoringFields) {
        assertThat(value1).isNotNull();
        assertThat(value2).isNotNull();
        if (!isProtobufMessage(value1)) {
            assertThat(value1).hasNoNullFieldsOrPropertiesExcept(ignoringFields);
            assertThat(value2).hasNoNullFieldsOrPropertiesExcept(ignoringFields);
            assertThat(value1).usingRecursiveAssertion(RECURSIVE_ASSERTION_CONFIGURATION)
                    .ignoringFields(ignoringFields)
                    .hasNoNullFields();
            assertThat(value2).usingRecursiveAssertion(RECURSIVE_ASSERTION_CONFIGURATION)
                    .ignoringFields(ignoringFields)
                    .hasNoNullFields();
        }
        assertThat(value1).isNotEqualTo(value2);
        assertThat(value1).usingRecursiveAssertion(RECURSIVE_ASSERTION_CONFIGURATION)
                .ignoringFields(ignoringFields)
                .isNotEqualTo(value2);
    }

    private static boolean isProtobufMessage(Object obj) {
        try {
            Class<?> protobufClass = Class.forName(PROTOBUF_MESSAGE_CLASS);
            return protobufClass.isInstance(obj);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static void assertRandomlyPopulatedValues(String value1, String value2) {
        assertThat(value1).isNotNull();
        assertThat(value2).isNotNull();
        assertThat(value1).isNotEqualTo(value2);
    }

    public static void assertCircularDependency(A a1, A a2) {
        assertThat(a1).isNotNull();
        assertThat(a2).isNotNull();
        assertThat(a1).isNotEqualTo(a2);
        assertThat(a1).usingRecursiveAssertion().isNotEqualTo(a2);
        assertCircularDependency(a1);
        assertCircularDependency(a2);
    }

    private static void assertCircularDependency(A a) {
        assertThat(a).hasNoNullFieldsOrProperties();

        B b = a.getB();
        assertThat(b).hasNoNullFieldsOrProperties();

        C c = b.getC();
        assertThat(c).hasNoNullFieldsOrProperties();

        List<C> cs = c.getCList();
        assertThat(cs).isEmpty();

        D d = c.getD();
        assertThat(d).hasAllNullFieldsOrProperties();

        C cdKey = a.getCdMap().keySet().iterator().next();
        assertThat(cdKey).hasNoNullFieldsOrProperties();
        assertThat(cdKey.getCList()).isEmpty();
        assertThat(cdKey.getCSet()).isEmpty();
        assertThat(cdKey.getCMap()).isEmpty();
        assertThat(cdKey.getCArrayList()).isEmpty();
        assertThat(cdKey.getCHashSet()).isEmpty();

        assertThat(cdKey.getCHashMap()).hasSize(1);
        assertThat(cdKey.getCHashMap().values().iterator().next()).isNull();

        D cdKeyD = cdKey.getD();
        assertThat(cdKeyD.getA()).isNull();

        B cdKeyDB = cdKeyD.getB();
        assertThat(cdKeyDB.getC()).isNull();
        assertThat(cdKeyDB.getString()).isNotNull();

        assertThat(cdKeyD.getC()).isNull();

        D cdValue = a.getCdMap().values().iterator().next();
        assertThat(cdValue.getA()).isNull();

        B cdValueB = cdValue.getB();

        C cdValueBC = cdValueB.getC();
        assertThat(cdValueBC.getD()).isNull();
        assertThat(cdValueBC.getCList()).isEmpty();

        assertThat(cdValueB.getString()).isNotNull();
    }

    private static class TypeIgnoringIntrospectionStrategy extends DefaultRecursiveAssertionIntrospectionStrategy {
        private static final List<Class<?>> IGNORED_TYPES = List.of(
                Path.class,
                TimeZone.class,
                File.class,
                Locale.class,
                Currency.class,
                Charset.class
        );

        @Override
        public List<RecursiveAssertionNode> getChildNodesOf(Object node) {
            return super.getChildNodesOf(node).stream()
                    .filter(childNode -> childNode.type != null && !childNode.type.getName().startsWith(PROTOBUF_PACKAGE))
                    .filter(childNode -> IGNORED_TYPES.stream().noneMatch(ignoredType -> ignoredType.isAssignableFrom(childNode.type)))
                    .collect(Collectors.toList());
        }
    }

}
