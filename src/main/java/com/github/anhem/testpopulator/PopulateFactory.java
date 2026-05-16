package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.OverridePopulate;
import com.github.anhem.testpopulator.config.OverrideTarget;
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
        return populate(clazz, Collections.emptyMap(), Collections.emptyMap());
    }

    /**
     * Call to create a fully populated object from a class with additional overrides for this execution.
     * The map keys can be of type {@link Class} or {@link OverrideTarget}.
     *
     * @param clazz     Class that should be populated
     * @param overrides additional overrides that take precedence over overrides in PopulateConfig
     * @param <T>       type of object to return
     * @return object of clazz
     */
    public <T> T populate(Class<T> clazz, Map<?, OverridePopulate<?>> overrides) {
        Map<Class<?>, OverridePopulate<?>> classOverrides = new HashMap<>();
        Map<OverrideTarget, OverridePopulate<?>> nameOverrides = new HashMap<>();
        overrides.forEach((k, v) -> {
            if (k instanceof Class) {
                classOverrides.put((Class<?>) k, v);
            } else if (k instanceof OverrideTarget) {
                nameOverrides.put((OverrideTarget) k, v);
            }
        });
        return populate(clazz, classOverrides, nameOverrides);
    }

    /**
     * Call to create a fully populated object from a class with additional overrides for this execution.
     * Overrides are provided as alternating key-value pairs (key, value, key, value...).
     * Keys can be of type {@link Class} or {@link OverrideTarget}.
     * Values must be of type {@link OverridePopulate}.
     *
     * @param clazz     Class that should be populated
     * @param overrides alternating key-value pairs of overrides
     * @param <T>       type of object to return
     * @return object of clazz
     */
    public <T> T populate(Class<T> clazz, Object... overrides) {
        if (overrides.length % 2 != 0) {
            throw new IllegalArgumentException("Overrides must be provided as alternating key-value pairs (key, value, key, value...)");
        }
        Map<Object, OverridePopulate<?>> overridesMap = new HashMap<>();
        for (int i = 0; i < overrides.length; i += 2) {
            overridesMap.put(overrides[i], (OverridePopulate<?>) overrides[i + 1]);
        }
        return populate(clazz, overridesMap);
    }

    /**
     * Call to create a fully populated object from a class with additional overrides for this execution
     *
     * @param clazz          Class that should be populated
     * @param classOverrides additional overrides that take precedence over overrides in PopulateConfig
     * @param nameOverrides  additional overrides that take precedence over overrides in PopulateConfig
     * @param <T>            type of object to return
     * @return object of clazz
     */
    public <T> T populate(Class<T> clazz, Map<Class<?>, OverridePopulate<?>> classOverrides, Map<OverrideTarget, OverridePopulate<?>> nameOverrides) {
        boolean noOverrides = classOverrides.isEmpty() && nameOverrides.isEmpty();
        PopulateConfig config = noOverrides ? populateConfig : populateConfig.toBuilder()
                .addClassOverrides(classOverrides)
                .addNameOverrides(nameOverrides)
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
        return populate(clazz, Map.of(overrideClass, overridePopulate), Collections.emptyMap());
    }

    /**
     * Call to create a fully populated object from a class with an additional override for this execution
     *
     * @param clazz            Class that should be populated
     * @param overrideName     name to override
     * @param overrideClass    class to override
     * @param overridePopulate implementation to use
     * @param <T>              type of object to return
     * @return object of clazz
     */
    public <T> T populate(Class<T> clazz, String overrideName, Class<?> overrideClass, OverridePopulate<?> overridePopulate) {
        return populate(clazz, Collections.emptyMap(), Map.of(OverrideTarget.of(overrideName, overrideClass), overridePopulate));
    }

    private static ValueFactory createValueFactory(PopulateConfig populateConfig) {
        return new ValueFactory(
                populateConfig.isRandomValues(),
                populateConfig.getClassOverrides(),
                populateConfig.getNameOverrides(),
                populateConfig.getBuilderPattern()
        );
    }
}
