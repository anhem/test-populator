package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.MethodType;
import com.github.anhem.testpopulator.config.OverridePopulate;
import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.exception.PopulateException;
import com.github.anhem.testpopulator.model.java.constructor.AllArgsConstructorExtendsAllArgsConstructorAbstract;
import com.github.anhem.testpopulator.model.java.field.Fields;
import com.github.anhem.testpopulator.model.java.mutator.MutatorWithConstructor;
import com.github.anhem.testpopulator.model.java.setter.Pojo;
import com.github.anhem.testpopulator.model.java.setter.PojoPrivateConstructor;
import com.github.anhem.testpopulator.model.java.setter.PojoWithMultipleCustomSetters;
import com.github.anhem.testpopulator.model.java.stc.MultipleStaticMethods;
import com.github.anhem.testpopulator.model.java.stc.Users;
import com.github.anhem.testpopulator.model.lombok.LombokImmutableExtendsLombokAbstractImmutable;
import com.github.anhem.testpopulator.readme.model.MyClass2;
import com.github.anhem.testpopulator.readme.model.MyUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.github.anhem.testpopulator.config.BuilderPattern.LOMBOK;
import static com.github.anhem.testpopulator.config.ConstructorType.LARGEST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PopulateFactoryTest {

    private static final List<Class<?>> CLASSES = List.of(
            PojoWithMultipleCustomSetters.class, //SETTER
            AllArgsConstructorExtendsAllArgsConstructorAbstract.class, //CONSTRUCTOR
            LombokImmutableExtendsLombokAbstractImmutable.class, //BUILDER
            MutatorWithConstructor.class, //MUTATOR
            MyClass2.class, //CONSTRUCTOR
            Fields.class, //FIELD
            Users.class, //STATIC_METHOD
            MyUUID.class, //override populate
            MultipleStaticMethods.class //STATIC_METHOD
    );

    @Test
    void canCreatePopulateFactoryWithoutDefaultConfiguration() {
        assertThat(new PopulateFactory()).isNotNull();
    }

    @Test
    void fullyConfiguredPopulateFactoryCanPopulateAMixOfClassesWithAccessNonPublicConstructors() {
        PopulateConfig populateConfig = createFullyConfiguredPopulateConfig();
        PopulateFactory populateFactory = new PopulateFactory(populateConfig);

        assertThat(populateFactory.populate(String.class)).isNotNull();
        assertThat(populateFactory.populate(Instant.class)).isNotNull();
        CLASSES.forEach(clazz -> assertPopulatedObject(populateFactory.populate(clazz)));
        assertPopulatedObject(populateFactory.populate(PojoPrivateConstructor.class));
    }

    @Test
    void fullyConfiguredPopulateFactoryCanPopulateAMixOfClasses() {
        PopulateConfig populateConfig = createFullyConfiguredPopulateConfig().toBuilder()
                .accessNonPublicConstructors(false)
                .build();
        PopulateFactory populateFactory = new PopulateFactory(populateConfig);

        assertThat(populateFactory.populate(String.class)).isNotNull();
        assertThat(populateFactory.populate(Instant.class)).isNotNull();
        CLASSES.forEach(clazz -> assertPopulatedObject(populateFactory.populate(clazz)));
        assertThatThrownBy(() -> populateFactory.populate(PojoPrivateConstructor.class))
                .isInstanceOf(PopulateException.class)
                .hasMessageContaining("No matching strategy found");
    }

    @Test
    void canPopulateWithMapOverrides() {
        PopulateFactory populateFactory = new PopulateFactory();
        String overrideValue = "overridden";
        Map<Class<?>, OverridePopulate<?>> overrides = Map.of(String.class, () -> overrideValue);

        String result = populateFactory.populate(String.class, overrides);

        assertThat(result).isEqualTo(overrideValue);
    }

    @Test
    void canPopulateWithClassOverride() {
        PopulateFactory populateFactory = new PopulateFactory();
        String overrideValue = "overridden";

        String result = populateFactory.populate(String.class, String.class, () -> overrideValue);

        assertThat(result).isEqualTo(overrideValue);
    }

    @Test
    void canPopulateWithNameAndClassOverride() {
        PopulateFactory populateFactory = new PopulateFactory();
        String overrideValue = "overridden";

        Pojo pojo = populateFactory.populate(Pojo.class, "setStringValue", String.class, () -> overrideValue);

        assertThat(pojo.getStringValue()).isEqualTo(overrideValue);
    }

    private static PopulateConfig createFullyConfiguredPopulateConfig() {
        return PopulateConfig.builder()
                .builderStrategy()
                .pattern(LOMBOK)
                .and()
                .setterStrategy()
                .setPrefixes("")
                .and()
                .mutatorStrategy()
                .constructorType(LARGEST)
                .and()
                .constructorStrategy()
                .and()
                .staticMethodStrategy()
                .methodType(MethodType.SIMPLEST)
                .and()
                .fieldStrategy()
                .and()
                .randomValues(true)
                .accessNonPublicConstructors(true)
                .addOverride(MyUUID.class, () -> new MyUUID(UUID.randomUUID().toString()))
                .objectFactoryEnabled(false)
                .nullOnCircularDependency(true)
                .build();
    }

    private <T> void assertPopulatedObject(T object) {
        assertThat(object).isNotNull();
        assertThat(object).hasNoNullFieldsOrProperties();
        assertThat(object).usingRecursiveAssertion().hasNoNullFields();
    }
}
