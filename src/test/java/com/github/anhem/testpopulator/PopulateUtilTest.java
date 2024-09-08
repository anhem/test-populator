package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.BuilderPattern;
import com.github.anhem.testpopulator.config.OverridePopulate;
import com.github.anhem.testpopulator.config.Strategy;
import com.github.anhem.testpopulator.model.java.*;
import com.github.anhem.testpopulator.model.java.override.MyUUID;
import com.github.anhem.testpopulator.model.java.override.MyUUIDOverride;
import com.github.anhem.testpopulator.model.lombok.LombokImmutable;
import org.junit.jupiter.api.Test;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.anhem.testpopulator.PopulateUtil.*;
import static com.github.anhem.testpopulator.config.Strategy.*;
import static com.github.anhem.testpopulator.testutil.FieldTestUtil.getField;
import static com.github.anhem.testpopulator.testutil.MethodTestUtil.getMethod;
import static com.github.anhem.testpopulator.testutil.PopulateConfigTestUtil.DEFAULT_POPULATE_CONFIG;
import static org.assertj.core.api.Assertions.assertThat;

class PopulateUtilTest {

    private static final String SETTER_PREFIX = "set";

    @Test
    void toArgumentTypesReturnsTypeArgumentsAsList() {
        Type[] typeArguments = getTypeArguments();

        List<Type> argumentTypes = toArgumentTypes(null, typeArguments);

        assertThat(argumentTypes).hasSize(1);
        assertThat(argumentTypes.get(0)).isEqualTo(typeArguments[0]);
    }

    @Test
    void toArgumentTypesReturnsParameterArgumentTypes() {
        Parameter parameter = getParameter();

        List<Type> argumentTypes = toArgumentTypes(parameter, null);

        assertThat(argumentTypes).hasSize(1);
        assertThat(argumentTypes.get(0)).isEqualTo(String.class);
    }

    @Test
    void getOverridePopulateValueReturnsValue() {
        Map<Class<?>, OverridePopulate<?>> overridePopulate = Map.of(MyUUID.class, new MyUUIDOverride());

        MyUUID myUUID = getOverridePopulateValue(MyUUID.class, overridePopulate);

        assertThat(myUUID).isNotNull();
        assertThat(myUUID.getClass()).isEqualTo(MyUUID.class);
        assertThat(myUUID.getUuid()).isNotNull();
    }

    @Test
    void getDeclaredFieldsReturnsAllDeclaredFields() {
        List<Field> declaredFields = getDeclaredFields(PojoExtendsPojoExtendsPojoAbstract.class, DEFAULT_POPULATE_CONFIG.getBlacklistedFields());

        assertThat(declaredFields).isNotEmpty().hasSize(20);
        List<String> fieldNames = declaredFields.stream()
                .map(Field::getName)
                .collect(Collectors.toList());
        assertThat(fieldNames).contains("anotherInteger", "anotherString", "stringValue", "integerValue");
    }

