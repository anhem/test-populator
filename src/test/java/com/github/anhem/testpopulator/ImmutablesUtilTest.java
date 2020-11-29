package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.model.immutables.ImmutableImmutablesInterface;
import com.github.anhem.testpopulator.model.immutables.ImmutablesInterface;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.anhem.testpopulator.ImmutablesUtil.*;
import static com.github.anhem.testpopulator.PopulateFactory.BUILDER_METHOD;
import static com.github.anhem.testpopulator.PopulateUtil.getDeclaredMethods;
import static org.assertj.core.api.Assertions.assertThat;

class ImmutablesUtilTest {

    @Test
    void getMethodsForImmutablesBuilderReturnsExpectedMethods() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Object builderObject = ImmutableImmutablesInterface.class.getDeclaredMethod(BUILDER_METHOD).invoke(null);
        List<String> allMethodNames = getMethodNames(PopulateUtil.getDeclaredMethods(builderObject.getClass()));
        assertThat(allMethodNames.stream().anyMatch(methodName -> methodName.startsWith(ADD_PREFIX))).isTrue();
        assertThat(allMethodNames.stream().anyMatch(methodName -> methodName.startsWith(ADD_ALL_PREFIX))).isTrue();
        assertThat(allMethodNames.stream().anyMatch(methodName -> methodName.startsWith(PUT_PREFIX))).isTrue();
        assertThat(allMethodNames.stream().anyMatch(methodName -> methodName.startsWith(PUT_ALL_PREFIX))).isTrue();

        List<Method> methodsForImmutablesBuilder = getMethodsForImmutablesBuilder(ImmutableImmutablesInterface.class, builderObject);

        assertThat(methodsForImmutablesBuilder).hasSize(getDeclaredMethods(ImmutablesInterface.class).size());
        List<String> methodNames = getMethodNames(methodsForImmutablesBuilder);
        assertThat(methodNames.stream().anyMatch(methodName -> methodName.startsWith(ADD_PREFIX))).isFalse();
        assertThat(methodNames.stream().anyMatch(methodName -> methodName.startsWith(ADD_ALL_PREFIX))).isFalse();
        assertThat(methodNames.stream().anyMatch(methodName -> methodName.startsWith(PUT_PREFIX))).isFalse();
        assertThat(methodNames.stream().anyMatch(methodName -> methodName.startsWith(PUT_ALL_PREFIX))).isFalse();
    }

    private List<String> getMethodNames(List<Method> methodsForImmutablesBuilder) {
        return methodsForImmutablesBuilder.stream().map(Method::getName)
                .collect(Collectors.toList());
    }

}
