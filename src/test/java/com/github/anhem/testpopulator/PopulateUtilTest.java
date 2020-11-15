package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.OverridePopulate;
import com.github.anhem.testpopulator.config.Strategy;
import com.github.anhem.testpopulator.model.java.*;
import com.github.anhem.testpopulator.model.java.override.MyUUID;
import com.github.anhem.testpopulator.model.java.override.MyUUIDOverride;
import com.github.anhem.testpopulator.model.lombok.Lombok;
import org.junit.jupiter.api.Test;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.anhem.testpopulator.PopulateUtil.*;
import static com.github.anhem.testpopulator.config.Strategy.*;
import static org.assertj.core.api.Assertions.assertThat;

class PopulateUtilTest {

    @Test
    void toArgumentTypesReturnsTypeArgumentsAsList() throws NoSuchFieldException {
        Type[] typeArguments = ((ParameterizedType) Pojo.class.getDeclaredField("setOfStrings")
                .getGenericType()).getActualTypeArguments();
        assertThat(typeArguments).hasSize(1);

        List<Type> argumentTypes = toArgumentTypes(null, typeArguments);

        assertThat(argumentTypes).hasSize(1);
        assertThat(argumentTypes.get(0)).isEqualTo(typeArguments[0]);
    }

    @Test
    void toArgumentTypesReturnsParameterArgumentTypes() {
        Parameter parameter = Arrays.stream(getLargestPublicConstructor(AllArgsConstructor.class).getParameters())
                .filter(p -> p.getType().equals(Set.class))
                .findFirst()
                .orElseThrow();

        List<Type> argumentTypes = toArgumentTypes(parameter, null);

        assertThat(argumentTypes).hasSize(1);
        assertThat(argumentTypes.get(0)).isEqualTo(String.class);
    }

    @Test
    void getOverridePopulateValueReturnsValue() {
        Map<Class<MyUUID>, OverridePopulate<?>> overridePopulate = Map.of(MyUUID.class, new MyUUIDOverride());

        Object overridePopulateValue = getOverridePopulateValue(MyUUID.class, overridePopulate);

        assertThat(overridePopulateValue).isNotNull();
        assertThat(overridePopulateValue.getClass()).isEqualTo(MyUUID.class);
        assertThat(((MyUUID) overridePopulateValue).getUuid()).isNotNull();
    }

    @Test
    void getDeclaredFieldsReturnsAllDeclaredFields() {
        List<Field> declaredFields = getDeclaredFields(PojoExtendsPojoExtendsPojoAbstract.class);

        assertThat(declaredFields).isNotEmpty();
        assertThat(declaredFields).hasSize(19);
        List<String> fieldNames = declaredFields.stream()
                .map(Field::getName)
                .collect(Collectors.toList());
        assertThat(fieldNames).contains("anotherInteger", "anotherString", "stringValue", "integerValue");
    }

    @Test
    void getDeclaredMethodsReturnAllDeclaredMethods() {
        List<Method> declaredMethods = getDeclaredMethods(PojoExtendsPojoExtendsPojoAbstract.class);

        assertThat(declaredMethods).isNotEmpty();
        List<String> fieldNames = declaredMethods.stream()
                .map(Method::getName)
                .collect(Collectors.toList());
        assertThat(fieldNames).contains("setAnotherInteger", "setAnotherString", "setStringValue", "setIntegerValue");
    }

    @Test
    void isSetReturnsFalse() {
        assertThat(isSet(String.class)).isFalse();
    }

    @Test
    void isSetReturnsTrue() {
        assertThat(isSet(Set.class)).isTrue();
    }

    @Test
    void isMapReturnsFalse() {
        assertThat(isMap(String.class)).isFalse();
    }

    @Test
    void isMapReturnsTrue() {
        assertThat(isMap(Map.class)).isTrue();
    }

    @Test
    void isCollectionReturnsFalse() {
        assertThat(isCollection(PojoExtendsPojoAbstract.class)).isFalse();
        assertThat(isCollection(String.class)).isFalse();
    }

    @Test
    void isCollectionReturnsTrue() {
        assertThat(isCollection(List.class)).isTrue();
        assertThat(isCollection(Set.class)).isTrue();
        assertThat(isCollection(Collection.class)).isTrue();
        assertThat(isCollection(Map.class)).isTrue();
        assertThat(isCollection(ArrayList.class)).isTrue();
    }

    @Test
    void isValueReturnsFalse() {
        assertThat(isValue(Pojo.class)).isFalse();
    }

