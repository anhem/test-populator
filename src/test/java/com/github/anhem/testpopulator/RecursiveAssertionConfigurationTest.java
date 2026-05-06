package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.model.java.setter.Pojo;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static com.github.anhem.testpopulator.testutil.AssertTestUtil.RECURSIVE_ASSERTION_CONFIGURATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RecursiveAssertionConfigurationTest {

    @Test
    void recursiveAssertionFailsWithoutConfigurationDueToInternalPathFields() {
        PathModel pathModel = new PathModel();
        pathModel.path = Path.of("test");

        assertThat(pathModel).hasNoNullFieldsOrProperties();
        assertThatThrownBy(() -> assertThat(pathModel).usingRecursiveAssertion().hasNoNullFields())
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("The following fields did not satisfy the predicate");
    }

    @Test
    void recursiveAssertionPassesWithConfigurationWhenPathIsSet() {
        PathModel pathModel = new PathModel();
        pathModel.path = Path.of("test");

        assertThat(pathModel).hasNoNullFieldsOrProperties();
        assertThat(pathModel).usingRecursiveAssertion(RECURSIVE_ASSERTION_CONFIGURATION).hasNoNullFields();
    }

    @Test
    void recursiveAssertionFailsWithConfigurationWhenNestedFieldIsNull() {
        Wrapper wrapper = new Wrapper();
        wrapper.pojo = new Pojo();
        wrapper.pojo.setPath(Path.of("test"));
        wrapper.pojo.setStringValue(null);

        assertThat(wrapper).hasNoNullFieldsOrProperties();
        assertThatThrownBy(() -> assertThat(wrapper).usingRecursiveAssertion(RECURSIVE_ASSERTION_CONFIGURATION).hasNoNullFields())
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("pojo.stringValue");
    }

    private static class PathModel {
        Path path;
    }

    private static class Wrapper {
        Pojo pojo;
    }
}
