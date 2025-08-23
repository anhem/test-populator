package com.github.anhem.testpopulator.internal.util;

import com.github.anhem.testpopulator.config.MethodType;
import com.github.anhem.testpopulator.model.java.constructor.AllArgsConstructor;
import com.github.anhem.testpopulator.model.java.stc.*;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static com.github.anhem.testpopulator.config.Strategy.CONSTRUCTOR;
import static com.github.anhem.testpopulator.config.Strategy.STATIC_METHOD;
import static com.github.anhem.testpopulator.internal.util.StaticMethodUtil.getStaticMethod;
import static com.github.anhem.testpopulator.internal.util.StaticMethodUtil.isMatchingStaticMethodStrategy;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

class StaticMethodUtilTest {

    @Test
    void isMatchingStaticMethodStrategyReturnsTrue() {
        assertThat(isMatchingStaticMethodStrategy(STATIC_METHOD, User.class)).isTrue();
    }

    @Test
    void isMatchingStaticMethodStrategyReturnsFalse() {
        assertThat(isMatchingStaticMethodStrategy(CONSTRUCTOR, User.class)).isFalse();
        assertThat(isMatchingStaticMethodStrategy(STATIC_METHOD, AllArgsConstructor.class)).isFalse();
    }


    @Test
    void getStaticMethodWithMethodTypeLargestReturnsMethod() {
        assertThat(getStaticMethod(Users.class, emptyList(), MethodType.LARGEST))
                .isNotNull()
                .extracting(Method::getName)
                .isEqualTo("ofTwo");
    }

    @Test
    void getStaticMethodWithMethodTypeSmallestReturnsMethod() {
        assertThat(getStaticMethod(Users.class, emptyList(), MethodType.SMALLEST))
                .isNotNull()
                .extracting(Method::getName)
                .isIn("with", "of");
    }

    @Test
    void getStaticMethodWithMethodTypeWillNotReturnMethodWithSelfReferencingParameter() {
        assertThat(getStaticMethod(User.class, emptyList(), MethodType.LARGEST))
                .isNotNull()
                .extracting(Method::getName)
                .isEqualTo("of");
        assertThat(getStaticMethod(User.class, emptyList(), MethodType.SMALLEST))
                .isNotNull()
                .extracting(Method::getName)
                .isEqualTo("of");
        assertThat(getStaticMethod(User.class, emptyList(), MethodType.SIMPLEST))
                .isNotNull()
                .extracting(Method::getName)
                .isEqualTo("of");
    }

    @Test
    void getStaticMethodWithDifferentMethodTypesWillReturnDifferentMethods() {
        assertThat(getStaticMethod(MultipleStaticMethods.class, emptyList(), MethodType.LARGEST))
                .isNotNull()
                .extracting(Method::getName)
                .isEqualTo("createFull");
        assertThat(getStaticMethod(MultipleStaticMethods.class, emptyList(), MethodType.SMALLEST))
                .isNotNull()
                .extracting(Method::getName)
                .isEqualTo("fromCsvRecord");
        assertThat(getStaticMethod(MultipleStaticMethods.class, emptyList(), MethodType.SIMPLEST))
                .isNotNull()
                .extracting(Method::getName)
                .isEqualTo("fromIdAndName");
        assertThat(getStaticMethod(UserGroup.class, emptyList(), MethodType.SIMPLEST))
                .isNotNull()
                .extracting(Method::getName)
                .isEqualTo("from");
        assertThat(getStaticMethod(Users.class, emptyList(), MethodType.SIMPLEST))
                .isNotNull()
                .extracting(Method::getName)
                .isEqualTo("with");
        assertThat(getStaticMethod(UserId.class, emptyList(), MethodType.SIMPLEST))
                .isNotNull()
                .extracting(Method::getName)
                .isEqualTo("of");
    }

}