    @Test
    void isValueReturnsTrue() {
        assertThat(isValue(String.class)).isTrue();
        assertThat(isValue(ArbitraryEnum.class)).isTrue();
    }

    @Test
    void isJavaBaseClassReturnsFalse() {
        assertThat(isJavaBaseClass(PojoExtendsPojoAbstract.class)).isFalse();
    }

    @Test
    void isJavaBaseClassReturnsTrue() {
        assertThat(isJavaBaseClass(String.class)).isTrue();
    }

    @Test
    void hasAtLeastOneParameterReturnsFalse() {
        Method method = getMethod("getStringValue", Pojo.class);

        assertThat(hasAtLeastOneParameter(method)).isFalse();
    }

    @Test
    void hasAtLeastOneParameterReturnsTrue() {
        Method method = getMethod("setStringValue", Pojo.class);

        assertThat(hasAtLeastOneParameter(method)).isTrue();
    }

    @Test
    void isMatchingSetterStrategyReturnsTrue() {
        assertThat(isMatchingSetterStrategy(SETTER, PojoExtendsPojoAbstract.class)).isTrue();
    }

    @Test
    void isMatchingSetterStrategyReturnsFalse() {
        assertThat(isMatchingSetterStrategy(Strategy.CONSTRUCTOR, PojoExtendsPojoAbstract.class)).isFalse();
        assertThat(isMatchingSetterStrategy(Strategy.SETTER, AllArgsConstructorExtendsAllArgsConstructorAbstract.class)).isFalse();

    }

    @Test
    void isMatchingConstructorStrategyReturnsTrue() {
        assertThat(isMatchingConstructorStrategy(CONSTRUCTOR, AllArgsConstructorExtendsAllArgsConstructorAbstract.class)).isTrue();
    }

    @Test
    void isMatchingConstructorStrategyReturnsFalse() {
        assertThat(isMatchingConstructorStrategy(FIELD, AllArgsConstructorExtendsAllArgsConstructorAbstract.class)).isFalse();
        assertThat(isMatchingConstructorStrategy(CONSTRUCTOR, PojoExtendsPojoAbstract.class)).isFalse();
    }

    @Test
    void isMatchingFieldStrategyReturnsTrue() {
        assertThat(isMatchingFieldStrategy(Strategy.FIELD, PojoExtendsPojoAbstract.class)).isTrue();
    }

    @Test
    void isMatchingFieldStrategyReturnsFalse() {
        assertThat(isMatchingFieldStrategy(Strategy.CONSTRUCTOR, PojoExtendsPojoAbstract.class)).isFalse();
        assertThat(isMatchingFieldStrategy(Strategy.FIELD, AllArgsConstructorExtendsAllArgsConstructorAbstract.class)).isFalse();
    }

    @Test
    void isMatchingLombokBuilderStrategyReturnsTrue() {
        assertThat(isMatchingLombokBuilderStrategy(LOMBOK_BUILDER, Lombok.class)).isTrue();
    }

    @Test
    void isMatchingLombokBuilderStrategyReturnsFalse() {
        assertThat(isMatchingLombokBuilderStrategy(Strategy.CONSTRUCTOR, Lombok.class)).isFalse();
        assertThat(isMatchingLombokBuilderStrategy(LOMBOK_BUILDER, PojoExtendsPojoAbstract.class)).isFalse();
    }

    @Test
    void isSetterReturnsTrue() {
        List<Method> setterMethods = getDeclaredMethods(Pojo.class).stream()
                .filter(PopulateUtil::isSetter)
                .collect(Collectors.toList());

        assertThat(setterMethods).isNotEmpty();
        assertThat(setterMethods).hasSize(17);
        setterMethods.forEach(method -> assertThat(method.getName()).startsWith("set"));
        setterMethods.forEach(method -> assertThat(method.getReturnType()).isEqualTo(void.class));
    }

    @Test
    void isBlackListedMethodReturnsTrue() {
        Method method = getMethod("$jacocoInit", HasBlackListedMethod.class);

        assertThat(isBlackListedMethod(method)).isTrue();
    }

    @Test
    void isBlackListedMethodReturnsFalse() {
        Method method = getMethod("getStringValue", Pojo.class);

        assertThat(isBlackListedMethod(method)).isFalse();
    }

    private Method getMethod(String methodName, Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredMethods())
                .filter(m -> m.getName().equals(methodName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Could not find method with name " + methodName));
    }

}
