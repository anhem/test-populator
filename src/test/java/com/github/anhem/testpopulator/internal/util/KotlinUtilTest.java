package com.github.anhem.testpopulator.internal.util;

import com.github.anhem.testpopulator.model.java.setter.Pojo;
import com.github.anhem.testpopulator.model.kotlin.KotlinLikeClass;
import com.github.anhem.testpopulator.model.kotlin.KotlinLikeSingleton;
import com.github.anhem.testpopulator.model.kotlin.KotlinLikeWithCompanion;
import com.github.anhem.testpopulator.model.kotlin.KotlinLikeWithDelegate;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static com.github.anhem.testpopulator.internal.util.KotlinUtil.*;
import static com.github.anhem.testpopulator.testutil.FieldTestUtil.getField;
import static org.assertj.core.api.Assertions.assertThat;

class KotlinUtilTest {

    @Test
    void isKotlinConstructorReturnsFalse() throws NoSuchMethodException {
        Class<?>[] parameterTypes = Pojo.class.getConstructor().getParameterTypes();
        assertThat(parameterTypes.length > 0 && parameterTypes[parameterTypes.length - 1].getSimpleName().equals(KotlinUtil.KOTLIN_DEFAULT_CONSTRUCTOR_MARKER)).isFalse();
    }

    @Test
    void isKotlinConstructorReturnsTrue() {
        Class<?>[] parameterTypes = KotlinLikeClass.class.getDeclaredConstructors()[0].getParameterTypes();
        assertThat(parameterTypes.length > 0 && parameterTypes[parameterTypes.length - 1].getSimpleName().equals(KotlinUtil.KOTLIN_DEFAULT_CONSTRUCTOR_MARKER)).isTrue();
    }

    @Test
    void isKotlinDelegateReturnsTrue() {
        Field field = getField("myProp$delegate", KotlinLikeWithDelegate.class);
        assertThat(isKotlinDelegate(field, true)).isTrue();
    }

    @Test
    void isKotlinDelegateReturnsFalse() {
        Field field = getField("myProp", KotlinLikeWithDelegate.class);
        assertThat(isKotlinDelegate(field, true)).isFalse();
    }

    @Test
    void isKotlinSingletonReturnsTrue() {
        assertThat(isKotlinSingleton(KotlinLikeSingleton.class)).isTrue();
    }

    @Test
    void isKotlinSingletonReturnsFalse() {
        assertThat(isKotlinSingleton(Pojo.class)).isFalse();
    }

    @Test
    void hasKotlinCompanionReturnsTrue() {
        assertThat(hasKotlinCompanion(KotlinLikeWithCompanion.class)).isTrue();
    }

    @Test
    void hasKotlinCompanionReturnsFalse() {
        assertThat(hasKotlinCompanion(Pojo.class)).isFalse();
    }

    @Test
    void getCompanionObjectReturnsObject() {
        assertThat(getCompanionObject(KotlinLikeWithCompanion.class)).isNotNull();
    }

    @Test
    void getCompanionObjectReturnsNull() {
        assertThat(getCompanionObject(Pojo.class)).isNull();
    }
}
