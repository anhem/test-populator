package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.OverridePopulate;
import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.internal.object.ObjectFactory;
import com.github.anhem.testpopulator.internal.object.ObjectFactoryImpl;
import com.github.anhem.testpopulator.internal.object.ObjectFactoryVoid;
import com.github.anhem.testpopulator.internal.populate.Populator;
import com.github.anhem.testpopulator.internal.value.ValueFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
        this.populator = new Populator(createValueFactory(populateConfig));
    }

    /**
     * Call to create a fully populated object from a class
     *
     * @param clazz Class that should be populated
     * @return object of clazz
     */
    public <T> T populate(Class<T> clazz) {
        return populate(clazz, Collections.emptyMap());
    }

    /**
     * Call to create a fully populated object from a class with additional overrides for this execution
     *
     * @param clazz     Class that should be populated
     * @param overrides additional overrides that take precedence over overrides in PopulateConfig. Can be Map&lt;Class, OverridePopulate&gt; or Map&lt;String, OverridePopulate&gt;
     * @param <T>       type of object to return
     * @return object of clazz
     */
    public <T> T populate(Class<T> clazz, Map<?, OverridePopulate<?>> overrides) {
        Map<Class<?>, OverridePopulate<?>> classOverrides = new HashMap<>();
        Map<String, OverridePopulate<?>> nameOverrides = new HashMap<>();
        overrides.forEach((k, v) -> {
            if (k instanceof Class) {
                classOverrides.put((Class<?>) k, v);
            } else {
                nameOverrides.put(k.toString(), v);
            }
        });
        return populate(clazz, classOverrides, nameOverrides);
    }

    /**
     * Call to create a fully populated object from a class with additional overrides for this execution
     *
     * @param clazz                  Class that should be populated
     * @param overridePopulates      additional overrides that take precedence over overrides in PopulateConfig
     * @param overridePopulateNames additional overrides that take precedence over overrides in PopulateConfig
     * @param <T>                    type of object to return
     * @return object of clazz
     */
    public <T> T populate(Class<T> clazz, Map<Class<?>, OverridePopulate<?>> overridePopulates, Map<String, OverridePopulate<?>> overridePopulateNames) {
        boolean noOverrides = overridePopulates.isEmpty() && overridePopulateNames.isEmpty();
        PopulateConfig config = noOverrides ? populateConfig : populateConfig.toBuilder()
                .addOverridePopulates(overridePopulates)
                .addOverridePopulateNames(overridePopulateNames)
                .build();
        Populator p = noOverrides ? populator : new Populator(createValueFactory(config));
        ObjectFactory objectFactory = config.isObjectFactoryEnabled() ? new ObjectFactoryImpl(config) : new ObjectFactoryVoid();
        T result = p.populate(initialize(clazz, objectFactory, config));
        objectFactory.writeToFile();
        return result;
    }

    /**
     * Call to create a fully populated object from a class with an additional override for this execution
     *
     * @param clazz            Class that should be populated
     * @param overrideClass    class to override
     * @param overridePopulate implementation to use
     * @param <T>              type of object to return
     * @param <U>              type of class to override
     * @return object of clazz
     */
    public <T, U> T populate(Class<T> clazz, Class<U> overrideClass, OverridePopulate<U> overridePopulate) {
        return populate(clazz, Map.of(overrideClass, overridePopulate));
    }

    /**
     * Call to create a fully populated object from a class with an additional override for this execution
     *
     * @param clazz            Class that should be populated
     * @param overrideName     name to override
     * @param overridePopulate implementation to use
     * @param <T>              type of object to return
     * @return object of clazz
     */
    public <T> T populate(Class<T> clazz, String overrideName, OverridePopulate<?> overridePopulate) {
        return populate(clazz, Collections.emptyMap(), Map.of(overrideName, overridePopulate));
    }

    private static ValueFactory createValueFactory(PopulateConfig populateConfig) {
        return new ValueFactory(
                populateConfig.useRandomValues(),
                populateConfig.getOverridePopulate(),
                populateConfig.getOverridePopulateNames(),
                populateConfig.getBuilderPattern()
        );
    }
}
