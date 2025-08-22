package com.github.anhem.testpopulator.internal.util;

import com.github.anhem.testpopulator.config.BuilderPattern;
import com.github.anhem.testpopulator.internal.carrier.Carrier;
import com.github.anhem.testpopulator.internal.carrier.ClassCarrier;
import com.github.anhem.testpopulator.internal.carrier.CollectionCarrier;
import com.github.anhem.testpopulator.internal.object.ObjectFactoryVoid;
import com.github.anhem.testpopulator.model.circular.A;
import com.github.anhem.testpopulator.model.custombuilder.CustomBuilder;
import com.github.anhem.testpopulator.model.java.HasBlackListed;
import com.github.anhem.testpopulator.model.java.constructor.AllArgsConstructor;
import com.github.anhem.testpopulator.model.java.constructor.AllArgsConstructorExtendsAllArgsConstructorAbstract;
import com.github.anhem.testpopulator.model.java.constructor.AllArgsConstructorPrivate;
import com.github.anhem.testpopulator.model.java.setter.Pojo;
import com.github.anhem.testpopulator.model.java.setter.PojoExtendsPojoAbstract;
import com.github.anhem.testpopulator.model.java.setter.PojoExtendsPojoExtendsPojoAbstract;
import com.github.anhem.testpopulator.model.java.setter.PojoPrivateConstructor;
import com.github.anhem.testpopulator.model.lombok.LombokImmutable;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.anhem.testpopulator.config.PopulateConfig.DEFAULT_BUILDER_METHOD;
import static com.github.anhem.testpopulator.config.Strategy.*;
import static com.github.anhem.testpopulator.internal.util.PopulateUtil.*;
import static com.github.anhem.testpopulator.testutil.FieldTestUtil.getField;
import static com.github.anhem.testpopulator.testutil.MethodTestUtil.getMethod;
import static com.github.anhem.testpopulator.testutil.PopulateConfigTestUtil.DEFAULT_POPULATE_CONFIG;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

class PopulateUtilTest {

