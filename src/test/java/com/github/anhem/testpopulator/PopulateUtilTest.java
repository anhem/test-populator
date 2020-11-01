package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.OverridePopulate;
import com.github.anhem.testpopulator.config.Strategy;
import com.github.anhem.testpopulator.model.java.*;
import com.github.anhem.testpopulator.model.java.override.MyUUID;
import com.github.anhem.testpopulator.model.java.override.MyUUIDOverride;
import org.junit.jupiter.api.Test;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.anhem.testpopulator.PopulateUtil.*;
import static org.assertj.core.api.Assertions.assertThat;

class PopulateUtilTest {

    @Test
    public void toArgumentTypesReturnsTypeArgumentsAsList() throws NoSuchFieldException {
        Type[] typeArguments = ((ParameterizedType) Pojo.class.getDeclaredField("setOfStrings")
                .getGenericType()).getActualTypeArguments();
        assertThat(typeArguments).hasSize(1);

        List<Type> argumentTypes = toArgumentTypes(null, typeArguments);

        assertThat(argumentTypes).hasSize(1);
        assertThat(argumentTypes.get(0)).isEqualTo(typeArguments[0]);
    }

    @Test
    public void toArgumentTypesReturnsParameterArgumentTypes() {
        Parameter parameter = Arrays.stream(getLargestConstructor(AllArgsConstructor.class).getParameters())
                .filter(p -> p.getType().equals(Set.class))
                .findFirst()
                .orElseThrow();

        List<Type> argumentTypes = toArgumentTypes(parameter, null);

        assertThat(argumentTypes).hasSize(1);
        assertThat(argumentTypes.get(0)).isEqualTo(String.class);
    }

    @Test
    public void getOverridePopulateValueReturnsValue() {
        Map<Class<MyUUID>, OverridePopulate<?>> overridePopulate = Map.of(MyUUID.class, new MyUUIDOverride());

        Object overridePopulateValue = getOverridePopulateValue(MyUUID.class, overridePopulate);

        assertThat(overridePopulateValue).isNotNull();
        assertThat(overridePopulateValue.getClass()).isEqualTo(MyUUID.class);
        assertThat(((MyUUID) overridePopulateValue).getUuid()).isNotNull();
    }

    @Test
    public void getDeclaredFieldsReturnsAllDeclaredFields() {
        List<Field> declaredFields = getDeclaredFields(PojoExtendsPojoExtendsPojoAbstract.class);

        assertThat(declaredFields).isNotEmpty();
        List<String> fieldNames = declaredFields.stream()
                .map(Field::getName)
                .collect(Collectors.toList());
        assertThat(fieldNames).contains("anotherInteger", "anotherString", "stringValue", "integerValue");
    }

    @Test
    public void hasOnlyDefaultConstructorReturnsFalse() {
        assertThat(hasOnlyDefaultConstructor(AllArgsConstructorExtendsAllArgsConstructorAbstract.class)).isFalse();
    }

    @Test
    public void hasOnlyDefaultConstructorReturnsTrue() {
        assertThat(hasOnlyDefaultConstructor(PojoExtendsPojoAbstract.class)).isTrue();
    }

    @Test
    public void isSetReturnsFalse() {
        assertThat(isSet(String.class)).isFalse();
    }

    @Test
    public void isSetReturnsTrue() {
        assertThat(isSet(Set.class)).isTrue();
    }

    @Test
    public void isMapReturnsFalse() {
        assertThat(isMap(String.class)).isFalse();
    }

    @Test
    public void isMapReturnsTrue() {
        assertThat(isMap(Map.class)).isTrue();
    }

    @Test
    public void isCollectionReturnsFalse() {
        assertThat(isCollection(PojoExtendsPojoAbstract.class)).isFalse();
        assertThat(isCollection(String.class)).isFalse();
    }

    @Test
    public void isCollectionReturnsTrue() {
        assertThat(isCollection(List.class)).isTrue();
        assertThat(isCollection(Set.class)).isTrue();
        assertThat(isCollection(Collection.class)).isTrue();
        assertThat(isCollection(Map.class)).isTrue();
        assertThat(isCollection(ArrayList.class)).isTrue();
    }

    @Test
    public void isValueReturnsFalse() {
        assertThat(isValue(Pojo.class)).isFalse();
    }

    @Test
    public void isValueReturnsTrue() {
        assertThat(isValue(String.class)).isTrue();
        assertThat(isValue(ArbitraryEnum.class)).isTrue();
    }

    @Test
    public void isJavaBaseClassReturnsFalse() {
        assertThat(isJavaBaseClass(PojoExtendsPojoAbstract.class)).isFalse();
    }

    @Test
    public void isJavaBaseClassReturnsTrue() {
        assertThat(isJavaBaseClass(String.class)).isTrue();
    }

    @Test
    public void hasAtLeastOneParameterReturnsFalse() {
        Method method = getMethod("getStringValue", Pojo.class);

        assertThat(hasAtLeastOneParameter(method)).isFalse();
    }

    @Test
    public void hasAtLeastOneParameterReturnsTrue() {
        Method method = getMethod("setStringValue", Pojo.class);

        assertThat(hasAtLeastOneParameter(method)).isTrue();
    }

    @Test
    public void isMatchingFieldStrategyReturnsTrue() {
        assertThat(isMatchingFieldStrategy(Strategy.FIELD, PojoExtendsPojoAbstract.class)).isTrue();
    }

    @Test
    public void isMatchingFieldStrategyReturnsFalse() {
        assertThat(isMatchingFieldStrategy(Strategy.CONSTRUCTOR, PojoExtendsPojoAbstract.class)).isFalse();
        assertThat(isMatchingFieldStrategy(Strategy.FIELD, AllArgsConstructorExtendsAllArgsConstructorAbstract.class)).isFalse();
    }

    @Test
    public void isMatchingConstructorStrategyReturnsTrue() {
        assertThat(isMatchingConstructorStrategy(Strategy.CONSTRUCTOR, AllArgsConstructorExtendsAllArgsConstructorAbstract.class)).isTrue();
    }

    @Test
    public void isMatchingConstructorStrategyReturnsFalse() {
        assertThat(isMatchingConstructorStrategy(Strategy.FIELD, AllArgsConstructorExtendsAllArgsConstructorAbstract.class)).isFalse();
        assertThat(isMatchingConstructorStrategy(Strategy.CONSTRUCTOR, PojoExtendsPojoAbstract.class)).isFalse();
    }

    private Method getMethod(String methodName, Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredMethods())
                .filter(m -> m.getName().equals(methodName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Could not find method with name " + methodName));
    }

}
