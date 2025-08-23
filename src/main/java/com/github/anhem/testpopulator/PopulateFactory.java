package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.internal.Populator;
import com.github.anhem.testpopulator.internal.object.ObjectFactory;
import com.github.anhem.testpopulator.internal.object.ObjectFactoryImpl;
import com.github.anhem.testpopulator.internal.object.ObjectFactoryVoid;
import com.github.anhem.testpopulator.internal.value.ValueFactory;

import static com.github.anhem.testpopulator.internal.carrier.CollectionCarrier.initialize;

/**
 * Factory for creating populated objects from classes
 */
public class PopulateFactory {

    private final PopulateConfig populateConfig;
    private final Populator populator;

    /**
     * Create new instance of PopulateFactory with default configuration
     */
    public PopulateFactory() {
        this(PopulateConfig.builder().build());
    }

    /**
     * Create new instance of PopulateFactory
     *
     * @param populateConfig configuration properties for PopulateFactory
     */
    public PopulateFactory(PopulateConfig populateConfig) {
        this.populateConfig = populateConfig;
        this.populator = new Populator(populateConfig, createValueFactory(populateConfig));
    }

    /**
     * Call to create a fully populated object from a class
     *
     * @param clazz Class that should be populated
     * @return object of clazz
     */
    public <T> T populate(Class<T> clazz) {
        ObjectFactory objectFactory = populateConfig.isObjectFactoryEnabled() ? new ObjectFactoryImpl(populateConfig) : new ObjectFactoryVoid();
        T result = populator.populateWithOverrides(initialize(clazz, objectFactory, populateConfig));
        objectFactory.writeToFile();
        return result;
    }

    private static ValueFactory createValueFactory(PopulateConfig populateConfig) {
        return new ValueFactory(
                populateConfig.useRandomValues(),
                populateConfig.getOverridePopulate(),
                populateConfig.getBuilderPattern()
        );
    }
}