    @Test
    void toArgumentTypesReturnsParameterArgumentTypes() {
        List<Type> argumentTypes = toArgumentTypes(getArbitraryParameter());

        assertThat(argumentTypes).hasSize(1);
        assertThat(argumentTypes.get(0)).isEqualTo(String.class);
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
    void isMapEntryReturnsFalse() {
        assertThat(isMapEntry(String.class)).isFalse();
    }

    @Test
    void isMapEntryReturnsTrue() {
        assertThat(isMapEntry(Map.Entry.class)).isTrue();
    }

    @Test
    void isCollectionLikeReturnsFalse() {
        assertThat(isCollectionLike(PojoExtendsPojoAbstract.class)).isFalse();
        assertThat(isCollectionLike(String.class)).isFalse();
    }

    @Test
    void isCollectionLikeReturnsTrue() {
        assertThat(isCollectionLike(List.class)).isTrue();
        assertThat(isCollectionLike(Set.class)).isTrue();
        assertThat(isCollectionLike(Collection.class)).isTrue();
        assertThat(isCollectionLike(Map.class)).isTrue();
        assertThat(isCollectionLike(ArrayList.class)).isTrue();
        assertThat(isCollectionLike(Map.Entry.class)).isTrue();
    }

    @Test
    void isCollectionLikeCarrierReturnsFalse() {
        assertThat(isCollectionCarrier(createClassCarrier())).isFalse();
    }

    @Test
    void isCollectionLikeCarrierReturnsTrue() {
        assertThat(isCollectionCarrier(createCollectionCarrier(String.class))).isTrue();
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
        assertThat(hasAtLeastOneParameter(getArbitraryMethod())).isFalse();
    }

    @Test
    void hasAtLeastOneParameterReturnsTrue() {
        assertThat(hasAtLeastOneParameter(getMethod("setStringValue", Pojo.class))).isTrue();
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
        assertThat(isMatchingFieldStrategy(FIELD, PojoExtendsPojoAbstract.class, false)).isTrue();
        assertThat(isMatchingFieldStrategy(FIELD, PojoPrivateConstructor.class, true)).isTrue();
    }

    @Test
    void isMatchingFieldStrategyReturnsFalse() {
        assertThat(isMatchingFieldStrategy(CONSTRUCTOR, PojoExtendsPojoAbstract.class, false)).isFalse();
        assertThat(isMatchingFieldStrategy(FIELD, AllArgsConstructorExtendsAllArgsConstructorAbstract.class, false)).isFalse();
        assertThat(isMatchingFieldStrategy(FIELD, PojoPrivateConstructor.class, false)).isFalse();
    }

    @Test
    void isMatchingBuilderStrategyReturnsTrue() {
        assertThat(isMatchingBuilderStrategy(BUILDER, LombokImmutable.class, BuilderPattern.LOMBOK, DEFAULT_BUILDER_METHOD)).isTrue();
    }

    @Test
    void isMatchingBuilderStrategyReturnsFalse() {
        assertThat(isMatchingBuilderStrategy(CONSTRUCTOR, LombokImmutable.class, BuilderPattern.LOMBOK, DEFAULT_BUILDER_METHOD)).isFalse();
        assertThat(isMatchingBuilderStrategy(BUILDER, PojoExtendsPojoAbstract.class, BuilderPattern.LOMBOK, DEFAULT_BUILDER_METHOD)).isFalse();
    }

    @Test
    void getMethodsForCustomBuilderReturnsMethods() {
        assertThat(getMethodsForCustomBuilder(CustomBuilder.CustomBuilderBuilder.class, emptyList())).hasSize(7);
    }

    @Test
    void isBlackListedMethodReturnsTrue() {
        Method method = getMethod("$jacocoInit", HasBlackListed.class);

        Assertions.assertThat(PopulateUtil.isBlackListed(method, DEFAULT_POPULATE_CONFIG.getBlacklistedMethods())).isTrue();
    }

    @Test
    void isBlackListedMethodReturnsFalse() {
        assertThat(PopulateUtil.isBlackListed(getArbitraryMethod(), DEFAULT_POPULATE_CONFIG.getBlacklistedMethods())).isFalse();
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

    @Test
    void hasConstructorsReturnsTrue() {
        assertThat(hasConstructors(new CollectionCarrier<>(HashMap.class, getArbitraryParameter(), new ObjectFactoryVoid(), new ArrayList<>()))).isTrue();
    }

    @Test
    void hasConstructorsReturnsFalse() {
        assertThat(hasConstructors(new CollectionCarrier<>(Map.class, getArbitraryParameter(), new ObjectFactoryVoid(), new ArrayList<>()))).isFalse();
    }

    @Test
    void alreadyVisitedReturnsTrueWhenClassHasBeenVisited() {
        ClassCarrier<A> classCarrier = ClassCarrier.initialize(A.class, new ObjectFactoryVoid());

        assertThat(alreadyVisited(classCarrier, true)).isFalse();

        classCarrier = classCarrier.toClassCarrier(A.class);

        assertThat(alreadyVisited(classCarrier, true)).isTrue();
    }

    @Test
    void alreadyVisitedReturnsFalseWhenNullOnCircularDependencyIsFalse() {
        ClassCarrier<A> classCarrier = ClassCarrier.initialize(A.class, new ObjectFactoryVoid());

        assertThat(alreadyVisited(classCarrier, true)).isFalse();

        classCarrier = classCarrier.toClassCarrier(A.class);

        assertThat(alreadyVisited(classCarrier, false)).isFalse();
    }

    @Test
    void alreadyVisitedReturnsFalseWhenBaseJavaClass() {
        ClassCarrier<String> classCarrier = ClassCarrier.initialize(String.class, new ObjectFactoryVoid());

        assertThat(alreadyVisited(classCarrier, true)).isFalse();

        classCarrier = classCarrier.toClassCarrier(String.class);

        assertThat(alreadyVisited(classCarrier, true)).isFalse();
    }

    private static ClassCarrier<String> createClassCarrier() {
        return Carrier.initialize(String.class, new ObjectFactoryVoid());
    }

    private static <T> CollectionCarrier<T> createCollectionCarrier(Class<T> clazz) {
        return new CollectionCarrier<>(clazz, getArbitraryParameter(), new ObjectFactoryVoid(), new ArrayList<>());
    }

    private static Method getArbitraryMethod() {
        return getMethod("getStringValue", Pojo.class);
    }

    private static Parameter getArbitraryParameter() {
        return Arrays.stream(getLargestConstructor(AllArgsConstructor.class, false).getParameters())
                .filter(p -> p.getType().equals(Set.class))
                .findFirst()
                .orElseThrow();
    }
}
