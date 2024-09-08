package com.github.anhem.testpopulator.testutil;

import static org.assertj.core.api.Assertions.assertThat;

public class AssertTestUtil {

    public static <T> void assertRandomlyPopulatedValues(T value_1, T value_2) {
        assertThat(value_1).isNotNull();
        assertThat(value_2).isNotNull();
        assertThat(value_1).hasNoNullFieldsOrProperties();
        assertThat(value_2).hasNoNullFieldsOrProperties();
        assertThat(value_1).usingRecursiveAssertion().hasNoNullFields();
        assertThat(value_2).usingRecursiveAssertion().hasNoNullFields();
        assertThat(value_1).isNotEqualTo(value_2);
    }

    public static void assertRandomlyPopulatedValues(String value_1, String value_2) {
        assertThat(value_1).isNotNull();
        assertThat(value_2).isNotNull();
        assertThat(value_1).isNotEqualTo(value_2);
    }
}
