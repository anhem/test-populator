package com.github.anhem.testpopulator.util;

import com.github.anhem.testpopulator.model.immutables.ImmutableImmutablesAbstract;
import com.github.anhem.testpopulator.model.immutables.ImmutableImmutablesInterface;
import com.github.anhem.testpopulator.model.immutables.ImmutablesAbstract;
import com.github.anhem.testpopulator.model.immutables.ImmutablesInterface;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.anhem.testpopulator.PopulateFactory.BUILDER_METHOD;
import static com.github.anhem.testpopulator.testutil.PopulateConfigTestUtil.DEFAULT_POPULATE_CONFIG;
import static com.github.anhem.testpopulator.util.ImmutablesUtil.*;
import static com.github.anhem.testpopulator.util.PopulateUtil.getDeclaredMethods;
import static org.assertj.core.api.Assertions.assertThat;

class ImmutablesUtilTest {

    @Test
    void getMethodsForImmutablesBuilderReturnsExpectedMethods() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Object builderObject = ImmutableImmutablesInterface.class.getDeclaredMethod(BUILDER_METHOD).invoke(null);
        List<String> allMethodNames = getMethodNames(getDeclaredMethods(builderObject.getClass(), DEFAULT_POPULATE_CONFIG.getBlacklistedMethods()));
        assertThat(allMethodNames.stream().anyMatch(methodName -> methodName.startsWith(ADD_PREFIX))).isTrue();
        assertThat(allMethodNames.stream().anyMatch(methodName -> methodName.startsWith(ADD_ALL_PREFIX))).isTrue();
        assertThat(allMethodNames.stream().anyMatch(methodName -> methodName.startsWith(PUT_PREFIX))).isTrue();
        assertThat(allMethodNames.stream().anyMatch(methodName -> methodName.startsWith(PUT_ALL_PREFIX))).isTrue();

        List<Method> methodsForImmutablesBuilder = getMethodsForImmutablesBuilder(ImmutableImmutablesInterface.class, builderObject, DEFAULT_POPULATE_CONFIG.getBlacklistedMethods());

        assertThat(methodsForImmutablesBuilder).hasSize(getDeclaredMethods(ImmutablesInterface.class, DEFAULT_POPULATE_CONFIG.getBlacklistedMethods()).size());
        List<String> methodNames = getMethodNames(methodsForImmutablesBuilder);
        assertThat(methodNames.stream().anyMatch(methodName -> methodName.startsWith(ADD_PREFIX))).isFalse();
        assertThat(methodNames.stream().anyMatch(methodName -> methodName.startsWith(ADD_ALL_PREFIX))).isFalse();
        assertThat(methodNames.stream().anyMatch(methodName -> methodName.startsWith(PUT_PREFIX))).isFalse();
        assertThat(methodNames.stream().anyMatch(methodName -> methodName.startsWith(PUT_ALL_PREFIX))).isFalse();
    }

    @Test
    void getImmutablesGeneratedClassReturnsExpectedClass() {
        assertThat(getImmutablesGeneratedClass(String.class)).isEqualTo(String.class);
        assertThat(getImmutablesGeneratedClass(ImmutablesInterface.class)).isEqualTo(ImmutableImmutablesInterface.class);
        assertThat(getImmutablesGeneratedClass(ImmutableImmutablesInterface.class)).isEqualTo(ImmutableImmutablesInterface.class);
        assertThat(getImmutablesGeneratedClass(ImmutablesAbstract.class)).isEqualTo(ImmutableImmutablesAbstract.class);
        assertThat(getImmutablesGeneratedClass(ImmutableImmutablesAbstract.class)).isEqualTo(ImmutableImmutablesAbstract.class);
    }

    private List<String> getMethodNames(List<Method> methodsForImmutablesBuilder) {
        return methodsForImmutablesBuilder.stream().map(Method::getName)
                .collect(Collectors.toList());
    }

}