    @Test
    void getDeclaredMethodsReturnAllDeclaredMethods() {
        List<Method> declaredMethods = getDeclaredMethods(PojoExtendsPojoExtendsPojoAbstract.class, DEFAULT_POPULATE_CONFIG.getBlacklistedMethods());

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
    void isCollectionCarrierReturnsFalse() {
        ClassCarrier<String> classCarrier = Carrier.initialize(String.class, new ObjectFactoryVoid());

        assertThat(isCollectionCarrier(classCarrier)).isFalse();
    }

    @Test
    void isCollectionCarrierReturnsTrue() {
        CollectionCarrier<String> collectionCarrier = new CollectionCarrier<>(String.class, getParameter(), new ObjectFactoryVoid());

        assertThat(isCollectionCarrier(collectionCarrier)).isTrue();
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
        assertThat(isMatchingSetterStrategy(SETTER, PojoExtendsPojoAbstract.class, SETTER_PREFIX, false)).isTrue();
        assertThat(isMatchingSetterStrategy(SETTER, PojoPrivateConstructor.class, SETTER_PREFIX, true)).isTrue();
    }

    @Test
    void isMatchingSetterStrategyReturnsFalse() {
        assertThat(isMatchingSetterStrategy(Strategy.CONSTRUCTOR, PojoExtendsPojoAbstract.class, SETTER_PREFIX, false)).isFalse();
        assertThat(isMatchingSetterStrategy(Strategy.SETTER, AllArgsConstructorExtendsAllArgsConstructorAbstract.class, SETTER_PREFIX, false)).isFalse();
        assertThat(isMatchingSetterStrategy(SETTER, PojoPrivateConstructor.class, SETTER_PREFIX, false)).isFalse();
    }

    @Test
    void isMatchingConstructorStrategyReturnsTrue() {
        assertThat(isMatchingConstructorStrategy(CONSTRUCTOR, AllArgsConstructorExtendsAllArgsConstructorAbstract.class, false)).isTrue();
        assertThat(isMatchingConstructorStrategy(CONSTRUCTOR, AllArgsConstructorPrivate.class, true)).isTrue();
    }

    @Test
    void isMatchingConstructorStrategyReturnsFalse() {
        assertThat(isMatchingConstructorStrategy(FIELD, AllArgsConstructorExtendsAllArgsConstructorAbstract.class, false)).isFalse();
        assertThat(isMatchingConstructorStrategy(CONSTRUCTOR, PojoExtendsPojoAbstract.class, false)).isFalse();
        assertThat(isMatchingConstructorStrategy(CONSTRUCTOR, PojoPrivateConstructor.class, true)).isFalse();
        assertThat(isMatchingConstructorStrategy(CONSTRUCTOR, AllArgsConstructorPrivate.class, false)).isFalse();
    }

    @Test
    void isMatchingFieldStrategyReturnsTrue() {
        assertThat(isMatchingFieldStrategy(Strategy.FIELD, PojoExtendsPojoAbstract.class, false)).isTrue();
        assertThat(isMatchingFieldStrategy(Strategy.FIELD, PojoPrivateConstructor.class, true)).isTrue();
    }

    @Test
    void isMatchingFieldStrategyReturnsFalse() {
        assertThat(isMatchingFieldStrategy(Strategy.CONSTRUCTOR, PojoExtendsPojoAbstract.class, false)).isFalse();
        assertThat(isMatchingFieldStrategy(Strategy.FIELD, AllArgsConstructorExtendsAllArgsConstructorAbstract.class, false)).isFalse();
        assertThat(isMatchingFieldStrategy(Strategy.FIELD, PojoPrivateConstructor.class, false)).isFalse();
    }

    @Test
    void isMatchingBuilderStrategyReturnsTrue() {
        assertThat(isMatchingBuilderStrategy(BUILDER, LombokImmutable.class, BuilderPattern.LOMBOK)).isTrue();
    }

    @Test
    void isMatchingBuilderStrategyReturnsFalse() {
        assertThat(isMatchingBuilderStrategy(Strategy.CONSTRUCTOR, LombokImmutable.class, BuilderPattern.LOMBOK)).isFalse();
        assertThat(isMatchingBuilderStrategy(BUILDER, PojoExtendsPojoAbstract.class, BuilderPattern.LOMBOK)).isFalse();
    }

    @Test
    void isSetterReturnsTrue() {
        List<Method> setterMethods = getDeclaredMethods(Pojo.class, DEFAULT_POPULATE_CONFIG.getBlacklistedMethods()).stream()
                .filter(method -> isSetterMethod(method, SETTER_PREFIX))
                .collect(Collectors.toList());

        assertThat(setterMethods).isNotEmpty().hasSize(23);
        setterMethods.forEach(method -> assertThat(method.getName()).startsWith(SETTER_PREFIX));
        setterMethods.forEach(method -> assertThat(method.getReturnType()).isEqualTo(void.class));
    }

    @Test
    void isSetterReturnsTrueForCustomerSetter() {
        String setterPrefix = "with";
        List<Method> setterMethods = getDeclaredMethods(PojoWithCustomSetters.class, DEFAULT_POPULATE_CONFIG.getBlacklistedMethods()).stream()
                .filter(method -> isSetterMethod(method, setterPrefix))
                .collect(Collectors.toList());

        assertThat(setterMethods).isNotEmpty().hasSize(17);
        setterMethods.forEach(method -> assertThat(method.getName()).startsWith(setterPrefix));
        setterMethods.forEach(method -> assertThat(method.getReturnType()).isEqualTo(void.class));
    }

    @Test
    void isSetterReturnsTrueForBlankSetter() {
        String setterPrefix = "";
        List<Method> setterMethods = getDeclaredMethods(PojoWithCustomSetters.class, DEFAULT_POPULATE_CONFIG.getBlacklistedMethods()).stream()
                .filter(method -> isSetterMethod(method, setterPrefix))
                .collect(Collectors.toList());

        assertThat(setterMethods).isNotEmpty().hasSize(17);
        setterMethods.forEach(method -> assertThat(method.getName()).startsWith(setterPrefix));
        setterMethods.forEach(method -> assertThat(method.getReturnType()).isEqualTo(void.class));
    }

    @Test
    void isBlackListedMethodReturnsTrue() {
        Method method = getMethod("$jacocoInit", HasBlackListed.class);

        assertThat(PopulateUtil.isBlackListed(method, DEFAULT_POPULATE_CONFIG.getBlacklistedMethods())).isTrue();
    }

    @Test
    void isBlackListedMethodReturnsFalse() {
        Method method = getMethod("getStringValue", Pojo.class);

        assertThat(PopulateUtil.isBlackListed(method, DEFAULT_POPULATE_CONFIG.getBlacklistedMethods())).isFalse();
    }

    @Test
    void isBlackListedFieldsReturnsTrue() {
        Field field = getField("__$lineHits$__", HasBlackListed.class);

        assertThat(isBlackListed(field, DEFAULT_POPULATE_CONFIG.getBlacklistedFields())).isTrue();
    }

    @Test
    void isBlackListedFieldReturnsFalse() {
        Field field = getField("stringValue", Pojo.class);

        assertThat(isBlackListed(field, DEFAULT_POPULATE_CONFIG.getBlacklistedFields())).isFalse();
    }

    private static Parameter getParameter() {
        return Arrays.stream(getLargestConstructor(AllArgsConstructor.class, false).getParameters())
                .filter(p -> p.getType().equals(Set.class))
                .findFirst()
                .orElseThrow();
    }

    private static Type[] getTypeArguments() {
        try {
            Type[] typeArguments = ((ParameterizedType) Pojo.class.getDeclaredField("setOfStrings")
                    .getGenericType()).getActualTypeArguments();
            assertThat(typeArguments).hasSize(1);
            return typeArguments;
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}
