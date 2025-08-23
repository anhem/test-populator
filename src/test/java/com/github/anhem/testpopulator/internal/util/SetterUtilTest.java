package com.github.anhem.testpopulator.internal.util;

import com.github.anhem.testpopulator.model.java.constructor.AllArgsConstructorExtendsAllArgsConstructorAbstract;
import com.github.anhem.testpopulator.model.java.setter.Pojo;
import com.github.anhem.testpopulator.model.java.setter.PojoExtendsPojoAbstract;
import com.github.anhem.testpopulator.model.java.setter.PojoPrivateConstructor;
import com.github.anhem.testpopulator.model.java.setter.PojoWithCustomSetters;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;

import static com.github.anhem.testpopulator.config.Strategy.CONSTRUCTOR;
import static com.github.anhem.testpopulator.config.Strategy.SETTER;
import static com.github.anhem.testpopulator.internal.util.SetterUtil.getSetterMethods;
import static com.github.anhem.testpopulator.internal.util.SetterUtil.isMatchingSetterStrategy;
import static com.github.anhem.testpopulator.testutil.PopulateConfigTestUtil.DEFAULT_POPULATE_CONFIG;
import static org.assertj.core.api.Assertions.assertThat;

class SetterUtilTest {

    private static final String SETTER_PREFIX = "set";
    private static final List<String> SETTER_PREFIXES = List.of(SETTER_PREFIX);

    @Test
    void isMatchingSetterStrategyReturnsTrue() {
        assertThat(isMatchingSetterStrategy(SETTER, PojoExtendsPojoAbstract.class, SETTER_PREFIXES, false)).isTrue();
        assertThat(isMatchingSetterStrategy(SETTER, PojoPrivateConstructor.class, SETTER_PREFIXES, true)).isTrue();
    }

    @Test
    void isMatchingSetterStrategyReturnsFalse() {
        assertThat(isMatchingSetterStrategy(CONSTRUCTOR, PojoExtendsPojoAbstract.class, SETTER_PREFIXES, false)).isFalse();
        assertThat(isMatchingSetterStrategy(SETTER, AllArgsConstructorExtendsAllArgsConstructorAbstract.class, SETTER_PREFIXES, false)).isFalse();
        assertThat(isMatchingSetterStrategy(SETTER, PojoPrivateConstructor.class, SETTER_PREFIXES, false)).isFalse();
    }


    @Test
    void getSetterMethodsReturnsMethodsWhenRegularSetter() {
        List<Method> setterMethods = getSetterMethods(Pojo.class, DEFAULT_POPULATE_CONFIG.getBlacklistedMethods(), SETTER_PREFIXES);

        assertThat(setterMethods).isNotEmpty().hasSize(38);
        setterMethods.forEach(method -> assertThat(method.getName()).startsWith(SETTER_PREFIX));
        setterMethods.forEach(method -> assertThat(method.getReturnType()).isEqualTo(void.class));
    }

    @Test
    void getSetterMethodsReturnsMethodsWhenCustomSetter() {
        String setterPrefix = "with";
        List<Method> setterMethods = getSetterMethods(PojoWithCustomSetters.class, DEFAULT_POPULATE_CONFIG.getBlacklistedMethods(), List.of(setterPrefix));

        assertThat(setterMethods).isNotEmpty().hasSize(17);
        setterMethods.forEach(method -> assertThat(method.getName()).startsWith(setterPrefix));
        setterMethods.forEach(method -> assertThat(method.getReturnType()).isEqualTo(void.class));
    }

    @Test
    void getSetterMethodsReturnsMethodsWhenBlankSetter() {
        String setterPrefix = "";
        List<Method> setterMethods = getSetterMethods(PojoWithCustomSetters.class, DEFAULT_POPULATE_CONFIG.getBlacklistedMethods(), List.of(setterPrefix));

        assertThat(setterMethods).isNotEmpty().hasSize(17);
        setterMethods.forEach(method -> assertThat(method.getName()).startsWith(setterPrefix));
        setterMethods.forEach(method -> assertThat(method.getReturnType()).isEqualTo(void.class));
    }

}