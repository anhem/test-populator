package com.github.anhem.testpopulator.internal.populate;

import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.internal.carrier.ClassCarrier;
import com.github.anhem.testpopulator.internal.carrier.CollectionCarrier;
import com.github.anhem.testpopulator.internal.object.ObjectFactoryVoid;
import com.github.anhem.testpopulator.internal.value.ValueFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class ArrayPopulatorTest {

    private ArrayPopulator arrayPopulator;
    private Populator populator;

    @BeforeEach
    void setUp() {
        arrayPopulator = new ArrayPopulator();
        PopulateConfig config = PopulateConfig.builder().build();
        ValueFactory valueFactory = new ValueFactory(
                config.isRandomValues(),
                config.getClassOverrides(),
                config.getNameOverrides(),
                config.getBuilderPattern()
        );
        populator = new Populator(valueFactory);
    }

    @Test
    void populateWithClassCarrier() {
        Class<String[]> clazz = String[].class;
        ClassCarrier<String[]> classCarrier = new ClassCarrier<>(clazz, new ObjectFactoryVoid(), Collections.emptyList(), PopulateConfig.builder().build());

        String[] result = arrayPopulator.populate(classCarrier, populator);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result[0]).isInstanceOf(String.class);
    }

    @Test
    void populateWithCollectionCarrier() {
        Class<String[]> clazz = String[].class;
        Type[] typeArguments = new Type[]{String.class};
        CollectionCarrier<String[]> collectionCarrier = new CollectionCarrier<>(clazz, typeArguments, new ObjectFactoryVoid(), Collections.emptyList(), PopulateConfig.builder().build());

        String[] result = arrayPopulator.populate(collectionCarrier, populator);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result[0]).isInstanceOf(String.class);
    }

    @Test
    void populateWithPrimitiveArray() {
        Class<int[]> clazz = int[].class;
        ClassCarrier<int[]> classCarrier = new ClassCarrier<>(clazz, new ObjectFactoryVoid(), Collections.emptyList(), PopulateConfig.builder().build());

        int[] result = arrayPopulator.populate(classCarrier, populator);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
    }
}
