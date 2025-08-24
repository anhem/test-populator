package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.MethodType;
import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.exception.PopulateException;
import com.github.anhem.testpopulator.model.java.constructor.AllArgsConstructorExtendsAllArgsConstructorAbstract;
import com.github.anhem.testpopulator.model.java.field.Fields;
import com.github.anhem.testpopulator.model.java.mutator.MutatorWithConstructor;
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
import java.util.UUID;

import static com.github.anhem.testpopulator.config.BuilderPattern.LOMBOK;
import static com.github.anhem.testpopulator.config.ConstructorType.LARGEST;
import static com.github.anhem.testpopulator.config.Strategy.*;
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

    private static PopulateConfig createFullyConfiguredPopulateConfig() {
        return PopulateConfig.builder()
                .strategyOrder(List.of(BUILDER, SETTER, MUTATOR, CONSTRUCTOR, STATIC_METHOD, FIELD))
                .builderPattern(LOMBOK)
                .randomValues(true)
                .addSetterPrefix("")
                .accessNonPublicConstructors(true)
                .overridePopulate(MyUUID.class, () -> new MyUUID(UUID.randomUUID().toString()))
                .objectFactoryEnabled(false)
                .nullOnCircularDependency(true)
                .constructorType(LARGEST)
                .methodType(MethodType.SIMPLEST)
                .build();
    }

    private <T> void assertPopulatedObject(T object) {
        assertThat(object).isNotNull();
        assertThat(object).hasNoNullFieldsOrProperties();
        assertThat(object).usingRecursiveAssertion().hasNoNullFields();
    }
}
