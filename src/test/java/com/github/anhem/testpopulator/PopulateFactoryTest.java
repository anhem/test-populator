package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.model.java.constructor.AllArgsConstructorExtendsAllArgsConstructorAbstract;
import com.github.anhem.testpopulator.model.java.field.Fields;
import com.github.anhem.testpopulator.model.java.mutator.MutatorWithConstructor;
import com.github.anhem.testpopulator.model.java.setter.PojoPrivateConstructor;
import com.github.anhem.testpopulator.model.java.setter.PojoWithMultipleCustomSetters;
import com.github.anhem.testpopulator.model.kotlin.KotlinLikeClass;
import com.github.anhem.testpopulator.model.lombok.LombokImmutableExtendsLombokAbstractImmutable;
import com.github.anhem.testpopulator.readme.model.MyClass2;
import com.github.anhem.testpopulator.readme.model.MyUUID;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static com.github.anhem.testpopulator.config.BuilderPattern.LOMBOK;
import static com.github.anhem.testpopulator.config.ConstructorType.LARGEST;
import static com.github.anhem.testpopulator.config.Strategy.*;
import static org.assertj.core.api.Assertions.assertThat;

class PopulateFactoryTest {

    @Test
    void canCreatePopulateFactoryWithoutDefaultConfiguration() {
        PopulateFactory populateFactory = new PopulateFactory();

        assertThat(populateFactory).isNotNull();
    }

    @Test
    void fullyConfiguredPopulateFactoryCanPopulateAMixOfClasses() {
        PopulateConfig populateConfig = PopulateConfig.builder()
                .strategyOrder(List.of(BUILDER, SETTER, MUTATOR, CONSTRUCTOR, FIELD))
                .builderPattern(LOMBOK)
                .randomValues(true)
                .setterPrefix("")
                .accessNonPublicConstructors(true)
                .overridePopulate(MyUUID.class, () -> new MyUUID(UUID.randomUUID().toString()))
                .objectFactoryEnabled(false)
                .nullOnCircularDependency(true)
                .constructorType(LARGEST)
                .kotlinSupport(true)
                .build();
        PopulateFactory populateFactory = new PopulateFactory(populateConfig);

        assertThat(populateFactory.populate(String.class)).isNotNull();
        assertPopulatedObject(populateFactory.populate(PojoWithMultipleCustomSetters.class));
        assertPopulatedObject(populateFactory.populate(AllArgsConstructorExtendsAllArgsConstructorAbstract.class));
        assertPopulatedObject(populateFactory.populate(LombokImmutableExtendsLombokAbstractImmutable.class));
        assertPopulatedObject(populateFactory.populate(MutatorWithConstructor.class));
        assertPopulatedObject(populateFactory.populate(MyClass2.class));
        assertPopulatedObject(populateFactory.populate(PojoPrivateConstructor.class));
        assertPopulatedObject(populateFactory.populate(Fields.class));
        assertPopulatedObject(populateFactory.populate(KotlinLikeClass.class));
    }

    private <T> void assertPopulatedObject(T object) {
        assertThat(object).isNotNull();
        assertThat(object).hasNoNullFieldsOrProperties();
        assertThat(object).usingRecursiveAssertion().hasNoNullFields();
    }


}
