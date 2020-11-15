package com.github.anhem.testpopulator.testutil;

import static org.assertj.core.api.Assertions.assertThat;

public class AssertTestUtil {

    public static void assertRandomlyPopulatedValues(Object value_1, Object value_2) {
        assertThat(value_1).isNotNull();
        assertThat(value_2).isNotNull();
        assertThat(value_1).hasNoNullFieldsOrProperties();
        assertThat(value_2).hasNoNullFieldsOrProperties();
        assertThat(value_1).isNotEqualTo(value_2);
    }
}
