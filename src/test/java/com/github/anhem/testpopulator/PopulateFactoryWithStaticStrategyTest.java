package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.MethodType;
import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.model.java.stc.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.anhem.testpopulator.config.Strategy.STATIC_METHOD;
import static com.github.anhem.testpopulator.testutil.AssertTestUtil.assertRandomlyPopulatedValues;
import static com.github.anhem.testpopulator.testutil.GeneratedCodeUtil.assertGeneratedCode;
import static org.assertj.core.api.Assertions.assertThat;

class PopulateFactoryWithStaticStrategyTest {

    private PopulateConfig populateConfig;
    private PopulateFactory populateFactory;

    @BeforeEach
    void setUp() {
        populateConfig = PopulateConfig.builder()
                .strategyOrder(List.of(STATIC_METHOD))
                .objectFactoryEnabled(true)
                .build();
        populateFactory = new PopulateFactory(populateConfig);
    }

    @Test
    void string() {
        String value_1 = populateAndAssertWithGeneratedCode(String.class);
        String value_2 = populateAndAssertWithGeneratedCode(String.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    void userId() {
        UserId value_1 = populateAndAssertWithGeneratedCode(UserId.class);
        UserId value_2 = populateAndAssertWithGeneratedCode(UserId.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    void user() {
        User value_1 = populateAndAssertWithGeneratedCode(User.class);
        User value_2 = populateAndAssertWithGeneratedCode(User.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    void users() {
        Users value_1 = populateAndAssertWithGeneratedCode(Users.class);
        Users value_2 = populateAndAssertWithGeneratedCode(Users.class);
        assertRandomlyPopulatedValues(value_1, value_2);
        assertThat(List.of(value_1, value_2)).allSatisfy(users -> assertThat(users.getUsers()).hasSize(2));
    }

    @Test
    void userGroup() {
        UserGroup value_1 = populateAndAssertWithGeneratedCode(UserGroup.class);
        UserGroup value_2 = populateAndAssertWithGeneratedCode(UserGroup.class);
        assertRandomlyPopulatedValues(value_1, value_2);
        assertThat(List.of(value_1, value_2)).allSatisfy(users -> assertThat(users.getUsers()).hasSize(1));
    }

    @Test
    void multipleStaticMethods() {
        MultipleStaticMethods value_1 = populateAndAssertWithGeneratedCode(MultipleStaticMethods.class);
        MultipleStaticMethods value_2 = populateAndAssertWithGeneratedCode(MultipleStaticMethods.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    void multipleStaticMethodsWithSimplestMethodType() {
        populateConfig = PopulateConfig.builder()
                .strategyOrder(List.of(STATIC_METHOD))
                .objectFactoryEnabled(true)
                .methodType(MethodType.SIMPLEST)
                .build();
        populateFactory = new PopulateFactory(populateConfig);
        MultipleStaticMethods value_1 = populateAndAssertWithGeneratedCode(MultipleStaticMethods.class);
        MultipleStaticMethods value_2 = populateAndAssertWithGeneratedCode(MultipleStaticMethods.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    void multipleStaticMethodsWithSmallestMethodType() {
        populateConfig = PopulateConfig.builder()
                .strategyOrder(List.of(STATIC_METHOD))
                .objectFactoryEnabled(true)
                .methodType(MethodType.SMALLEST)
                .build();
        populateFactory = new PopulateFactory(populateConfig);
        MultipleStaticMethods value_1 = populateAndAssertWithGeneratedCode(MultipleStaticMethods.class);
        MultipleStaticMethods value_2 = populateAndAssertWithGeneratedCode(MultipleStaticMethods.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    private <T> T populateAndAssertWithGeneratedCode(Class<T> clazz) {
        assertThat(populateConfig.isObjectFactoryEnabled()).isTrue();
        assertThat(populateConfig.getStrategyOrder()).containsExactly(STATIC_METHOD);
        T value = populateFactory.populate(clazz);
        assertThat(value).isNotNull();
        assertThat(value).isInstanceOf(clazz);
        assertGeneratedCode(value, populateConfig);

        return value;
    }
}
